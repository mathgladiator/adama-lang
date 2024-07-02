/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
