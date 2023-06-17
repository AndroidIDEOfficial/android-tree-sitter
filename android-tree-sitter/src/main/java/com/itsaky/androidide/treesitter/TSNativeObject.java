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
 * Represents a native object.
 *
 * @author Akash Yadav
 */
public abstract class TSNativeObject implements TSClosable {

  protected long pointer;

  /**
   * Creates a new {@link TSNativeObject} instance with the given pointer.
   *
   * @param pointer The pointer to the native object. Subclasses can initialize this
   *                {@link TSNativeObject} with pointer set to 0 and then set the pointer lazily.
   */
  protected TSNativeObject(final long pointer) {
    this.pointer = pointer;
  }

  /**
   * Check whether the native object can be accessed or not. This checks for a valid pointer to the
   * native object.
   *
   * @return Whether the native object can be accessed.
   */
  public boolean canAccess() {
    return this.pointer != 0;
  }

  protected void checkAccess() {
    if (!canAccess()) {
      throw new IllegalStateException("Cannot access native object");
    }
  }

  @Override
  public void close() {
    if (this.pointer != 0) {
      closeNativeObj();
    }

    this.pointer = 0;
  }

  /**
   * Closes/deletes the native object.
   */
  protected abstract void closeNativeObj();
}
