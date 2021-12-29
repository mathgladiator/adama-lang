/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

import java.util.Objects;

/** A document is identified by a key */
public class Key implements Comparable<Key> {
    public final String space;
    public final String key;
    private final int cachedHashCode;

    public Key(String space, String key) {
        this.space = space;
        this.key = key;
        this.cachedHashCode = Math.abs(Objects.hash(space, key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key1 = (Key) o;
        return Objects.equals(space, key1.space) && Objects.equals(key, key1.key);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public int compareTo(Key o) {
        if (this == o) return 0;
        int delta = space.compareTo(o.space);
        if (delta == 0) {
            delta = key.compareTo(o.key);
        }
        return delta;
    }
}
