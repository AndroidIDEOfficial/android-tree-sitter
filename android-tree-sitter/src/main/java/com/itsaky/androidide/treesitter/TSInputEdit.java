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

package com.itsaky.androidide.treesitter;

/**
 * InputEdit
 */
public class TSInputEdit {

    public int startByte;
    int oldEndByte;
    int newEndByte;
    public TSPoint start_point;
    TSPoint old_end_point;
    TSPoint new_end_point;

    public TSInputEdit(
        int startByte,
        int oldEndByte,
        int newEndByte,
        TSPoint start_point,
        TSPoint old_end_point,
        TSPoint new_end_point
    ) {
        this.startByte = startByte;
        this.oldEndByte = oldEndByte;
        this.newEndByte = newEndByte;
        this.start_point = start_point;
        this.old_end_point = old_end_point;
        this.new_end_point = new_end_point;
    }
}