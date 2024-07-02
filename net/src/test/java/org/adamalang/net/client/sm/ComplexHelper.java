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
package org.adamalang.net.client.sm;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.net.TestBed;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ComplexHelper {
  public static final String SIMPLE = "@static { create { return true; } } @connected { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; } @can_attach { return true; } @attached(what) {} ";
  public static final String VIEW_MIRROR = "@static { create { return true; } } @connected { return true; } view int z; bubble zz = 1000 + @viewer.z; public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; } @can_attach { return true; } @attached(what) {} ";
  public static final String BAD_CODE = "@can_attach { int x = 1; while(true) { x++; } return true; } @attached(what) { while(true) {} } @static { create { return true; } } @connected { return true; } message M {} channel foo(M y) { while(true) {} }  ";

  public static void spinUpCapacity(TestBed[] servers, boolean start, String code) throws Exception {
    for (int k = 0; k < servers.length; k++) {
      servers[k] = new TestBed(20005 + k, code);
      CountDownLatch latchMade = new CountDownLatch(1);
      servers[k].coreService.create(
          new CoreRequestContext(NtPrincipal.NO_ONE, "origin", "ip", "key"),
          new Key("space", "key"),
          "{}",
          null,
          new Callback<Void>() {
            @Override
            public void success(Void value) {
              latchMade.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {}
          });
      Assert.assertTrue(latchMade.await(1000, TimeUnit.MILLISECONDS));
      if (start) {
        servers[k].startServer();
      }
    }
  }

  public static void stopCapacity(TestBed[] servers) {
    try {
      for (int k = 0; k < servers.length; k++) {
        if (servers[k] != null) {
          servers[k].close();
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("SHUTDOWN WENT POORLY");
    }
  }
}
