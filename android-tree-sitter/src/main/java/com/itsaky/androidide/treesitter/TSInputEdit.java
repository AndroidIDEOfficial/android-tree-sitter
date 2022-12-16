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