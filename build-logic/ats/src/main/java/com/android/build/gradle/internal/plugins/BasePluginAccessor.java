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

package com.android.build.gradle.internal.plugins;

import com.android.build.gradle.internal.services.DslServices;

/**
 * Class which allows us to access protected members of {@link BasePlugin}.
 *
 * @author Akash Yadav
 */
public final class BasePluginAccessor {

  private BasePluginAccessor() {
    throw new UnsupportedOperationException();
  }

  public static DslServices getDslServices(BasePlugin<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> basePlugin) {
    return basePlugin.getDslServices();
  }
}
