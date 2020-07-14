/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.CanGetAndSet;

import java.util.function.Consumer;

public class MockCanGetAndSet<T> implements CanGetAndSet<T>, Consumer<T> {
    private T value;

    public MockCanGetAndSet() {
        value = null;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public void accept(T t) {
        this.value = t;
    }
}
