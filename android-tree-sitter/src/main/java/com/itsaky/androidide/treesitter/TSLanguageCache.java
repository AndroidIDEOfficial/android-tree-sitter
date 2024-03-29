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

import com.itsaky.androidide.treesitter.util.Pair;
import java.util.HashSet;
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

  /**
   * Caches the given {@link TSLanguage}.
   *
   * @param name     The name of the language. This can be later used to retrieve the language
   *                 instance using {@link TSLanguageCache#get(String)}.
   * @param language The {@link TSLanguage} instance to cache.
   */
  public static void cache(String name, TSLanguage language) {
    languagesByName.computeIfAbsent(name, key -> language);
    languagesByPtr.put(language.getNativeObject(), language);
  }

  /**
   * Get the {@link TSLanguage} instance by the given pointer.
   *
   * @param name The name of the language to get the {@link TSLanguage} instance for.
   * @return The {@link TSLanguage} instance for the given language, or <code>null</code> if the
   * language was not cached.
   */
  public static TSLanguage get(String name) {
    if (name == null) {
      return null;
    }
    return languagesByName.get(name);
  }

  /**
   * Get the {@link TSLanguage} instance by the given pointer.
   *
   * @param pointer The pointer to the native language instance.
   * @return The {@link TSLanguage} instance for the given pointer, or <code>null</code> if the
   * language was not cached.
   */
  public static TSLanguage get(long pointer) {
    if (pointer == 0) {
      return null;
    }
    return languagesByPtr.get(pointer);
  }

  /**
   * Calls {@link TSLanguage#close()} on each cached language that is externally loaded. This makes
   * sure that any language that may have been opened with
   * {@link TSLanguage#loadLanguage(String, String)} closes the associated native library handle.
   */
  public static void closeExternal() {
    final var toRemove = new HashSet<Pair<String, Long>>();
    languagesByName.forEach((name, lang) -> {
      if (lang.isExternal()) {
        toRemove.add(Pair.of(name, lang.getNativeObject()));
        lang.close();
      }
    });

    for (Pair<String, Long> lang : toRemove) {
      //noinspection resource
      languagesByName.remove(lang.first);
      //noinspection resource
      languagesByPtr.remove(lang.second);
    }
  }

  /**
   * Remove the given language from the cache.
   *
   * @param language The language to remove.
   */
  static void remove(TSLanguage language) {
    if (language == null) {
      return;
    }

    if (language.getName() != null) {
      //noinspection resource
      languagesByName.remove(language.getName());
    } else {
      languagesByName.entrySet().removeIf(entry -> language.equals(entry.getValue()));
    }

    if (language.getNativeObject() != 0) {
      //noinspection resource
      languagesByPtr.remove(language.getNativeObject());
    } else {
      languagesByPtr.entrySet().removeIf(entry -> language.equals(entry.getValue()));
    }
  }
}