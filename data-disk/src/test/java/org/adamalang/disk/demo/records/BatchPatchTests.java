/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo.records;

import org.adamalang.disk.demo.BadConsumer;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class BatchPatchTests {
  private static final RemoteDocumentUpdate A = new RemoteDocumentUpdate(1, 10, null, "request", "redo", "undo", true, 123, 50000L, UpdateType.Internal);
  private static final RemoteDocumentUpdate B = new RemoteDocumentUpdate(11, 15, new NtClient("yes", "auth"), "1", "2", "3", false, 0, 500L, UpdateType.Internal);

  public DataInputStream prepare(BadConsumer<DataOutputStream> consumer) throws Exception {
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(new BufferedOutputStream(memory));
    consumer.accept(output);
    output.flush();
    output.close();
    return new DataInputStream(new ByteArrayInputStream(memory.toByteArray()));
  }

  @Test
  public void empty() throws Exception {
    DataInputStream input = prepare((output) -> {
      Header.writeNewHeader(output);
      BatchPatch.write(new RemoteDocumentUpdate[0], output);
      BatchPatch.write(new RemoteDocumentUpdate[0], output);
      BatchPatch.write(new RemoteDocumentUpdate[0], output);
    });
    Header.from(input);
    RemoteDocumentUpdate[] updates = BatchPatch.read(Header.CURRENT_VERSION, input);
    Assert.assertEquals(0, updates.length);
    updates = BatchPatch.read(Header.CURRENT_VERSION, input);
    Assert.assertEquals(0, updates.length);
    updates = BatchPatch.read(Header.CURRENT_VERSION, input);
    Assert.assertEquals(0, updates.length);
    Assert.assertNull(BatchPatch.read(Header.CURRENT_VERSION, input));
  }

  @Test
  public void eof() throws Exception {
    DataInputStream input = prepare((output) -> {
      Header.writeNewHeader(output);
    });
    Header.from(input);
    Assert.assertNull(BatchPatch.read(Header.CURRENT_VERSION, input));
  }

  @Test
  public void singles() throws Exception {
    DataInputStream input = prepare((output) -> {
      Header.writeNewHeader(output);
      BatchPatch.write(new RemoteDocumentUpdate[] {A}, output);
      BatchPatch.write(new RemoteDocumentUpdate[] {B}, output);
    });
    Header.from(input);
    RemoteDocumentUpdate[] updates1 = BatchPatch.read(Header.CURRENT_VERSION, input);
    Assert.assertEquals(1, updates1.length);
    Assert.assertEquals(1, updates1[0].seqBegin);
    Assert.assertEquals(10, updates1[0].seqEnd);
    Assert.assertNull(updates1[0].who);
    Assert.assertEquals("request", updates1[0].request);
    Assert.assertEquals("redo", updates1[0].redo);
    Assert.assertEquals("undo", updates1[0].undo);
    Assert.assertTrue(updates1[0]. requiresFutureInvalidation);
    Assert.assertEquals(123, updates1[0].whenToInvalidateMilliseconds);
    Assert.assertEquals(50000L, updates1[0].assetBytes);
    RemoteDocumentUpdate[] updates2 = BatchPatch.read(Header.CURRENT_VERSION, input);
    Assert.assertEquals(1, updates2.length);
    Assert.assertEquals(11, updates2[0].seqBegin);
    Assert.assertEquals(15, updates2[0].seqEnd);
    Assert.assertEquals("yes", updates2[0].who.agent);
    Assert.assertEquals("auth", updates2[0].who.authority);
    Assert.assertEquals("1", updates2[0].request);
    Assert.assertEquals("2", updates2[0].redo);
    Assert.assertEquals("3", updates2[0].undo);
    Assert.assertFalse(updates2[0]. requiresFutureInvalidation);
    Assert.assertEquals(0, updates2[0].whenToInvalidateMilliseconds);
    Assert.assertEquals(500L, updates2[0].assetBytes);
    Assert.assertNull(BatchPatch.read(Header.CURRENT_VERSION, input));
  }

  @Test
  public void big() throws Exception {
    DataInputStream input = prepare((output) -> {
      Header.writeNewHeader(output);
      BatchPatch.write(new RemoteDocumentUpdate[] {A, B, A, B}, output);
      BatchPatch.write(new RemoteDocumentUpdate[] {A, B, A, B}, output);
    });
    Header.from(input);
    for (int k = 0; k < 2; k++) {
      RemoteDocumentUpdate[] updates = BatchPatch.read(Header.CURRENT_VERSION, input);
      Assert.assertEquals(4, updates.length);
      Assert.assertEquals(1, updates[0].seqBegin);
      Assert.assertEquals(10, updates[0].seqEnd);
      Assert.assertNull(updates[0].who);
      Assert.assertEquals("request", updates[0].request);
      Assert.assertEquals("redo", updates[0].redo);
      Assert.assertEquals("undo", updates[0].undo);
      Assert.assertTrue(updates[0].requiresFutureInvalidation);
      Assert.assertEquals(123, updates[0].whenToInvalidateMilliseconds);
      Assert.assertEquals(50000L, updates[0].assetBytes);
      Assert.assertEquals(11, updates[1].seqBegin);
      Assert.assertEquals(15, updates[1].seqEnd);
      Assert.assertEquals("yes", updates[1].who.agent);
      Assert.assertEquals("auth", updates[1].who.authority);
      Assert.assertEquals("1", updates[1].request);
      Assert.assertEquals("2", updates[1].redo);
      Assert.assertEquals("3", updates[1].undo);
      Assert.assertFalse(updates[1].requiresFutureInvalidation);
      Assert.assertEquals(0, updates[1].whenToInvalidateMilliseconds);
      Assert.assertEquals(500L, updates[1].assetBytes);

      Assert.assertEquals(1, updates[2].seqBegin);
      Assert.assertEquals(10, updates[2].seqEnd);
      Assert.assertNull(updates[2].who);
      Assert.assertEquals("request", updates[2].request);
      Assert.assertEquals("redo", updates[2].redo);
      Assert.assertEquals("undo", updates[2].undo);
      Assert.assertTrue(updates[2].requiresFutureInvalidation);
      Assert.assertEquals(123, updates[2].whenToInvalidateMilliseconds);
      Assert.assertEquals(50000L, updates[2].assetBytes);
      Assert.assertEquals(11, updates[3].seqBegin);
      Assert.assertEquals(15, updates[3].seqEnd);
      Assert.assertEquals("yes", updates[3].who.agent);
      Assert.assertEquals("auth", updates[3].who.authority);
      Assert.assertEquals("1", updates[3].request);
      Assert.assertEquals("2", updates[3].redo);
      Assert.assertEquals("3", updates[3].undo);
      Assert.assertFalse(updates[3].requiresFutureInvalidation);
      Assert.assertEquals(0, updates[3].whenToInvalidateMilliseconds);
      Assert.assertEquals(500L, updates[3].assetBytes);
    }
    Assert.assertNull(BatchPatch.read(Header.CURRENT_VERSION, input));
  }
}
