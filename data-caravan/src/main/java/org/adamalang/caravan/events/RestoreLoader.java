/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.events;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/** convert a file into a list of writes */
public class RestoreLoader {
  public static ArrayList<byte[]> load(File file) throws Exception {
    ArrayList<byte[]> writes = new ArrayList<>();
    try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
      while (input.readBoolean()) {
        byte[] bytes = new byte[input.readInt()];
        input.readFully(bytes);
        writes.add(bytes);
      }
    }
    return writes;
  }
}
