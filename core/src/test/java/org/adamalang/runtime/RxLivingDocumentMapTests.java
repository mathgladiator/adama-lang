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

public class RxLivingDocumentMapTests {
  private static NtPrincipal A = new NtPrincipal("A", "TEST");
  @Test
  public void formula_lookup() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "public formula v1 = m[1].val;" +
              "public formula v2 = m[2].val;" +
              "public formula v3 = m[3].val;" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":10},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":5},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":7},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_lookup() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "bubble v1 = m[1].val;" +
              "bubble v2 = m[2].val;" +
              "bubble v3 = m[3].val;" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":10},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":5},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":7},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void formula_size() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "public formula s = m.size();" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"s\":0},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"s\":1},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"s\":2},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"s\":3},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_size() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "bubble s = m.size();" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"s\":0},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"s\":1},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"s\":2},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"s\":3},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void formula_aggr() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "procedure sum() -> int readonly { int s = 0; foreach (kvp in m) { s += kvp.value.val; } return s; }" +
              "public formula s = sum();" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"s\":0},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"s\":10},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"s\":15},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"s\":22},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_aggr() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int val; }" +
              "map<int, R> m;" +
              "message M {int key; int val; }" +
              "procedure sum() -> int readonly { int s = 0; foreach (kvp in m) { s += kvp.value.val; } return s; }" +
              "bubble s = sum();" +
              "channel add(M d) { m[d.key] <- {val:d.val}; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":1,\"val\":10}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":2,\"val\":5}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"key\":3,\"val\":7}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"data\":{\"s\":0},\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"s\":10},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"s\":15},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"s\":22},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }
}
