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
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.string;

import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Decodes bytes in {@link UTF16String} to {@link String}.
 *
 * @author Akash Yadav
 */
public class StringDecoder {

  public static String fromBytes(byte[] bytes) {
    return fromBytes(bytes, UTF_16LE);
  }

  public static String fromBytes(byte[] bytes, Charset charset) {
    final var buff = charset.decode(ByteBuffer.wrap(bytes));
    return buff.toString();
  }
}
