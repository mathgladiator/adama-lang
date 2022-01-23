/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** a very simple stream for the client to get a json stream from the web proxy */
public interface WebJsonStream {
  void data(ObjectNode node);

  void complete();

  void failure(int code);
}
