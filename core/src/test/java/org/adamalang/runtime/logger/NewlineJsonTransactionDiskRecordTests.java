/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class NewlineJsonTransactionDiskRecordTests {
  public static byte[] lines(final String... lines) {
    final var baos = new ByteArrayOutputStream();
    final var writer = new PrintWriter(baos, true, StandardCharsets.UTF_8);
    for (final String line : lines) {
      writer.println(line);
    }
    return baos.toByteArray();
  }

  public static BufferedReader readerOf(final byte[] bytes) {
    final var input = new ByteArrayInputStream(bytes);
    final var reader = new InputStreamReader(input);
    return new BufferedReader(reader);
  }

  @Test
  public void bad3() throws Exception {
    try {
      final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), "x");
      final var reader = readerOf(lines);
      new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
  }

  @Test
  public void bad4() throws Exception {
    try {
      final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("delta", "data"), "x");
      final var reader = readerOf(lines);
      new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
  }

  @Test
  public void no_delta_1() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "data"));
    final var reader = readerOf(lines);
    NewlineJsonTransactionDiskRecord record = null;
    try {
      record = new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
    Assert.assertNull(record);
  }

  @Test
  public void no_delta_2() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"));
    final var reader = readerOf(lines);
    NewlineJsonTransactionDiskRecord record = null;
    try {
      record = new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
    Assert.assertNull(record);
  }

  @Test
  public void no_delta_3() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"), JsonHelper.encode("request", "data"));
    final var reader = readerOf(lines);
    NewlineJsonTransactionDiskRecord record = null;
    try {
      record = new NewlineJsonTransactionDiskRecord(reader);
      record = new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
    Assert.assertNotNull(record);
  }

  @Test
  public void no_meta() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"));
    final var reader = readerOf(lines);
    NewlineJsonTransactionDiskRecord record = null;
    try {
      record = new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
    Assert.assertNull(record);
  }

  @Test
  public void no_meta_2() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"), JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"));
    final var reader = readerOf(lines);
    NewlineJsonTransactionDiskRecord record = null;
    try {
      record = new NewlineJsonTransactionDiskRecord(reader);
      record = new NewlineJsonTransactionDiskRecord(reader);
      Assert.fail();
    } catch (final IOException ioe) {}
    Assert.assertNotNull(record);
  }

  @Test
  public void single() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "after"), JsonHelper.encode("request", "before"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"));
    final var reader = readerOf(lines);
    var record = new NewlineJsonTransactionDiskRecord(reader);
    Assert.assertTrue(record.valid());
    record = new NewlineJsonTransactionDiskRecord(reader);
    Assert.assertFalse(record.valid());
  }

  @Test
  public void single_convert() throws Exception {
    final var lines = lines(JsonHelper.encode("request", "after"), JsonHelper.encode("request", "before"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"));
    final var reader = readerOf(lines);
    final var record = new NewlineJsonTransactionDiskRecord(reader);
    Assert.assertTrue(record.valid());
    final var t = record.toTransaction();
    final var memory = new ByteArrayOutputStream();
    final var writer = new PrintWriter(memory);
    NewlineJsonTransactionDiskRecord.writeTo(t, writer);
    writer.flush();
    final var str = new String(memory.toByteArray());
    Assert.assertEquals("{\"request\":\"after\"}\n" + "{\"request\":\"before\"}\n" + "{\"delta\":\"data\"}\n" + "{\"needsInvalidation\":false,\"whenToInvalidMilliseconds\":0,\"seq\":0}", str.trim().replaceAll(Pattern.quote("\r"), ""));
  }
}
