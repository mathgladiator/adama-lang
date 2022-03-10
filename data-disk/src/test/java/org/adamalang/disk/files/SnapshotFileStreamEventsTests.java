/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.files;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SnapshotFileStreamEventsTests {
  @Test
  public void readAndWrite() throws Exception {

    final byte[] snapshotFile;
    final int docLen;
    {
      ByteArrayOutputStream memory = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(memory);
      SnapshotFileStreamEvents writer = SnapshotFileStreamEvents.writerFor(new DataOutputStream(output));
      byte[] doc = "Howdy".getBytes(StandardCharsets.UTF_8);
      docLen = doc.length;
      writer.onHeader(new SnapshotHeader(123, 42, doc.length, 42, false));
      writer.onDocument(doc);
      writer.onUndo(1, "UNDO1");
      writer.onUndo(2, "UNDO2");
      writer.onUndo(3, "UNDO3");
      writer.onUndo(4, "UNDO4");
      writer.onFinished(true);
      output.close();
      snapshotFile = memory.toByteArray();
    }

    { // IDEAL: entire thing
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(snapshotFile));
      SnapshotFileStreamEvents.read(input, new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          Assert.assertEquals(123, header.seq);
          Assert.assertEquals(42, header.history);
          Assert.assertEquals(docLen, header.documentSize);
          Assert.assertFalse(header.active);
          return true;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.assertEquals("Howdy", new String(document, StandardCharsets.UTF_8));
          return true;
        }

        int at = 1;
        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          Assert.assertEquals(at, seq);
          Assert.assertEquals("UNDO" + at, undo);
          at++;
          return true;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertTrue(complete);
        }
      });
    }

    { // IDEAL: header + doc + 4 undo = happy complete
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(snapshotFile));
      SnapshotFileStreamEvents.read(input, new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          Assert.assertEquals(123, header.seq);
          Assert.assertEquals(42, header.history);
          Assert.assertEquals(docLen, header.documentSize);
          Assert.assertFalse(header.active);
          return true;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.assertEquals("Howdy", new String(document, StandardCharsets.UTF_8));
          return true;
        }

        int at = 0;
        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          at++;
          Assert.assertEquals(at, seq);
          Assert.assertEquals("UNDO" + at, undo);
          return at < 4;
        }


        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertTrue(complete);
        }
      });
    }


    { // IDEAL: header + doc + 1 undo
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(snapshotFile));
      SnapshotFileStreamEvents.read(input, new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          Assert.assertEquals(123, header.seq);
          Assert.assertEquals(42, header.history);
          Assert.assertEquals(docLen, header.documentSize);
          Assert.assertFalse(header.active);
          return true;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.assertEquals("Howdy", new String(document, StandardCharsets.UTF_8));
          return true;
        }

        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          Assert.assertEquals(1, seq);
          Assert.assertEquals("UNDO1", undo);
          return false;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertFalse(complete);
        }
      });
    }

    { // IDEAL: header + doc
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(snapshotFile));
      SnapshotFileStreamEvents.read(input, new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          Assert.assertEquals(123, header.seq);
          Assert.assertEquals(42, header.history);
          Assert.assertEquals(docLen, header.documentSize);
          Assert.assertFalse(header.active);
          return true;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.assertEquals("Howdy", new String(document, StandardCharsets.UTF_8));
          return false;
        }

        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          Assert.fail();
          return false;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertFalse(complete);
        }
      });
    }

    { // IDEAL: just header
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(snapshotFile));
      SnapshotFileStreamEvents.read(input, new SnapshotFileStreamEvents() {
        @Override
        public boolean onHeader(SnapshotHeader header) throws IOException {
          Assert.assertEquals(123, header.seq);
          Assert.assertEquals(42, header.history);
          Assert.assertEquals(docLen, header.documentSize);
          Assert.assertFalse(header.active);
          return false;
        }

        @Override
        public boolean onDocument(byte[] document) throws IOException {
          Assert.fail();
          return false;
        }

        @Override
        public boolean onUndo(int seq, String undo) throws IOException {
          Assert.fail();
          return false;
        }

        @Override
        public void onFinished(boolean complete) throws IOException {
          Assert.assertFalse(complete);
        }
      });
    }

  }
}
