/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play;

import org.adamalang.bald.play.atomic.AtomicRandomAccessFile;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class X {

  public static String expand(String x, int repeat) {
    StringBuilder sb = new StringBuilder();
    for(int k = 0; k < repeat; k++) {
      sb.append(x);
    }
    return sb.toString();
  }
  public static void main(String[] args) throws Exception {
    // public AtomicRandomAccessFile(SimpleExecutor executor, RandomAccessFile file, File wal, int bytesBetweenWalFlush, int millisecondsBetweenWalFlush, int bytesBetweensFileFlush) {
    SimpleExecutor executor = SimpleExecutor.create("simple");
    File file = File.createTempFile("file", "adama_" + System.currentTimeMillis());
    File wal = File.createTempFile("wal", "adama_" + System.currentTimeMillis());
    AtomicRandomAccessFile araf = new AtomicRandomAccessFile(executor, new RandomAccessFile(file, "rw"), wal, 6 * 1024 * 1024, 2, 1024 * 1024 * 1024);
    try {
      int M = 1024;
      byte[][] bytesToWrite = new byte[][]{expand("Small", M).getBytes(StandardCharsets.UTF_8), expand("MediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMediumMedium", M).getBytes(StandardCharsets.UTF_8), (expand("LargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLarge" + "LargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLarge" + "LargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLarge" + "LargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLarge" + "LargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLargeLarge", M)).getBytes(StandardCharsets.UTF_8),};

      for (int round = 0; round < 10; round++) {
        ArrayList<Callback<Void>> callbacks = new ArrayList<>();
        Random rng = new Random();
        int N = 20000;
        long byteWritten = 0;
        CountDownLatch latch = new CountDownLatch(N);
        for (int k = 0; k < N; k++) {
          byte[] toWrite = bytesToWrite[rng.nextInt(bytesToWrite.length)];
          byteWritten += toWrite.length;
          Callback<Void> callback = new Callback<Void>() {
            @Override
            public void success(Void value) {
              latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {

            }
          };
          callbacks.add(callback);
          araf.write(rng.nextInt(1024 * 1024 * 32), toWrite, callback);
        }
        araf.sync();
        long started = System.currentTimeMillis();
        while (!latch.await(1000, TimeUnit.MILLISECONDS)) {
          System.err.println("wait");
        }
        long delta = (System.currentTimeMillis() - started);
        double MBPerSecond = byteWritten * 1000.0; // bytes * 1000 ms / second
        MBPerSecond /= delta; // /= ms
        MBPerSecond /= (1024 * 1024); // * 1 MB / (1024 * 1024) bytes
        System.out.println("MB/second=" + MBPerSecond);
      }
    } finally {
      araf.sync();
      araf.close();
      file.delete();
      wal.delete();
    }
  }
}
