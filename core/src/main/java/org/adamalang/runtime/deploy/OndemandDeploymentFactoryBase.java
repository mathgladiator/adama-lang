/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;

/** ensures an instance is always alive by fetching plans... on demand  */
public class OndemandDeploymentFactoryBase implements LivingDocumentFactoryFactory  {
  private final DeploymentFactoryBase base;
  private final PlanFetcher fetcher;
  private final DeploySync deploySync;

  public OndemandDeploymentFactoryBase(DeploymentFactoryBase base, PlanFetcher fetcher, DeploySync deploySync) {
    this.base = base;
    this.fetcher = fetcher;
    this.deploySync = deploySync;
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    if (base.contains(key.space)) {
      base.fetch(key, callback);
    } else {
      fetcher.find(key.space, new Callback<>() {
        @Override
        public void success(DeploymentBundle bundle) {
          try {
            base.deploy(key.space, bundle.plan, bundle.keys);
            deploySync.watch(key.space);
            base.fetch(key, callback);
          } catch (ErrorCodeException ex) {
            callback.failure(ex);
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    }
  }

  @Override
  public Collection<String> spacesAvailable() {
    return null;
  }
}
