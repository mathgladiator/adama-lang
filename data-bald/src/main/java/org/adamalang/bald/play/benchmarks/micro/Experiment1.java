/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play.benchmarks.micro;

import java.io.File;
import java.io.FileOutputStream;

public class Experiment1 {

  public static void drive(Common common) throws Exception {
    for (int repeat = 1; repeat < 1024; repeat*=2) {
      byte[][] samples = common.generateSamples(repeat);
      File file = File.createTempFile("benchmark", "one");
      try {
        common.start();
        for (int writes = 0; writes < 1000; writes++) {
          byte[] toWrite = common.pick(samples);
          FileOutputStream output = new FileOutputStream(file, true);
          try {
            output.write(toWrite);
            output.flush();
          } finally {
            output.close();
          }
          common.report(toWrite.length);
        }
        common.stop();
      } finally {
        file.delete();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    // throw away the first run due to JVM warm-up
    drive(new Common());

    // now, run it 10 times
    Common common = new Common();
    for (int runs = 0; runs < 10; runs++) {
      drive(common);
    }
    common.dump();
  }
}
