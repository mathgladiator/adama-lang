package org.adamalang.runtime.sys.mocks;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.exceptions.ErrorCodeException;
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
