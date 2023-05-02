/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;
import java.util.Collections;

public class MockInstantLivingDocumentFactoryFactory implements LivingDocumentFactoryFactory {
  private LivingDocumentFactory factory;

  public MockInstantLivingDocumentFactoryFactory(LivingDocumentFactory factory) {
    this.factory = factory;
  }

  public synchronized void set(LivingDocumentFactory factory) {
    this.factory = factory;
  }

  @Override
  public synchronized void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    if (factory != null) {
      callback.success(factory);
    } else {
      callback.failure(new ErrorCodeException(999));
    }
  }

  @Override
  public Collection<String> spacesAvailable() {
    return Collections.singleton("space");
  }
}
