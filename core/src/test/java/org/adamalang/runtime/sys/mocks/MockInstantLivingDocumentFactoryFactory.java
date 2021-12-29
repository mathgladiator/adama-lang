/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;

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
}
