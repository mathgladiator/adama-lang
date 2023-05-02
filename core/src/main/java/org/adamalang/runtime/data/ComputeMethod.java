/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
