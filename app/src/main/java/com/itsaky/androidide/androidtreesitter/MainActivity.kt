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
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.itsaky.androidide.androidtreesitter.databinding.ActivityMainBinding
import com.itsaky.androidide.androidtreesitter.databinding.ContentMainBinding
import com.itsaky.androidide.treesitter.TSLanguage
import com.itsaky.androidide.treesitter.TSLanguageCache
import com.itsaky.androidide.treesitter.TSParser
import com.itsaky.androidide.treesitter.TSTreeCursor
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Locale
import java.util.concurrent.CancellationException

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

  private val parser = TSParser.create()
  private var parseJob: Job? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    _content = binding.content

    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    languageMap["C"] = TSLanguage.loadLanguage(this, "c")
    content.languageChooser.adapter =
      ArrayAdapter(this, layout.simple_list_item_1,
        languageMap.keys.toTypedArray())

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

  override fun onDestroy() {
    super.onDestroy()

    TSLanguageCache.closeExternal()
    parser.requestCancellationAsync()
    parseJob?.cancel(CancellationException("Activity is being destroyed"))
  }

  private fun afterInputChanged(editable: Editable) {
    val language = languageMap[content.languageChooser.selectedItem as String]
//    content.progress.visibility = View.VISIBLE
//    content.code.isEnabled = false

    val currentJob = this.parseJob

    this.parseJob = activityScope.launch {
      if (parser.isParsing) {
        parser.requestCancellationAndWait()
      }

      currentJob?.cancel(
        CancellationException("Another parse has been requested")
      )

      val start = System.currentTimeMillis()
      parser.reset()
      parser.language = language!!

      parser.parseString(editable.toString()).use { tree ->
        val duration = System.currentTimeMillis() - start
        tree.rootNode.walk().use { cursor ->
          val sb = StringBuilder()
          sb.append("Parsed in ").append(duration).append("ms").append("\n")
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
      val progress = content.progress
      val codeView = content.code

      text.text = result
      progress.visibility = View.GONE
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

    private val languageMap = hashMapOf<String, TSLanguage>()

    init {
      System.loadLibrary("android-tree-sitter")
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

    private fun StringBuilder.repeatKt(text: String, indent: Int) : StringBuilder {
      for (i in 1..indent) append(text)
      return this
    }
  }
}