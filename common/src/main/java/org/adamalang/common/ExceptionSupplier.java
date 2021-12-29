/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
