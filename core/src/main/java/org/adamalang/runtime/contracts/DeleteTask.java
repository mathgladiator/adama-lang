/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

/** a task to wedge between mark-delete and commit-delete */
public interface DeleteTask {

  public void executeAfterMark(Callback<Void> callback);

  /** trivial task that just executes the callback; i.e. do nothing */
  public static final DeleteTask TRIVIAL = new DeleteTask() {
    @Override
    public void executeAfterMark(Callback<Void> callback) {
      callback.success(null);
    }
  };
}
