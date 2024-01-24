/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.runtime;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RxChainTests {
  private static NtPrincipal A = new NtPrincipal("A", "TEST");

  @Test
  public void formula_indirection() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record Items { public int id; public int val; private int group; }" +
              "table<Items> _items;" +
              "record Group { public int id; public int g; public formula s = (iterate _items where group == g).val.sum(); }" +
              "table<Group> _groups;" +
              "public formula yo = iterate _groups;" +
              "message M {int val; int group; }" +
              "@construct { _groups <- {g:1}; _groups <- {g:2}; _groups <- {g:3}; } " +
              "channel add(M m) { _items <- m; }",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };

      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), gv);
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":10,\"group\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":100,\"group\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1000,\"group\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"yo\":{\"1\":{\"id\":1,\"g\":1},\"2\":{\"id\":2,\"g\":2},\"3\":{\"id\":3,\"g\":3},\"@o\":[1,2,3]}},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"yo\":{\"1\":{\"s\":10}}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"yo\":{\"2\":{\"s\":100}}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"yo\":{\"3\":{\"s\":1000}}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_indirection() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record Items { public int id; public int val; private int group; }" +
              "table<Items> _items;" +
              "record Group { public int id; public int g; bubble s = (iterate _items where group == g).val.sum(); }" +
              "table<Group> _groups;" +
              "bubble yo = iterate _groups;" +
              "message M {int val; int group; }" +
              "@construct { _groups <- {g:1}; _groups <- {g:2}; _groups <- {g:3}; } " +
              "channel add(M m) { _items <- m; }",
          null);
      RealDocumentSetup.GotView gv = new RealDocumentSetup.GotView();
      ArrayList<String> list = new ArrayList<>();
      Perspective linked =
          new Perspective() {
            @Override
            public void data(String data) {
              list.add(data);
            }

            @Override
            public void disconnect() {}
          };

      setup.document.connect(ContextSupport.WRAP(A), new RealDocumentSetup.AssertInt(2));
      setup.document.createPrivateView(A, linked, new JsonStreamReader("{}"), gv);
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":10,\"group\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":100,\"group\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1000,\"group\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"yo\":{\"1\":{\"id\":1,\"g\":1},\"2\":{\"id\":2,\"g\":2},\"3\":{\"id\":3,\"g\":3},\"@o\":[1,2,3]}},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"yo\":{\"1\":{\"s\":10}}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"yo\":{\"2\":{\"s\":100}}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"yo\":{\"3\":{\"s\":1000}}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }
}
