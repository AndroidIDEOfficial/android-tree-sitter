/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/\>.
 */

package com.itsaky.androidide.androidtreesitter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itsaky.androidide.androidtreesitter.databinding.ActivityMainBinding;
import com.itsaky.androidide.androidtreesitter.databinding.ContentMainBinding;
import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSParser;
import com.itsaky.androidide.treesitter.TSTreeCursor;
import com.itsaky.androidide.treesitter.string.UTF16String;
import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.itsaky.androidide.treesitter.python.TSLanguagePython;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * @author Akash Yadav
 */
public class MainActivity extends AppCompatActivity {

  static {
    System.loadLibrary("android-tree-sitter");
    System.loadLibrary("tree-sitter-java");
    System.loadLibrary("tree-sitter-python");
  }

  private final TSLanguage[] langs = {TSLanguageJava.newInstance(), TSLanguagePython.newInstance()};
  private ActivityMainBinding binding;
  private ContentMainBinding content;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    content = binding.content;

    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);

    // new String(byte[], String) is not supported on Android)
    // so we use ByteBuffer to decode the string
    // Test working of UTF16String.toString() on Android
    final var utf16String = UTF16StringFactory.newString("android-tree-sitter UTF16String");
    Log.d("MainActivity", "UTF16Str: " + utf16String);
    utf16String.close();

    content.code.addTextChangedListener(
        new Watcher() {
          @Override
          public void afterTextChanged(Editable editable) {
            afterInputChanged(editable);
          }
        });
  }

  private void afterInputChanged(Editable editable) {
    final var start = System.currentTimeMillis();
    try (final var parser = new TSParser()) {
      parser.setLanguage(langs[content.languageChooser.getSelectedItemPosition()]);
      try (final var tree = parser.parseString(editable.toString())) {
        try (final var cursor = tree.getRootNode().walk()) {
          final var duration = System.currentTimeMillis() - start;
          final var sb = new StringBuilder();
          sb.append("Parsed in ").append(duration).append("ms").append("\n\n");

          appendTo(sb, cursor, 0);

          content.ast.setText(sb);
        }
      } catch (Throwable err) {
        content.ast.setText(trace(err));
      }
    }
  }

  private void appendTo(@NonNull StringBuilder sb, @NonNull TSTreeCursor cursor, int indentLevel) {
    final var node = cursor.getCurrentNode();
    sb.append(node.getType());
    sb.append(
        String.format(Locale.getDefault(), "[%d, %d]", node.getStartByte(), node.getEndByte()));
    sb.append("\n");
    repeatSpaces(sb, indentLevel * 2);

    if (node.getNamedChildCount() != 0) {
      for (int i = 0; i < node.getNamedChildCount(); i++) {
        final var child = node.getNamedChild(i);
        appendTo(sb, child.walk(), indentLevel + 1);
      }
    }

    while (cursor.gotoNextSibling()) {
      appendTo(sb, cursor, indentLevel);
    }
  }

  @NonNull
  private String trace(@NonNull Throwable err) {
    final var sw = new StringWriter();
    err.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  private void repeatSpaces(StringBuilder sb, int count) {
    for (int i = 0; i < count; i++) {
      sb.append(" ");
    }
  }

  private abstract static class Watcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
  }
}