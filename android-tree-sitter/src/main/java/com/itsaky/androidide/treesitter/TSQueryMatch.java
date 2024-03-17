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

import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Akash Yadav
 */
public class TSQueryMatch {

  protected int id;
  protected int patternIndex;
  protected TSQueryCapture[] captures;

  protected final Metadata metadata;

  protected TSQueryMatch() {
    this.metadata = new Metadata();
  }

  protected TSQueryMatch(int id, int patternIndex, TSQueryCapture[] captures, Metadata metadata) {
    this.id = id;
    this.patternIndex = patternIndex;
    this.captures = captures;

    if (metadata == null) {
      metadata = new Metadata();
    }

    this.metadata = metadata;
  }

  public static TSQueryMatch create(int id, int patternIndex, TSQueryCapture[] captures) {
    return TSObjectFactoryProvider.getFactory().createQueryMatch(id, patternIndex, captures);
  }

  public int getId() {
    return id;
  }

  public int getPatternIndex() {
    return patternIndex;
  }

  public TSQueryCapture[] getCaptures() {
    return captures;
  }

  public TSQueryCapture getCapture(int index) {
    return captures[index];
  }

  public Metadata getMetadata() {
    return metadata;
  }

  /**
   * Metadata associated with a {@link TSQueryMatch}.
   */
  public static class Metadata {

    private final Map<Object, Object> data = new HashMap<>(0);

    /**
     * Check if the map contains the given key.
     *
     * @param key The key.
     * @return <code>true</code> if the map contains the key, <code>false</code> otherwise.
     */
    public boolean containsKey(Object key) {
      return data.containsKey(key);
    }

    /**
     * Generic getter for the given key.
     */
    public <T> T get(Object key) {
      //noinspection unchecked
      return (T) data.get(key);
    }

    /**
     * Generic setter for the given key.
     */
    public <T> T put(Object key, T value) {
      //noinspection unchecked
      return (T) data.put(key, value);
    }

    /**
     * Get the value associated with the given key. This will return the value as a {@link String}.
     *
     * @param key The key.
     * @return The string value, or <code>null</code>.
     */
    public String getString(String key) {
      return get(key);
    }

    /**
     * Put a string value with the given key.
     *
     * @param key   The key.
     * @param value The value.
     * @return The previous value associated with the key, or <code>null</code>.
     */
    public String putString(String key, String value) {
      return put(key, value);
    }

    /**
     * Get metadata specific to the given capture ID.
     *
     * @param captureId The capture ID.
     * @return The metadata associated with the capture ID, or <code>null</code>.
     */
    public Metadata getCaptureMetadata(String captureId) {
      return get(captureId);
    }

    /**
     * Put metadata specific to the given capture ID.
     *
     * @param captureId The capture ID.
     * @param metadata  The metadata.
     * @return The previous metadata associated with the capture ID, or <code>null</code>.
     */
    public Metadata putCaptureMetadata(String captureId, Metadata metadata) {
      return put(captureId, metadata);
    }
  }
}