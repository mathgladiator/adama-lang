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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtComplex;

public class DComplex {
    private NtComplex prior;

    public DComplex() {
        prior = null;
    }

    /** the double is no longer visible (was made private) */
    public void hide(final PrivateLazyDeltaWriter writer) {
        if (prior != null) {
            writer.writeNull();
            prior = null;
        }
    }

    /** the double is visible, so show changes */
    public void show(final NtComplex value, final PrivateLazyDeltaWriter writer) {
        if (prior == null || !value.equals(prior)) {
            writer.writeNtComplex(value);
        }
        prior = value;
    }
}
