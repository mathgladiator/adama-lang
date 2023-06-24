/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
