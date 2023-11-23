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
import static com.itsaky.androidide.treesitter.ResourceUtils.readResource;

import com.itsaky.androidide.treesitter.string.UTF16String;
import com.itsaky.androidide.treesitter.string.UTF16StringFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Akash Yadav
 */
@RunWith(RobolectricTestRunner.class)
public class UTF16StringTest extends TreeSitterTest {

  @Test
  public void testFunctionality() {
    final var str = UTF16StringFactory.newString("Hello");

    assertThat(str.toString()).isEqualTo("Hello");
    assertThat(str.length()).isEqualTo(5);
    assertThat(str.byteLength()).isEqualTo(10);

    assertThat(str.charAt(0)).isEqualTo('H');
    str.setCharAt(0, 'h');
    assertThat(str.charAt(0)).isEqualTo('h');
    str.setCharAt(0, 'H');

    assertThat(str.byteAt(0)).isEqualTo((byte) 'H');
    str.setByteAt(0, (byte) 'h');
    assertThat(str.byteAt(0)).isEqualTo('h');
    str.setByteAt(0, (byte) 'H');

    str.append(" World!");
    assertThat(str.toString()).isEqualTo("Hello World!");
    assertThat(str.length()).isEqualTo(12);
    assertThat(str.byteLength()).isEqualTo(24);

    str.append("__Only AndroidIDE will be appended__", 6, 11);
    assertThat(str.toString()).isEqualTo("Hello World! AndroidIDE");
    assertThat(str.length()).isEqualTo(23);
    assertThat(str.byteLength()).isEqualTo(46);

    str.insert(13, "Love ");
    assertThat(str.toString()).isEqualTo("Hello World! Love AndroidIDE");
    assertThat(str.length()).isEqualTo(28);
    assertThat(str.byteLength()).isEqualTo(56);

    str.delete(0, 13);
    assertThat(str.toString()).isEqualTo("Love AndroidIDE");
    assertThat(str.length()).isEqualTo(15);
    assertThat(str.byteLength()).isEqualTo(30);

    str.replaceChars(0, 4, "\uD83D\uDE0D");
    assertThat(str.toString()).isEqualTo("\uD83D\uDE0D AndroidIDE");
    assertThat(str.length()).isEqualTo(13);
    assertThat(str.byteLength()).isEqualTo(26);

    str.replaceChars(0, 2, "Love");
    assertThat(str.toString()).isEqualTo("Love AndroidIDE");
    assertThat(str.length()).isEqualTo(15);
    assertThat(str.byteLength()).isEqualTo(30);

    str.replaceBytes(0, 8, "\uD83D\uDE0D");
    assertThat(str.toString()).isEqualTo("\uD83D\uDE0D AndroidIDE");
    assertThat(str.length()).isEqualTo(13);
    assertThat(str.byteLength()).isEqualTo(26);

    str.replaceBytes(0, 4, "Love");
    assertThat(str.toString()).isEqualTo("Love AndroidIDE");
    assertThat(str.length()).isEqualTo(15);
    assertThat(str.byteLength()).isEqualTo(30);

    str.deleteBytes(0, 10);
    assertThat(str.toString()).isEqualTo("AndroidIDE");
    assertThat(str.length()).isEqualTo(10);
    assertThat(str.byteLength()).isEqualTo(20);

    var substr = str.subseqChars(7);
    assertThat(substr.toString()).isEqualTo("IDE");
    assertThat(substr.length()).isEqualTo(3);
    assertThat(substr.byteLength()).isEqualTo(6);

    substr = str.subseqChars(7, 8);
    assertThat(substr.toString()).isEqualTo("I");
    assertThat(substr.length()).isEqualTo(1);
    assertThat(substr.byteLength()).isEqualTo(2);

    substr = str.subseqBytes(14);
    assertThat(substr.toString()).isEqualTo("IDE");
    assertThat(substr.length()).isEqualTo(3);
    assertThat(substr.byteLength()).isEqualTo(6);

    substr = str.subseqBytes(14, 16);
    assertThat(substr.toString()).isEqualTo("I");
    assertThat(substr.length()).isEqualTo(1);
    assertThat(substr.byteLength()).isEqualTo(2);

    str.close();
  }

  @Test
  public void testEmoji() {
    final var str = UTF16StringFactory.newString("😍");

    str.append("\n\n");
    str.append("😍");

    assertThat(str.length()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D".length());
    assertThat(str.byteLength()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D".length() * 2);
    assertThat(str.toString()).isEqualTo("\uD83D\uDE0D\n\n\uD83D\uDE0D");
    str.close();
  }

  @Test
  public void testContinuousStringCreation() {
    final var arr = new UTF16String[100];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = UTF16StringFactory.newString("Item #" + i);
    }
    for (UTF16String utf16String : arr) {
      utf16String.close();
    }
  }

  @Test
  public void testMultithreadedUse() throws InterruptedException {
    final var threads = new Thread[20];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(() -> {
        final var strs = new UTF16String[100];
        for (int j = 0; j < strs.length; j++) {
          strs[j] = UTF16StringFactory.newString(
            "UTF16String from " + Thread.currentThread().getName());
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        for (UTF16String str : strs) {
          str.close();
        }
      }, "StringThread #" + i);
    }

    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  @Test
  public void testInsertAtLength() {
    final var str = UTF16StringFactory.newString("Hello");
    assertThat(str).isNotNull();
    assertThat(str.toString()).isEqualTo("Hello");
    assertThat(str.length()).isEqualTo(5);

    str.insert(5, " World!");
    assertThat(str.length()).isEqualTo(12);
    assertThat(str.toString()).isEqualTo("Hello World!");
    str.close();
  }

  @Test
  public void testCharIteration() {
    try (final var str = UTF16StringFactory.newString(readResource("View.java.txt"))) {
      assertThat(str).isNotNull();
      str.forEachChar(c -> {});
      str.forEachChar(100, 1000, c -> {});
    }
  }

  @Test
  public void testByteIteration() {
    try (final var str = UTF16StringFactory.newString(readResource("View.java.txt"))) {
      assertThat(str).isNotNull();
      str.forEachByte(b -> {});
      str.forEachByte(100, 1000, b -> {});
    }
  }
}
