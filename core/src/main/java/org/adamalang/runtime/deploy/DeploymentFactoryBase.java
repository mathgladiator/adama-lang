/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** this is the base for all spaces to resolve against */
public class DeploymentFactoryBase implements LivingDocumentFactoryFactory {
  private final AtomicInteger newClassId;
  private final ConcurrentHashMap<String, DeploymentFactory> spaces;

  public DeploymentFactoryBase() {
    this.newClassId = new AtomicInteger(0);
    this.spaces = new ConcurrentHashMap<>();
  }

  public String hashOf(String space) {
    DeploymentFactory factory = this.spaces.get(space);
    if (factory != null) {
      return factory.plan.hash;
    }
    return null;
  }

  public void deploy(String space, DeploymentPlan plan) throws ErrorCodeException {
    spaces.put(space, new DeploymentFactory(space, getSpaceClassNamePrefix(space), newClassId, spaces.get(space), plan));
  }

  /** issue #108; expose this internal bit for others to use to keep sanity in check */
  public static String getSpaceClassNamePrefix(String space) {
    StringBuilder spacePrefix = new StringBuilder().append("Space_");
    for (int k = 0; k < space.length(); k++) {
      char ch = space.charAt(k);
      if (Character.isAlphabetic(ch)) {
        spacePrefix.append(ch);
      }
    }
    spacePrefix.append("_");
    return spacePrefix.toString();
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    DeploymentFactory factory = spaces.get(key.space);
    if (factory == null) {
      callback.failure(new ErrorCodeException(ErrorCodes.DEPLOYMENT_FACTORY_CANT_FIND_SPACE));
      return;
    }
    factory.fetch(key, callback);
  }

  @Override
  public Collection<String> spacesAvailable() {
    return spaces.keySet();
  }
}
