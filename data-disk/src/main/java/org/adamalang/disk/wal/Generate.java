/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.wal;

import org.adamalang.common.codec.CodecCodeGen;

import java.io.File;
import java.nio.file.Files;

public class Generate {
  public static void main(String[] args) throws Exception {
    String codec = CodecCodeGen.assembleCodec("org.adamalang.disk.wal", "WriteAheadMessageCodec", WriteAheadMessage.class.getDeclaredClasses());
    Files.writeString(new File("./data-disk/src/main/java/org/adamalang/disk/wal/WriteAheadMessageCodec.java").toPath(), codec);
  }
}
