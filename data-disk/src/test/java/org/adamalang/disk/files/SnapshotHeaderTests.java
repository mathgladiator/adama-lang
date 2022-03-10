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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SnapshotHeaderTests {

  private SnapshotHeader identity(SnapshotHeader header) throws Exception {
    final byte[] memory;
    {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      header.write(new DataOutputStream(buffer));
      buffer.flush();
      buffer.close();
      memory = buffer.toByteArray();
    }
    return SnapshotHeader.read(new DataInputStream(new ByteArrayInputStream(memory)));
  }

  @Test
  public void sanity() throws Exception {
    {
      SnapshotHeader header = identity(new SnapshotHeader(0, 0, 0, 0, false));
      Assert.assertEquals(0, header.seq);
      Assert.assertEquals(0, header.history);
      Assert.assertEquals(0, header.documentSize);
      Assert.assertEquals(0, header.assetBytes);
      Assert.assertFalse(header.active);
      System.err.println(header.toString());
    }
    {
      SnapshotHeader header = identity(new SnapshotHeader(1, 2, 3, 4, true));
      Assert.assertEquals(1, header.seq);
      Assert.assertEquals(2, header.history);
      Assert.assertEquals(3, header.documentSize);
      Assert.assertEquals(4, header.assetBytes);
      Assert.assertTrue(header.active);
      System.err.println(header.toString());
    }
    {
      SnapshotHeader header = identity(new SnapshotHeader(1000000000, 1000000000, 1000000000, 1000000000000L,true));
      Assert.assertEquals(1000000000, header.seq);
      Assert.assertEquals(1000000000, header.history);
      Assert.assertEquals(1000000000, header.documentSize);
      Assert.assertEquals(1000000000000L, header.assetBytes);
      Assert.assertTrue(header.active);
      System.err.println(header.toString());
    }
    {
      SnapshotHeader header = identity(new SnapshotHeader(-1000000, -1000000, -1000000, -1000000000000L,false));
      Assert.assertEquals(-1000000, header.seq);
      Assert.assertEquals(-1000000, header.history);
      Assert.assertEquals(-1000000, header.documentSize);
      Assert.assertEquals(-1000000000000L, header.assetBytes);
      Assert.assertFalse(header.active);
      System.err.println(header.toString());
    }

  }
}
