package org.adamalang.common;

import java.util.function.Supplier;

@FunctionalInterface
public interface ExceptionSupplier<T> {

    public T get() throws Exception;

    public static <T> Supplier<T> TO_RUNTIME(ExceptionSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
