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
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** manages the relationship between compiling directly and using an external bytecode cache */
public class ManagedAsyncByteCodeCache implements AsyncByteCodeCache {
  private static final Logger LOG = LoggerFactory.getLogger(ManagedAsyncByteCodeCache.class);
  private final ExternalByteCodeSystem extern;
  private final SimpleExecutor offload;
  private final DeploymentMetrics metrics;

  public ManagedAsyncByteCodeCache(ExternalByteCodeSystem extern, SimpleExecutor offload, DeploymentMetrics metrics) {
    this.extern = extern;
    this.offload = offload;
    this.metrics = metrics;
  }

  @Override
  public void fetchOrCompile(String spaceName, String className, String javaSource, String reflection, Callback<CachedByteCode> callback) {
    extern.fetchByteCode(className, new Callback<CachedByteCode>() {
      @Override
      public void success(CachedByteCode value) {
        metrics.deploy_bytecode_found.run();
        callback.success(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        offload.execute(new NamedRunnable("compiler-offload") {
          @Override
          public void execute() throws Exception {
            try {
              metrics.deploy_bytecode_compiled.run();
              CachedByteCode code = SyncCompiler.compile(spaceName, className, javaSource, reflection);
              extern.storeByteCode(className, code, new Callback<>() {
                @Override
                public void success(Void value) {
                  callback.success(code);
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  LOG.error("failed-store-code:" + ex.code);
                  callback.success(code);
                }
              });
            } catch (ErrorCodeException problem) {
              callback.failure(problem);
            }
          }
        });
      }
    });
  }
}
