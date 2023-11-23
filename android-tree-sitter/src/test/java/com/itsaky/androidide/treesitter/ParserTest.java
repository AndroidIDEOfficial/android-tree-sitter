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

package com.itsaky.androidide.treesitter;

import static com.google.common.truth.Truth.assertThat;
import static com.itsaky.androidide.treesitter.TestUtils.readString;

import android.text.TextUtils;
import com.itsaky.androidide.treesitter.aidl.TSLanguageAidl;
import com.itsaky.androidide.treesitter.java.TSLanguageJava;
import com.itsaky.androidide.treesitter.json.TSLanguageJson;
import com.itsaky.androidide.treesitter.kotlin.TSLanguageKotlin;
import com.itsaky.androidide.treesitter.log.TSLanguageLog;
import com.itsaky.androidide.treesitter.python.TSLanguagePython;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import static com.itsaky.androidide.treesitter.ResourceUtils.readResource;

@RunWith(RobolectricTestRunner.class)
public class ParserTest extends TreeSitterTest {

  private MockedStatic<TextUtils> mockedTextUtils;

  @Before
  public void setupMocks() {
    mockedTextUtils = Mockito.mockStatic(TextUtils.class);
    mockedTextUtils.when(() -> TextUtils.getTrimmedLength(ArgumentMatchers.anyString()))
      .thenAnswer(invocation -> {
        final var arguments = invocation.getArguments();
        if (arguments == null || arguments.length != 1 || !(arguments[0] instanceof CharSequence)) {
          throw new IllegalArgumentException();
        }
        return getTrimmedLength(((CharSequence) arguments[0]));
      });
  }

  @After
  public void releaseMocks() {
    if (mockedTextUtils != null) {
      mockedTextUtils.close();
    }
  }

  @Test
  public void testParse()  {
    try (TSParser parser = TSParser.create()) {
      parser.setLanguage(TSLanguagePython.getInstance());
      try (TSTree tree = parser.parseString("print(\"hi\")")) {
        assertThat(tree.getRootNode().getNodeString()).isEqualTo(
          "(module (expression_statement (call function: (identifier) arguments: (argument_list (string (string_start) (string_content) (string_end))))))");
      }
    }
  }

  @Test
  public void testCodeEditor() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      assertThat(parser.getLanguage().getNativeObject()).isEqualTo(
        TSLanguageJava.getInstance().getNativeObject());
      try (var tree = parser.parseString(readResource("CodeEditor.java.txt"))) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println(
          "\nParsed CodeEditor.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  @Test
  public void testView() throws Throwable {
    final long start = System.currentTimeMillis();
    try (TSParser parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      try (var tree = parser.parseString(readResource("View.java.txt"))) {
        System.out.println(tree.getRootNode().getNodeString());
        System.out.println("\nParsed View.java in: " + (System.currentTimeMillis() - start) + "ms");
      }
    }
  }

  @Test
  public void testTimeout() throws UnsupportedEncodingException {
    final var timeout = 1000L; // 1 millisecond
    final var start = System.currentTimeMillis();
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());
      parser.setTimeout(timeout);
      assertThat(parser.getTimeout()).isEqualTo(timeout);
      try (final var tree = parser.parseString(readResource("View.java.txt"))) {
        final var timeConsumed = System.currentTimeMillis() - start;
        assertThat(tree).isNull();
        System.out.println("Parsed in " + timeConsumed + "ms");
      }
    }
  }

  @Test
  public void testIncrementalParsing() throws UnsupportedEncodingException {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      // the start byte below is invalid as it is in the middle of a character
      // It should fail in this case
      parser.setIncludedRanges(
        new TSRange[]{TSRange.create(21, 65, TSPoint.create(0, 21), TSPoint.create(0, 65))});

      final var source = "public class Main { class Inner { public static void main() {} } }";
      try (final var tree = parser.parseString(source)) {
        assertThat(tree).isNotNull();

        final var root = tree.getRootNode();
        assertThat(root).isNotNull();

        // errorneous type
        assertThat(root.getChild(0).getType()).isEqualTo("ERROR");
      }
    }
  }

  @Test
  public void testJsonGrammar() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJson.getInstance());

      final var source = "{\n" + "    \"string\": \"value\",\n" + "    \"boolean\": true,\n" +
        "    \"number\": 1234,\n" + "    \"null\": null,\n" + "\n" + "    \"object\": {\n" + "\n" +
        "    },\n" + "\n" + "    \"array\": [\n" + "        \"array_element\"\n" + "    ]\n" + "}";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
      }
    }
  }

  @Test
  public void testKotlinGrammar() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageKotlin.getInstance());

      final var source =
        "class Main {\n" + "    fun main() {\n" + "        println(\"Hello World\")\n" + "    }\n" +
          "}";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.getNodeString()).isEqualTo(
          "(source_file (class_declaration name: (type_identifier) body: (class_body (function_declaration name: (simple_identifier) parameters: (function_value_parameters) body: (function_body (statements (call_expression (simple_identifier) (call_suffix (value_arguments (value_argument (string_literal)))))))))))");
      }
    }
  }

  @Test
  public void testLogGrammar_beginHeader() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageLog.getInstance());

      final var source = "--------- beginning of system";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.getNodeString()).isEqualTo("(logs (begin_header))");
      }
    }
  }

  @Test
  public void testLogGrammar_logcatLine() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageLog.getInstance());

      final var source = "06-16 17:32:54.289 19154 19154 W ziparchive: Unable to open '/data/app/~~S0ZGwshlag_3SKvS2AUm9g==/com.itsaky.androidide.logsender.sample-2mbpM5fpkBwx7otcNLWYNA==/base.dm': No such file or directory";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.getNodeString()).isEqualTo(
          "(logs (log_line (date) (time) (pid) (tid) (priority) (tag) (message)))");
      }
    }
  }

  @Test
  public void testLogGrammar_ideLogLine() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageLog.getInstance());

      final var source = "..aultApiVersionsRegistry I   Creating API versions table for platform dir: /data/data/com.itsaky.androidide/files/home/android-sdk/platforms/android-32";

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.getNodeString()).isEqualTo(
          "(logs (ide_log_line (ide_tag) (priority) (message)))");
      }
    }
  }

  @Test
  public void testAIDLGrammar_interfaceDecl() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageAidl.getInstance());

      final var source = readResource("IInterface.aidl");

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.hasErrors()).isFalse();

        //noinspection DataFlowIssue
        final var packages = substrings(
          execQueryGroupByCaptures("(package_declaration name: (_) @package)",
            TSLanguageAidl.getInstance(), rootNode).get("package"), source);
        assertThat(packages).containsExactly("com.itsaky.androidide.treesitter.test");

        //noinspection DataFlowIssue
        final var annotations = substrings(execQueryGroupByCaptures(
          "(annotation name: (_) @annotation) (marker_annotation name: (_) @annotation)",
          TSLanguageAidl.getInstance(), rootNode).get("annotation"), source);
        assertThat(annotations).containsExactly("SomeMarker", "Something",
          "SomethingWithArrayParam", "UnsupportedAppUsage", "OnAMethod", "OnAParam", "OnAParam",
          "CanBeMultiple", "MayHaveValues");

        //noinspection DataFlowIssue
        final var interfaces = substrings(
          execQueryGroupByCaptures("(interface_declaration name: (_) @interface)",
            TSLanguageAidl.getInstance(), rootNode).get("interface"), source);
        assertThat(interfaces).containsExactly("IInterface");

        //noinspection DataFlowIssue
        final var methods = substrings(
          execQueryGroupByCaptures("(method_declaration name: (_) @method)",
            TSLanguageAidl.getInstance(), rootNode).get("method"), source);
        assertThat(methods).containsExactly("notify", "fill", "thereIsOnlyOneWay");

        //noinspection DataFlowIssue
        final var variables = substrings(
          execQueryGroupByCaptures("(variable_declarator name: (_) @variable)",
            TSLanguageAidl.getInstance(), rootNode).get("variable"), source);
        assertThat(variables).containsExactly("somethingWeReceive", "somethingWeProvide",
          "somethingWeBothUse");
      }
    }
  }

  @Test
  public void testAIDLGrammar_parcelableDecl() {
    try (final var parser = TSParser.create()) {
      parser.setLanguage(TSLanguageAidl.getInstance());

      final var source = readResource("SomethingParcelable.aidl");

      try (final var tree = parser.parseString(source)) {
        final var rootNode = tree.getRootNode();
        assertThat(rootNode).isNotNull();
        assertThat(rootNode.getChildCount()).isGreaterThan(0);
        assertThat(rootNode.hasErrors()).isFalse();

        //noinspection DataFlowIssue
        final var interfaces = substrings(
          execQueryGroupByCaptures("(parcelable_declaration name: (_) @parcelable)",
            TSLanguageAidl.getInstance(), rootNode).get("parcelable"), source);
        assertThat(interfaces).containsExactly("SomethingDefinedSomewhere",
          "CanWeDefineAsManyAsWeWant", "SomethingParcelable");

        //noinspection DataFlowIssue
        final var variables = substrings(
          execQueryGroupByCaptures("(variable_declarator name: (_) @variable)",
            TSLanguageAidl.getInstance(), rootNode).get("variable"), source);
        assertThat(variables).containsExactly("hasField", "orFields", "isIt");
      }
    }
  }

  @Test
  public void testParserCancellation() {
    try (TSParser parser = TSParser.create()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      final var executor = Executors.newScheduledThreadPool(2);
      final var parseFuture = executor.schedule(() -> {

        // cancel the parsing after 200ms
        executor.schedule(() -> assertThat(parser.requestCancellationAsync()).isTrue(), 200,
          TimeUnit.MILLISECONDS);

        // parsing the View.java.txt file takes 300-600ms
        try (var tree = parser.parseString(readResource("View.java.txt"))) {
          // if the parsing is cancelled, then the tree must be null
          assertThat(tree).isNull();
        }
      }, 0, TimeUnit.MICROSECONDS);

      try {
        parseFuture.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Test
  public void testParserParseCallShouldFailIfAnotherParseIsInProgress() {
    try (final var parser = TSParser.create(); final var mainParseContent = UTF16StringFactory.newString()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      // Read the content before starting the threads
      final var fileContent = readResource("View.java.txt");
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);

      final var executor = Executors.newScheduledThreadPool(2);

      // start the main parse operation immediately
      final var parseFuture1 = executor.schedule(() -> {
        try (final var tree = parser.parseString(mainParseContent)) {
          // This parse was already in progress before another parse was requested
          // so this should return a valid tree
          assertThat(tree).isNotNull();
          assertThat(tree.canAccess()).isTrue();
        }
      }, 0, TimeUnit.MICROSECONDS);

      // delay the second parse so that the parser is in the 'parsing' state when this is executed
      final var secondParseDelayMs = 100;
      final var parseFuture2 = executor.schedule(() -> {

        // the parser should be in the 'parsing' state by now
        assertThat(parser.isParsing()).isTrue();

        try (var tree = parser.parseString(fileContent)) {
          // A parse was already in progress when this parse was requested
          // so the parseString call should never succeed
          assertThat(tree).isNull();
          throw new IllegalStateException("Second parse was supposed to fail!");
        } catch (Throwable e) {
          assertThat(e).isInstanceOf(TSParser.ParseInProgressException.class);
        }
      }, secondParseDelayMs, TimeUnit.MILLISECONDS);

      try {
        parseFuture1.get();
        parseFuture2.get();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Test
  public void testParserParseCallShouldSucceedIfAnotherParseIsInProgressAndCancellationWasRequested() {
    try (final var parser = TSParser.create(); final var mainParseContent = UTF16StringFactory.newString()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      // Read the content before starting the threads
      final var fileContent = readResource("View.java.txt");
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);

      final var executor = Executors.newScheduledThreadPool(2);

      // start the main parse operation immediately
      final var parseFuture1 = executor.schedule(() -> {
        try (final var tree = parser.parseString(mainParseContent)) {
          // This parse was cancelled and another parse was requested
          // so this should not return a valid tree
          assertThat(tree).isNull();
        }
      }, 0, TimeUnit.MICROSECONDS);

      // delay the second parse so that the parser is in the 'parsing' state when this is executed
      final var secondParseDelayMs = 100;
      final var parseFuture2 = executor.schedule(() -> {

        // the parser should be in the 'parsing' state by now
        assertThat(parser.isParsing()).isTrue();

        // request the cancellation
        assertThat(parser.requestCancellationAsync()).isTrue();

        // the next parse call should wait for the previous parse call to return
        try (var tree = parser.parseString(fileContent)) {
          // A parse was already in progress when this parse was requested
          // so the parseString call should never succeed
          assertThat(tree).isNotNull();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }, secondParseDelayMs, TimeUnit.MILLISECONDS);

      try {
        parseFuture1.get();
        parseFuture2.get();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Test
  public void testParserParseCallShouldNotFailIfWhenMultipleParsersAreParsing() {
    try (final var parser1 = TSParser.create();
      final var parser2 = TSParser.create();
      final var mainParseContent = UTF16StringFactory.newString()
    ) {
      parser1.setLanguage(TSLanguageJava.getInstance());
      parser2.setLanguage(TSLanguageJava.getInstance());

      // Read the content before starting the threads
      final var fileContent = readResource("View.java.txt");
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);

      final var executor = Executors.newScheduledThreadPool(2);

      final var parseFuture1 = executor.schedule(() -> {
        assertThat(parser1.isParsing()).isFalse();
        try (final var tree = parser1.parseString(mainParseContent)) {
          assertThat(tree).isNotNull();
        }
      }, 0, TimeUnit.MICROSECONDS);

      final var secondParseDelayMs = 1;
      final var parseFuture2 = executor.schedule(() -> {
        assertThat(parser2.isParsing()).isFalse();
        try (var tree = parser2.parseString(fileContent)) {
          assertThat(tree).isNotNull();
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }
      }, secondParseDelayMs, TimeUnit.MILLISECONDS);

      try {
        // both parser instances are independent and hence, both of them must succeed
        parseFuture1.get();
        parseFuture2.get();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Test
  public void testParserParseCallShouldFailIfAnotherParseIsInProgressAndCancellationWasNotRequested() {
    try (final var parser = TSParser.create(); final var mainParseContent = UTF16StringFactory.newString()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      // Read the content before starting the threads
      final var fileContent = readResource("View.java.txt");
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);

      final var executor = Executors.newScheduledThreadPool(2);

      // start the main parse operation immediately
      final var parseFuture1 = executor.schedule(() -> {
        try (final var tree = parser.parseString(mainParseContent)) {
          // This parse was already in progress before another parse was requested
          // so this should return a valid tree
          assertThat(tree).isNotNull();
          assertThat(tree.canAccess()).isTrue();
        }
      }, 0, TimeUnit.MICROSECONDS);

      // delay the second parse so that the parser is in the 'parsing' state when this is executed
      final var secondParseDelayMs = 100;
      final var parseFuture2 = executor.schedule(() -> {

        // the parser should be in the 'parsing' state by now
        assertThat(parser.isParsing()).isTrue();

        // request another parse WITHOUT cancelling the previous parse
        try (var tree = parser.parseString(fileContent)) {
          // A parse was already in progress when this parse was requested
          // and no cancellation was requested
          // so the parseString call should never succeed
          assertThat(tree).isNull();
          throw new IllegalStateException("Second parse was supposed to fail!");
        } catch (Throwable e) {
          e.printStackTrace();
          assertThat(e).isInstanceOf(TSParser.ParseInProgressException.class);
        }
      }, secondParseDelayMs, TimeUnit.MILLISECONDS);

      try {
        parseFuture1.get();
        parseFuture2.get();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  @Test
  public void testAwaitedCancellation() {
    try (final var parser = TSParser.create(); final var mainParseContent = UTF16StringFactory.newString()) {
      parser.setLanguage(TSLanguageJava.getInstance());

      // Read the content before starting the threads
      final var fileContent = readResource("View.java.txt");
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);
      mainParseContent.append(fileContent);

      final var executor = Executors.newScheduledThreadPool(20);

      // start the main parse operation immediately
      final var parseFuture1 = executor.schedule(() -> {
        try (final var tree = parser.parseString(mainParseContent)) {
          // This parse was cancelled and another parse was requested
          // so this should fail
          assertThat(tree).isNull();
        }
      }, 0, TimeUnit.MICROSECONDS);

      // delay the second parse so that the parser is in the 'parsing' state when this is executed
      final var secondParseDelayMs = 100;
      final var parseFuture2 = executor.schedule(() -> {

        // the parser should be in the 'parsing' state by now
        assertThat(parser.isParsing()).isTrue();

        // request parse cancellation and wait till the parse returns
        final var start = System.currentTimeMillis();
        parser.requestCancellationAndWait();
        System.err.println("cancelAndWait() waited for " + (System.currentTimeMillis() - start) + "ms");

        // request another parse
        try (var tree = parser.parseString(fileContent)) {
          // A parse was already in progress when this parse was requested
          // however, we cancelled that parse and requested this one
          // so this parseString call should succeed
          assertThat(tree).isNotNull();
          assertThat(tree.canAccess()).isTrue();
        }
      }, secondParseDelayMs, TimeUnit.MILLISECONDS);

      try {
        parseFuture1.get();
        parseFuture2.get();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      } finally {
        executor.shutdownNow();
      }
    }
  }

  private static Map<String, List<TSNode>> execQueryGroupByCaptures(String querySource,
                                                                    TSLanguage language, TSNode node
  ) {
    final var result = new HashMap<String, List<TSNode>>();
    try (final var cursor = TSQueryCursor.create(); final var query = TSQuery.create(language,
      querySource)) {
      cursor.exec(query, node);
      TSQueryMatch match;
      while ((match = cursor.nextMatch()) != null) {
        for (TSQueryCapture capture : match.getCaptures()) {
          result.computeIfAbsent(query.getCaptureNameForId(capture.getIndex()),
            name -> new ArrayList<>()).add(capture.getNode());
        }
      }
    }

    return result;
  }

  private static List<String> substrings(List<TSNode> nodes, String source) {
    return nodes.stream()
      .map(node -> source.substring(node.getStartByte() / 2, node.getEndByte() / 2))
      .collect(Collectors.toList());
  }

  private static int getTrimmedLength(CharSequence s) {
    int len = s.length();

    int start = 0;
    while (start < len && s.charAt(start) <= ' ') {
      start++;
    }

    int end = len;
    while (end > start && s.charAt(end - 1) <= ' ') {
      end--;
    }

    return end - start;
  }
}
