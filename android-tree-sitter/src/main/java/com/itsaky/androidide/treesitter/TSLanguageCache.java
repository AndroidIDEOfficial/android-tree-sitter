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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides cached instances of {@link TSLanguage}.
 *
 * @author Akash Yadav
 */
public final class TSLanguageCache {

  private static final Map<String, TSLanguage> languagesByName = new ConcurrentHashMap<>();
  private static final Map<Long, TSLanguage> languagesByPtr = new ConcurrentHashMap<>();

  private TSLanguageCache() {
  }

  public static void cache(String name, TSLanguage language) {
    final var existing = languagesByName.put(name, language);
    if (existing != null && existing != language) {
      languagesByName.put(name, existing);
      throw new IllegalStateException(String.format("An instance of '%s' already exists", name));
    }

    languagesByPtr.put(language.pointer, language);
  }

  public static TSLanguage get(String name) {
    return languagesByName.get(name);
  }

  public static TSLanguage get(long pointer) {
    return languagesByPtr.get(pointer);
  }
}