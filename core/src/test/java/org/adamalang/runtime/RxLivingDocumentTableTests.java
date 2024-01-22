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

public class RxLivingDocumentTableTests {
  private static NtPrincipal A = new NtPrincipal("A", "TEST");
  @Test
  public void formula_add() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; }" +
              "table<R> tbl;" +
              "message M {int val; }" +
              "public formula v1 = (iterate tbl where id == 1)[0];" +
              "public formula v2 = (iterate tbl where id == 2)[0];" +
              "public formula v3 = (iterate tbl where id == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void formula_raw() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; }" +
              "table<R> tbl;" +
              "message M {int val; index val; }" +
              "public formula v1 = (iterate tbl where val == 1)[0];" +
              "public formula v2 = (iterate tbl where val == 2)[0];" +
              "public formula v3 = (iterate tbl where val == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());

    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void formula_idx() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; index val; }" +
              "table<R> tbl;" +
              "message M {int val; index val; }" +
              "public formula v1 = (iterate tbl where val == 1)[0];" +
              "public formula v2 = (iterate tbl where val == 2)[0];" +
              "public formula v3 = (iterate tbl where val == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());

    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_add() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; }" +
              "table<R> tbl;" +
              "message M {int val; }" +
              "bubble v1 = (iterate tbl where id == 1)[0];" +
              "bubble v2 = (iterate tbl where id == 2)[0];" +
              "bubble v3 = (iterate tbl where id == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());
    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_raw() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; }" +
              "table<R> tbl;" +
              "message M {int val; index val; }" +
              "bubble v1 = (iterate tbl where val == 1)[0];" +
              "bubble v2 = (iterate tbl where val == 2)[0];" +
              "bubble v3 = (iterate tbl where val == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());

    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }

  @Test
  public void bubble_idx() throws Exception {
    try {
      RealDocumentSetup setup = new RealDocumentSetup(
          "@static { create { return true; } }" +
              "@connected { return true; }" +
              "record R { public int id; public int val; index val; }" +
              "table<R> tbl;" +
              "message M {int val; index val; }" +
              "bubble v1 = (iterate tbl where val == 1)[0];" +
              "bubble v2 = (iterate tbl where val == 2)[0];" +
              "bubble v3 = (iterate tbl where val == 3)[0];" +
              "channel add(M m) { tbl <- m; }",
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
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":1}", new RealDocumentSetup.AssertInt(5));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":2}", new RealDocumentSetup.AssertInt(6));
      setup.document.send(ContextSupport.WRAP(A), null, null, "add", "{\"val\":3}", new RealDocumentSetup.AssertInt(7));
      Assert.assertEquals(4, list.size());
      Assert.assertEquals("{\"seq\":4}", list.get(0));
      Assert.assertEquals("{\"data\":{\"v1\":{\"id\":1,\"val\":1}},\"seq\":5}", list.get(1));
      Assert.assertEquals("{\"data\":{\"v2\":{\"id\":2,\"val\":2}},\"seq\":6}", list.get(2));
      Assert.assertEquals("{\"data\":{\"v3\":{\"id\":3,\"val\":3}},\"seq\":7}", list.get(3));
      System.out.println("COST:" + setup.document.document().__getCodeCost());

    } catch (RuntimeException re) {
      re.printStackTrace();
    }
  }
}
