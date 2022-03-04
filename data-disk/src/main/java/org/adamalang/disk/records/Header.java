/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.records;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** every file has a header which is used for versioning should things go south */
public class Header {
  public static final int CURRENT_VERSION = 424242;
  public final int version;

  private Header(int version) {
    this.version = version;
  }

  public static void writeNewHeader(DataOutputStream output) throws IOException {
    output.writeInt(CURRENT_VERSION);
  }

  public static Header from(DataInputStream input) throws IOException {
    return new Header(input.readInt());
  }
}
