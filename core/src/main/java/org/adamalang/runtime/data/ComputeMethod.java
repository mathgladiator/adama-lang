/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

/** the backend data service provides a variety of algorithms to execute on the log */
public enum ComputeMethod {
  /** patch the local document to be up to date after the given sequencer */
  HeadPatch(1),
  /** rewind the document to the given sequencer */
  Rewind(2);

  public final int type;

  ComputeMethod(int type) {
    this.type = type;
  }

  public static ComputeMethod fromType(int type) {
    for (ComputeMethod method : ComputeMethod.values()) {
      if (method.type == type) {
        return method;
      }
    }
    return null;
  }
}
