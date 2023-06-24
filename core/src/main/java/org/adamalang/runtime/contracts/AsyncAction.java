/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.AbortMessageException;

/**
 * this is a lazy way of associating code to run within a queue. It's basically a runnable that can
 * throw an abort
 */
@FunctionalInterface
public interface AsyncAction {
  /** execute the given task, and maybe abort */
  void execute() throws AbortMessageException;
}
