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
