/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itsaky.androidide.androidtreesitter

import android.R.layout
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itsaky.androidide.androidtreesitter.databinding.ActivityMainBinding
import com.itsaky.androidide.androidtreesitter.databinding.ContentMainBinding
import com.itsaky.androidide.androidtreesitter.databinding.LayoutTextInputBinding
import com.itsaky.androidide.treesitter.TSLanguage
import com.itsaky.androidide.treesitter.TSLanguageCache
import com.itsaky.androidide.treesitter.TSParser
import com.itsaky.androidide.treesitter.TSTreeCursor
import com.itsaky.androidide.treesitter.TreeSitter
import com.itsaky.androidide.treesitter.java.TSLanguageJava
import com.itsaky.androidide.treesitter.json.TSLanguageJson
import com.itsaky.androidide.treesitter.kotlin.TSLanguageKotlin
import com.itsaky.androidide.treesitter.log.TSLanguageLog
import com.itsaky.androidide.treesitter.python.TSLanguagePython
import com.itsaky.androidide.treesitter.string.UTF16StringFactory
import com.itsaky.androidide.treesitter.xml.TSLanguageXml
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Locale
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Akash Yadav
 */
class MainActivity : AppCompatActivity() {

  private var _content: ContentMainBinding? = null

  private val content: ContentMainBinding
    get() = checkNotNull(_content) {
      "Binding has been released"
    }

  private val activityScope =
    CoroutineScope(Dispatchers.Default + CoroutineName("MainActivity"))

  private var parser: TSParser? = TSParser.create()
  private var parseJob: Job? = null

  private var parseCount = 0L
  private var avgParse = 0L

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    _content = binding.content

    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    // noinspection SetTextI18n
    content.tsMeta.text =
      "Tree Sitter Language Version: " + TreeSitter.getLanguageVersion()

    loadLanguages()

    // new String(byte[], String) is not supported on Android)
    // so we use ByteBuffer to decode the string
    // Test working of UTF16String.toString() on Android
    UTF16StringFactory.newString("android-tree-sitter UTF16String").use {
      Log.d("MainActivity", "UTF16Str: $it")
    }

    content.code.addTextChangedListener(object : Watcher() {
      override fun afterTextChanged(editable: Editable) {
        afterInputChanged(editable)
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.reload_external -> {
        destroy()
        loadLanguages()
      }

      R.id.test_performance -> {
        askIterCount { doPerfTest(it) }
      }
    }
    return true
  }

  private fun askIterCount(onResult: (Int) -> Unit) {
    val binding = LayoutTextInputBinding.inflate(layoutInflater)
    binding.input.setText(DEF_ITER.toString())
    MaterialAlertDialogBuilder(this)
      .setTitle("Iterations")
      .setView(binding.root)
      .setPositiveButton(android.R.string.ok) { diag, _ ->
        diag.dismiss()
        onResult(binding.input.text!!.toString().toInt())
      }
      .setNegativeButton(android.R.string.cancel, null)
      .show()
  }

  @Suppress("DEPRECATION")
  private fun doPerfTest(iterations: Int) {
    val language = TSLanguageJava.getInstance()

    val progress = ProgressDialog(this)
    progress.setMessage("Please wait")
    progress.show()

    activityScope.launch(Dispatchers.IO) {

      var lineCount = 0L
      assets.open("View.java.txt").use { asset ->
        val str = UTF16StringFactory.newString()
        asset.bufferedReader().use { reader ->
          reader.forEachLine { line ->
            str.append(line)
            str.append("\n")
            ++lineCount
          }
        }

        str.synchronizedString()
      }.use { input ->

        val size = input.byteLength()

        val pd = withContext(Dispatchers.Main) {
          val pd = ProgressDialog(this@MainActivity)
          pd.setTitle(R.string.test_performance)
          pd.setCancelable(false)
          pd.setMessage("""
            Language: ${language.name}
            Iterations: $iterations
            File size: ${String.format("%.2f", size.toDouble() / 1024)} KB
            File line count: $lineCount
          """.trimIndent())
          pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
          pd.max = iterations

          progress.dismiss()
          pd.show()

          pd
        }

        val iter = AtomicInteger(0)
        val flow = flow {
          repeat(iterations) {
            emit(async {
              TSParser.create().use { parser ->
                parser.language = language
                val start = System.currentTimeMillis()
                parser.parseString(input)
                  .use { /* auto-close the returned tree */ }
                val duration = System.currentTimeMillis() - start
                parser.close()

                withContext(Dispatchers.Main) {
                  pd.progress = iter.incrementAndGet()
                }

                duration
              }
            })
          }
        }

        val totalDuration = flow.toList().awaitAll().sum()

        withContext(Dispatchers.Main) {
          pd.dismiss()
          showPerfTestResult(language.name, iterations, size, lineCount,
            totalDuration)
        }
      }
    }
  }

  @SuppressLint("SetTextI18n")
  private fun showPerfTestResult(name: String, iterations: Int, size: Int,
                                 lineCount: Long, totalDuration: Long
  ) {
    val dialog = MaterialAlertDialogBuilder(this)
    dialog.setPositiveButton(android.R.string.ok, null)
    dialog.setTitle("Performance results")
    dialog.setMessage("""
      Language : $name
      Iterations : $iterations
      File size : ${String.format("%.2f", size.toDouble() / 1024)} KB
      File line count: $lineCount
      Total duration: ${totalDuration}ms
      Average duration : ${totalDuration / iterations}ms
    """.trimIndent())
    dialog.setCancelable(false)
    dialog.show()
  }

  private fun loadLanguages() {
    languageMap["C"] = TSLanguage.loadLanguage(this, "c")

    content.languageChooser.adapter =
      ArrayAdapter(this, layout.simple_list_item_1,
        languageMap.keys.toTypedArray())
  }

  override fun onDestroy() {
    super.onDestroy()
    destroy()
  }

  private fun destroy() {
    TSLanguageCache.closeExternal()
    parser?.requestCancellationAsync()
    parseJob?.cancel(CancellationException("Activity is being destroyed"))

    parser = null
    parseJob = null
  }

  private fun afterInputChanged(editable: Editable) {
    val language = languageMap[content.languageChooser.selectedItem as String]

    val currentJob = this.parseJob

    this.parseJob = activityScope.launch {
      if (parser?.isParsing == true) {
        parser?.requestCancellationAndWait()
      }

      currentJob?.cancel(
        CancellationException("Another parse has been requested"))

      val parser = this@MainActivity.parser ?: TSParser.create()
        .also { this@MainActivity.parser }

      val toParse = editable.toString()

      val start = System.currentTimeMillis()
      parser.language = language!!
      parser.reset()
      val parseStart = System.currentTimeMillis()

      parser.parseString(toParse).use { tree ->
        val parseEnd = System.currentTimeMillis()

        val sb = StringBuilder()
        val setupDur = parseStart - start
        val parseDur = parseEnd - parseStart

        avgParse += parseDur
        ++parseCount

        sb.append("Setup in ").append(setupDur).append("ms").append("\n")
        sb.append("Parsed in ").append(parseDur).append("ms").append("\n")
        sb.append("Total: ")
          .append(setupDur + parseDur)
          .append("ms")
          .append("\n")
        sb.append("Parse count: ").append(parseCount).append("\n")
        sb.append("Parse avg duration: ")
          .append(avgParse / parseCount)
          .append("\n")
        sb.append("Is success: ").append(tree != null).append("\n")
        Log.d("MainActivity", sb.toString())

        tree?.rootNode?.walk()?.use { cursor ->
          val insert = sb.length
          appendTo(sb, cursor, 0)
          sb.insert(insert, String.format(Locale.US, "Walked in %d ms\n",
            System.currentTimeMillis() - start))
          handleParseResult(sb)
        }
      }
    }.also {
      it.invokeOnCompletion { error ->
        if (error != null) {
          activityScope.launch(Dispatchers.Main.immediate) {
            handleParseResult(trace(error))
          }
        }
      }
    }
  }

  private suspend fun handleParseResult(result: CharSequence) {
    withContext(Dispatchers.Main.immediate) {
      val text = content.ast
      val codeView = content.code

      text.text = result
      codeView.isEnabled = true
      codeView.requestFocus()
    }
  }

  private fun appendTo(sb: StringBuilder, cursor: TSTreeCursor, indentLevel: Int
  ) {
    val node = cursor.currentNode
    sb.append("\n")
    sb.repeatKt(" ", indentLevel * 4)
    sb.append(node.type)
    sb.append("[${node.startByte shr 1}, ${node.endByte shr 1}]")

    if (node.namedChildCount != 0) {
      for (i in 0 until node.namedChildCount) {
        val child = node.getNamedChild(i)
        appendTo(sb, child.walk(), indentLevel + 1)
      }
    }

    while (cursor.gotoNextSibling()) {
      appendTo(sb, cursor, indentLevel)
    }
  }

  private fun trace(err: Throwable): String {
    val sw = StringWriter()
    err.printStackTrace(PrintWriter(sw))
    return sw.toString()
  }

  private abstract class Watcher : TextWatcher {

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int,
                                   i2: Int
    ) {
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int,
                               i2: Int
    ) {
    }
  }

  companion object {

    private const val DEF_ITER = 1000

    private val languageMap = hashMapOf<String, TSLanguage>()

    init {
      TreeSitter.loadLibrary()

      System.loadLibrary("tree-sitter-java")
      System.loadLibrary("tree-sitter-json")
      System.loadLibrary("tree-sitter-kotlin")
      System.loadLibrary("tree-sitter-log")
      System.loadLibrary("tree-sitter-python")
      System.loadLibrary("tree-sitter-xml")

      languageMap["Java"] = TSLanguageJava.getInstance()
      languageMap["JSON"] = TSLanguageJson.getInstance()
      languageMap["Kotlin"] = TSLanguageKotlin.getInstance()
      languageMap["Log"] = TSLanguageLog.getInstance()
      languageMap["Python"] = TSLanguagePython.getInstance()
      languageMap["XML"] = TSLanguageXml.getInstance()
    }

    private fun StringBuilder.repeatKt(text: String, indent: Int
    ): StringBuilder {
      for (i in 1..indent) append(text)
      return this
    }
  }
}