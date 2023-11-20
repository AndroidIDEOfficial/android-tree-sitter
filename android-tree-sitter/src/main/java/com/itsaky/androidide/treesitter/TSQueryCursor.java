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

import com.itsaky.androidide.treesitter.annotations.GenerateNativeHeaders;
import com.itsaky.androidide.treesitter.util.TSObjectFactoryProvider;
import dalvik.annotation.optimization.FastNative;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Akash Yadav
 */
public class TSQueryCursor extends TSNativeObject implements Iterable<TSQueryMatch> {

  protected boolean isExecuted = false;
  protected TSNode targetNode = null;

  private boolean allowChangedNodes = false;

  protected TSQueryCursor() {
    this(Native.newCursor());
  }

  protected TSQueryCursor(long pointer) {
    super(pointer);
  }

  public static TSQueryCursor create(long pointer) {
    return TSObjectFactoryProvider.getFactory().createQueryCursor(pointer);
  }

  public static TSQueryCursor create() {
    return create(Native.newCursor());
  }

  /**
   * Whether the cursor should accept {@link TSNode}s whose {@link TSNode#hasChanges()} returns
   * true. This is set to <code>false</code> by default. Setting it to <code>true</code> is risky,
   * especially in cases when the node or the tree is accessed/edited from multiple threads. This
   * could result in
   * <code>SEGV_MAPERR</code> issues.
   *
   * @param allowChangedNodes Whether changed nodes should be allowed.
   */
  public void setAllowChangedNodes(boolean allowChangedNodes) {
    this.allowChangedNodes = allowChangedNodes;
  }

  /**
   * Whether the cursor accepts {@link TSNode} whose {@link TSNode#hasChanges()} returns true.
   *
   * @return Whether the cursor accepts {@link TSNode} whose {@link TSNode#hasChanges()} returns
   * true.
   * @see #setAllowChangedNodes(boolean)
   */
  public boolean isAllowChangedNodes() {
    return allowChangedNodes;
  }

  /**
   * Start running the given query on the given node.
   */
  public void exec(TSQuery query, TSNode node) {
    Objects.requireNonNull(node, "TSNode cannot be null");
    checkAccess();
    if (query == null || !query.canAccess()) {
      throw new IllegalArgumentException("Cannot execute invalid query");
    }
    if (!node.canAccess() || !node.getTree().canAccess() ||
      (!isAllowChangedNodes() && node.hasChanges())) {
      String msg = "Cannot execute query on invalid node. node=" + node + " node.canAccess=" +
        node.canAccess() + " node.tree.canAccess=" + node.getTree().canAccess() +
        " node.hasChanges=" + node.hasChanges() + " isAllowChangedNodes=" + isAllowChangedNodes();

      throw new IllegalArgumentException(msg);
    }
    Native.exec(getNativeObject(), query.getNativeObject(), node);
    isExecuted = true;
    targetNode = node;
  }

  /**
   * @noinspection NullableProblems
   */
  @Override
  public Iterator<TSQueryMatch> iterator() {

    return new Iterator<>() {

      private TSQueryMatch nextMatch = null;

      @Override
      public boolean hasNext() {
        boolean shouldFetchNextMatch = canAccess() // query cursor must be accessible
          && isExecuted // at least one query should have been executed
          && targetNode != null // query should have been executed on a non-null node

          // the target node's tree should not have been changed since query execution
          // if the user has explicitly opted to allow changed nodes, allow those changes
          && (isAllowChangedNodes() || !targetNode.hasChanges());
        nextMatch = shouldFetchNextMatch ? nextMatch() : null;
        return nextMatch != null;
      }

      @Override
      public TSQueryMatch next() {
        if (nextMatch == null) {
          throw new NoSuchElementException();
        }

        return nextMatch;
      }
    };
  }

  /**
   * Whether the maximum number of in-progress matches allowed by this query cursor has been
   * exceeded or not.
   *
   * <p>Query cursors have an optional maximum capacity for storing lists of in-progress captures.
   * If this capacity is exceeded, then the earliest-starting match will silently be dropped to make
   * room for further matches. This maximum capacity is optional — by default, query cursors allow
   * any number of pending matches, dynamically allocating new space for them as needed as the query
   * is executed.
   */
  public boolean didExceedMatchLimit() {
    checkAccess();
    return Native.exceededMatchLimit(getNativeObject());
  }

  /**
   * Get the maximum number of in-progress matches allowed by this query * cursor.
   *
   * @return The match limit.
   * @see #didExceedMatchLimit()
   */
  public int getMatchLimit() {
    checkAccess();
    return Native.getMatchLimit(getNativeObject());
  }

  /**
   * Set the maximum number of in-progress matches allowed by this query * cursor.
   *
   * @param newLimit The new match limit.
   * @see #didExceedMatchLimit()
   */
  public void setMatchLimit(int newLimit) {
    checkAccess();
    Native.setMatchLimit(getNativeObject(), newLimit);
  }

  public void setByteRange(int start, int end) {
    checkAccess();
    Native.setByteRange(getNativeObject(), start, end);
  }

  public void setPointRange(TSPoint start, TSPoint end) {
    checkAccess();
    Native.setPointRange(getNativeObject(), start, end);
  }

  public TSQueryMatch nextMatch() {
    checkAccess();
    checkExecuted("nextMatch");
    return Native.nextMatch(getNativeObject());
  }

  public void removeMatch(int id) {
    checkAccess();
    checkExecuted("removeMatch");
    Native.removeMatch(getNativeObject(), id);
  }

  @Override
  public void close() {
    isExecuted = false;
    targetNode = null;
    super.close();
  }

  @Override
  protected void closeNativeObj() {
    Native.delete(getNativeObject());
  }

  protected void checkExecuted(String name) {
    if (!isExecuted) {
      throw new IllegalStateException(
        "TSQueryCursor.exec() must be called before accessing '" + name + "'");
    }
  }

  @GenerateNativeHeaders(fileName = "query_cursor")
  private static class Native {

    static {
      registerNatives();
    }

    @FastNative
    static native void registerNatives();

    @FastNative
    static native long newCursor();

    @FastNative
    static native void delete(long cursor);

    @FastNative
    static native void exec(long cursor, long query, TSNode node);

    @FastNative
    static native boolean exceededMatchLimit(long cursor);

    @FastNative
    static native void setMatchLimit(long cursor, int newLimit);

    @FastNative
    static native int getMatchLimit(long cursor);

    @FastNative
    static native void setByteRange(long cursor, int start, int end);

    @FastNative
    static native void setPointRange(long cursor, TSPoint start, TSPoint end);

    @FastNative
    static native TSQueryMatch nextMatch(long cursor);

    @FastNative
    static native void removeMatch(long cursor, int id);
  }
}