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
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.StreamHandle;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PrivateViewTests {
  @Test
  public void killing() {
    ArrayList<String> list = new ArrayList<>();
    PrivateView pv =
        new PrivateView(3,
            NtPrincipal.NO_ONE,
            new Perspective() {
              @Override
              public void data(String data) {
                list.add(data);
              }

              @Override
              public void disconnect() {}
            }) {

          @Override
          public long memory() {
            return 0;
          }

          @Override
          public void ingest(JsonStreamReader reader) {}

          @Override
          public void dumpViewer(JsonStreamWriter writer) {}

          @Override
          public void update(JsonStreamWriter writer) {}
        };
    Assert.assertTrue(pv.isAlive());
    pv.kill();
    Assert.assertFalse(pv.isAlive());
    pv.deliver("{}");
    Assert.assertEquals("{}", list.get(0));
  }

  @Test
  public void usurp() {
    ArrayList<String> list = new ArrayList<>();
    AtomicBoolean gotViewUpdate = new AtomicBoolean(false);
    PrivateView pv1 =
        new PrivateView(2,
            NtPrincipal.NO_ONE,
            new Perspective() {
              @Override
              public void data(String data) {
                list.add(data);
              }

              @Override
              public void disconnect() {}
            }) {

          @Override
          public long memory() {
            return 0;
          }

          @Override
          public void ingest(JsonStreamReader reader) { }

          @Override
          public void dumpViewer(JsonStreamWriter writer) {}

          @Override
          public void update(JsonStreamWriter writer) {}
        };
    StreamHandle handle = new StreamHandle(pv1);
    pv1.link(handle);

    PrivateView pv2 =
        new PrivateView(1, NtPrincipal.NO_ONE, pv1.perspective) {

          @Override
          public long memory() {
            return 0;
          }

          @Override
          public void ingest(JsonStreamReader reader) { gotViewUpdate.set(true); }

          @Override
          public void dumpViewer(JsonStreamWriter writer) {}

          @Override
          public void update(JsonStreamWriter writer) {}
        };
    AtomicInteger iv = new AtomicInteger(0);
    pv2.setRefresh(() -> iv.addAndGet(2));
    pv1.setRefresh(() -> iv.addAndGet(5));
    handle.triggerRefresh();
    pv1.usurp(pv2);
    handle.triggerRefresh();
    Assert.assertEquals(7, iv.get());
    Assert.assertFalse(pv1.isAlive());
    Assert.assertFalse(gotViewUpdate.get());
    handle.ingestViewUpdate(new JsonStreamReader("{}"));;
    Assert.assertTrue(gotViewUpdate.get());
    handle.kill();
    Assert.assertFalse(pv1.isAlive());
  }

  @Test
  public void futures() {
    ArrayList<String> list = new ArrayList<>();
    PrivateView pv =
        new PrivateView(4,
            NtPrincipal.NO_ONE,
            new Perspective() {
              @Override
              public void data(String data) {
                list.add(data);
              }

              @Override
              public void disconnect() {}
            }) {

          @Override
          public long memory() {
            return 0;
          }

          @Override
          public void ingest(JsonStreamReader reader) {}

          @Override
          public void dumpViewer(JsonStreamWriter writer) {}

          @Override
          public void update(JsonStreamWriter writer) {}
        };
    Assert.assertFalse(pv.futures("\"outstanding\":[],\"blockers\":[]"));
    Assert.assertTrue(pv.futures("XYZ"));
    Assert.assertFalse(pv.futures("XYZ"));
  }
}
