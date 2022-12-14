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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * @author Akash Yadav
 */
public class MainActivity extends AppCompatActivity {

  static {
    System.loadLibrary("ts");
  }

  private final TSLanguage[] langs = {TSLanguages.java(), TSLanguages.python()};
  private ActivityMainBinding binding;
  private ContentMainBinding content;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    content = binding.content;

    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolbar);

    Log.d("MainActivity", "post setContentView()");

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
