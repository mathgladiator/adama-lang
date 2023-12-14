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
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;
import java.util.HashMap;

/** ensures an instance is always alive by fetching plans... on demand  */
public class OndemandDeploymentFactoryBase implements LivingDocumentFactoryFactory, Undeploy, Deploy {
  private final DeploymentMetrics metrics;
  private final DeploymentFactoryBase base;
  private final PlanFetcher fetcher;
  private final DeploySync sync;

  public OndemandDeploymentFactoryBase(DeploymentMetrics metrics, DeploymentFactoryBase base, PlanFetcher fetcher, DeploySync sync) {
    this.metrics = metrics;
    this.base = base;
    this.fetcher = fetcher;
    this.sync = sync;
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    if (base.contains(key.space)) {
      metrics.deploy_cache_hit.run();
      base.fetch(key, callback);
    } else {
      metrics.deploy_cache_miss.run();
      fetcher.find(key.space, metrics.deploy_plan_fetch.wrap(new Callback<>() {
        @Override
        public void success(DeploymentBundle bundle) {
          base.deploy(key.space, bundle.plan, bundle.keys, new Callback<Void>() {
            @Override
            public void success(Void value) {
              sync.watch(key.space);
              base.fetch(key, callback);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      }));
    }
  }

  @Override
  public void account(HashMap<String, PredictiveInventory.MeteringSample> sample) {
    base.account(sample);
  }

  @Override
  public void deploy(String space, Callback<Void> callback) {
    fetcher.find(space, metrics.deploy_plan_push.wrap(new Callback<DeploymentBundle>() {
      @Override
      public void success(DeploymentBundle bundle) {
        base.deploy(space, bundle.plan, bundle.keys, new Callback<Void>() {
          @Override
          public void success(Void value) {
            sync.watch(space);
            callback.success(null);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }));
  }

  @Override
  public void undeploy(String space) {
    base.undeploy(space);
    metrics.deploy_undo.run();
    sync.unwatch(space);
  }

  @Override
  public Collection<String> spacesAvailable() {
    return base.spacesAvailable();
  }
}
