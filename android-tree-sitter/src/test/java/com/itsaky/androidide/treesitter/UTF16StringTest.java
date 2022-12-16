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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Akash Yadav
 */
@RunWith(JUnit4.class)
public class UTF16StringTest extends TreeSitterTest {

  @Before
  public void testFunctionality() {
    System.out.println("testFunc");
    final var str = UTF16String.newInstance("Hello");
    final var str2 = UTF16String.newInstance("World");

    assertThat(str.toString()).isEqualTo("Hello");
    assertThat(str.length()).isEqualTo(5);
    assertThat(str.byteLength()).isEqualTo(10);

    str.append(" World!");
    assertThat(str.toString()).isEqualTo("Hello World!");
    assertThat(str.length()).isEqualTo(12);
    assertThat(str.byteLength()).isEqualTo(24);
    str.close();
  }

  @Test
  public void testEmoji() {
    System.out.println("testEmoji");
    final var str = UTF16String.newInstance("😍");

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
      arr[i] = UTF16String.newInstance("Item #" + i);
    }
    for (UTF16String utf16String : arr) {
      utf16String.close();
    }
  }

  @Test
  public void testMultithreadedUse() throws InterruptedException {
    final var threads = new Thread[20];
    for (int i = 0; i < threads.length; i++) {
      threads[i] =
          new Thread(
              () -> {
                final var strs = new UTF16String[100];
                for (int j = 0; j < strs.length; j++) {
                  strs[j] =
                      UTF16String.newInstance(
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
              },
              "StringThread #" + i);
    }

    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }
}