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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Hashing;
import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class ManagedAsyncByteCodeCacheTests {
  @Test
  public void flow() throws Exception {
    HashMap<String, CachedByteCode> cache = new HashMap<>();
    ExternalByteCodeSystem sys = new ExternalByteCodeSystem() {
      @Override
      public void fetchByteCode(String className, Callback<CachedByteCode> callback) {
        CachedByteCode code = cache.get(className);
        if (code != null) {
          System.err.println("FOUND:" + code.className + "::" + code.classBytes.size());
          callback.success(code);
          return;
        }
        System.err.println("FAILED:" + className);
        callback.failure(new ErrorCodeException(404));
      }

      @Override
      public void storeByteCode(String className, CachedByteCode code, Callback<Void> callback) {
        cache.put(className, code);
        System.err.println("STASH:" + className + "/" + code.className.equals(className) + "::" + code.classBytes.size());
        callback.success(null);
      }
    };
    ManagedAsyncByteCodeCache managed = new ManagedAsyncByteCodeCache(sys, SimpleExecutor.NOW);
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertEquals(0, cache.size());
    Assert.assertTrue(AsyncCompilerTests.pump(null, plan, managed) instanceof DeploymentFactory);
    Assert.assertEquals(1, cache.size());
    Assert.assertTrue(AsyncCompilerTests.pump(null, plan, managed) instanceof DeploymentFactory);
    Assert.assertEquals(1, cache.size());
  }
}
