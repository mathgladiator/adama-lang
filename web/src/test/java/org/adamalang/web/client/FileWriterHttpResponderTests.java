/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class FileWriterHttpResponderTests {
  @Test
  public void happy() throws Exception {
    AtomicInteger callbackValue = new AtomicInteger(0);
    File file = File.createTempFile("ADAMA_tempfile", "suffix");
    file.deleteOnExit();
    FileWriterHttpResponder writer = new FileWriterHttpResponder(file, wrap(callbackValue));
    writer.start(new SimpleHttpResponseHeader(200, Collections.emptyMap()));
    writer.bodyStart(3);
    writer.bodyFragment("XYZ".getBytes(StandardCharsets.UTF_8), 0, 3);
    Assert.assertEquals(0, callbackValue.get());
    writer.bodyEnd();
    Assert.assertEquals(1, callbackValue.get());
    Assert.assertEquals("XYZ", Files.readString(file.toPath()));
  }

  @Test
  public void prematureEnd() throws Exception {
    AtomicInteger callbackValue = new AtomicInteger(0);
    File file = File.createTempFile("ADAMA_tempfile", "suffix");
    file.deleteOnExit();
    FileWriterHttpResponder writer = new FileWriterHttpResponder(file, wrap(callbackValue));
    writer.start(new SimpleHttpResponseHeader(200, Collections.emptyMap()));
    writer.bodyStart(5);
    writer.bodyFragment("XYZ".getBytes(StandardCharsets.UTF_8), 0, 3);
    Assert.assertEquals(0, callbackValue.get());
    writer.bodyEnd();
    Assert.assertEquals(986319, callbackValue.get());
  }

  @Test
  public void not200() throws Exception {
    AtomicInteger callbackValue = new AtomicInteger(0);
    File file = File.createTempFile("ADAMA_tempfile", "suffix");
    file.deleteOnExit();
    FileWriterHttpResponder writer = new FileWriterHttpResponder(file, wrap(callbackValue));
    writer.start(new SimpleHttpResponseHeader(302, Collections.emptyMap()));
    writer.bodyStart(5);
    writer.bodyFragment("XYZ".getBytes(StandardCharsets.UTF_8), 0, 3);
    Assert.assertEquals(903347, callbackValue.get());
    writer.bodyEnd();
    Assert.assertEquals(903347, callbackValue.get());
  }

  @Test
  public void failureProxy() throws Exception {
    AtomicInteger callbackValue = new AtomicInteger(0);
    File file = File.createTempFile("ADAMA_tempfile", "suffix");
    file.deleteOnExit();
    FileWriterHttpResponder writer = new FileWriterHttpResponder(file, wrap(callbackValue));
    writer.failure(new ErrorCodeException(123));
    Assert.assertEquals(123, callbackValue.get());
    writer.start(new SimpleHttpResponseHeader(200, Collections.emptyMap()));
    writer.bodyStart(5);
    writer.bodyFragment("XYZ".getBytes(StandardCharsets.UTF_8), 0, 3);
    writer.bodyEnd();
    Assert.assertEquals(123, callbackValue.get());
  }

  @Test
  public void dumbCrash() throws Exception {
    try {
      new FileWriterHttpResponder(null, null);
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(928944, ece.code);
    }
  }

  @Test
  public void dumbCoverageFinish() {
    AtomicInteger callbackValue = new AtomicInteger(0);
    FileWriterHttpResponder.finish(null, wrap(callbackValue));
    Assert.assertEquals(993487, callbackValue.get());
  }

  @Test
  public void dumbCoverageWrite() {
    AtomicInteger callbackValue = new AtomicInteger(0);
    FileWriterHttpResponder.write(null, null, 0, 0, wrap(callbackValue));
    Assert.assertEquals(913615, callbackValue.get());
  }

  public Callback<Void> wrap(AtomicInteger value) {
    return new Callback<Void>() {
      @Override
      public void success(Void x) {
        value.set(1);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        value.set(ex.code);
      }
    };
  }
}
