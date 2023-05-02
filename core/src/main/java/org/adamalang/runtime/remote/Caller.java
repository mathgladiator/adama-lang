/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

/** Just enough information about the caller to be dangerous */
public interface Caller {
  /** get the pathway to deliver a message */
  Deliverer __getDeliverer();

  /** the key of the document making the call */
  String __getKey();

  /** the space of the document making the call */
  String __getSpace();
}
