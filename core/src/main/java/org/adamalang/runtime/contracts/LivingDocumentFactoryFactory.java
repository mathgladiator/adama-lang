/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;

/** This represents where scripts live such that deployments can pull versions based on the key */
public interface LivingDocumentFactoryFactory {
  /** fetch the factory for the given key */
  void fetch(Key key, Callback<LivingDocumentFactory> callback);

  /** fetch the available spaces */
  Collection<String> spacesAvailable();
}
