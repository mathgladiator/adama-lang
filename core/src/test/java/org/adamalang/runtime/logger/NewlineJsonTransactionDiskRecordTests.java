/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.junit.Assert;
import org.junit.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class NewlineJsonTransactionDiskRecordTests {
    public static BufferedReader readerOf(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        InputStreamReader reader = new InputStreamReader(input);
        return new BufferedReader(reader);
    }
    public static byte[] lines(String... lines) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8);
        for (String line : lines) {
            writer.println(line);
        }
        return baos.toByteArray();
    }


    @Test
    public void single() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = new NewlineJsonTransactionDiskRecord(reader);
        Assert.assertTrue(record.valid());
        record = new NewlineJsonTransactionDiskRecord(reader);
        Assert.assertFalse(record.valid());
    }

    @Test
    public void single_convert() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = new NewlineJsonTransactionDiskRecord(reader);
        Assert.assertTrue(record.valid());
        Transaction t = record.toTransaction();
        ByteArrayOutputStream memory = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(memory);
        NewlineJsonTransactionDiskRecord.writeTo(t, writer);
        writer.flush();
        String str = new String(memory.toByteArray());
        Assert.assertEquals("{\"request\":\"data\"}\r\n" +
                "{\"delta\":\"data\"}\r\n" +
                "{\"needsInvalidation\":false,\"whenToInvalidMilliseconds\":0,\"seq\":0}", str.trim());
    }

    @Test
    public void no_delta_1() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = null;
        try {
            record = new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {

        }
        Assert.assertNull(record);
    }

    @Test
    public void no_delta_2() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"), JsonHelper.encode("request", "data"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = null;
        try {
            record = new NewlineJsonTransactionDiskRecord(reader);
            record = new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
        Assert.assertNotNull(record);
    }

    @Test
    public void no_meta() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = null;
        try {
            record = new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
        Assert.assertNull(record);
    }

    @Test
    public void no_meta_2() throws Exception {
        byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), JsonHelper.encode("x"), JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"));
        BufferedReader reader = readerOf(lines);
        NewlineJsonTransactionDiskRecord record = null;
        try {
            record = new NewlineJsonTransactionDiskRecord(reader);
            record = new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
        Assert.assertNotNull(record);
    }

    @Test
    public void bad1() throws Exception {
        try {
            byte[] lines = lines("x", JsonHelper.encode("delta", "data"), JsonHelper.encode("x"));
            BufferedReader reader = readerOf(lines);
            new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
    }

    @Test
    public void bad2() throws Exception {
        try {
            byte[] lines = lines(JsonHelper.encode("request", "data"), "x", JsonHelper.encode("x"));
            BufferedReader reader = readerOf(lines);
            new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
    }

    @Test
    public void bad3() throws Exception {
        try {
            byte[] lines = lines(JsonHelper.encode("request", "data"), JsonHelper.encode("delta", "data"), "x");
            BufferedReader reader = readerOf(lines);
            new NewlineJsonTransactionDiskRecord(reader);
            Assert.fail();
        } catch (IOException ioe) {
        }
    }
}
