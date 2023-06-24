/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.ops;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;

/** a simple way to intercept and handle space failures. This does not ensure the spaces are up to date. */
public class ProxyDeploymentFactory implements LivingDocumentFactoryFactory {
  private final DeploymentFactoryBase base;
  private DeploymentAgent agent;

  public ProxyDeploymentFactory(DeploymentFactoryBase base) {
    this.base = base;
    this.agent = null;
  }

  public void setAgent(DeploymentAgent agent) {
    this.agent = agent;
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    this.base.fetch(key, new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory factory) {
        callback.success(factory);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (ex.code == ErrorCodes.DEPLOYMENT_FACTORY_CANT_FIND_SPACE && agent != null) {
          agent.requestCodeDeployment(key.space, new Callback<Void>() {
            @Override
            public void success(Void value) {
              agent.bind(key.space);
              base.fetch(key, callback);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        } else {
          callback.failure(ex);
        }
      }
    });
  }

  @Override
  public Collection<String> spacesAvailable() {
    return base.spacesAvailable();
  }
}
