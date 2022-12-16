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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum TSInputEncoding {
    TSInputEncodingUTF8(0, StandardCharsets.UTF_8),
    TSInputEncodingUTF16(1, StandardCharsets.UTF_16LE);

    private final int flag;
    private final Charset charset;

    private TSInputEncoding(int flag, Charset charset) {
        this.flag = flag;
        this.charset = charset;
    }

    public int getFlag() {
        return flag;
    }

    public Charset getCharset() {
        return charset;
    }
}