/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class RemoteDocumentUpdateTests {
  private static final RemoteDocumentUpdate UPDATE_1 = new RemoteDocumentUpdate(1, 1, NtPrincipal.NO_ONE, "REQUEST", "{\"x\":1}", "{\"x\":0}", false, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_2 = new RemoteDocumentUpdate(2, 2, null, "REQUEST", "{\"x\":2}", "{\"x\":1}", true, 0, 100, UpdateType.Invalidate);
  private static final RemoteDocumentUpdate UPDATE_3 = new RemoteDocumentUpdate(3, 3, null, "REQUEST", "{\"x\":3}", "{\"x\":2}", true, 0, 100, UpdateType.Invalidate);
  private static final RemoteDocumentUpdate UPDATE_4 = new RemoteDocumentUpdate(4, 4, null, "REQUEST", "{\"x\":4}", "{\"x\":3}", true, 0, 100, UpdateType.AddUserData);
  private static final RemoteDocumentUpdate UPDATE_5 = new RemoteDocumentUpdate(5, 5, null, "REQUEST", "{\"x\":5}", "{\"x\":4}", true, 0, 100, UpdateType.Invalidate);
  private static final RemoteDocumentUpdate UPDATE_6 = new RemoteDocumentUpdate(6, 6, null, "REQUEST", "{\"x\":6}", "{\"x\":5}", true, 0, 100, UpdateType.AddUserData);

  @Test
  public void merging() {
    RemoteDocumentUpdate[] input = new RemoteDocumentUpdate[]{UPDATE_1, UPDATE_2, UPDATE_3, UPDATE_4, UPDATE_5, UPDATE_6};
    RemoteDocumentUpdate[] output = RemoteDocumentUpdate.compact(input);
    Assert.assertEquals(3, output.length);
    Assert.assertEquals(1, output[0].seqBegin);
    Assert.assertEquals(3, output[0].seqEnd);
    Assert.assertEquals(4, output[1].seqBegin);
    Assert.assertEquals(5, output[1].seqEnd);
    Assert.assertEquals(6, output[2].seqBegin);
    Assert.assertEquals(6, output[2].seqEnd);
    Assert.assertEquals("{\"x\":3}", output[0].redo);
    Assert.assertEquals("{\"x\":0}", output[0].undo);
    Assert.assertEquals("{\"x\":5}", output[1].redo);
    Assert.assertEquals("{\"x\":3}", output[1].undo);
    Assert.assertEquals("{\"x\":6}", output[2].redo);
    Assert.assertEquals("{\"x\":5}", output[2].undo);
  }

  @Test
  public void trivial_merge() {
    RemoteDocumentUpdate[] input = new RemoteDocumentUpdate[]{UPDATE_1};
    Assert.assertTrue(input == RemoteDocumentUpdate.compact(input));
  }
}
