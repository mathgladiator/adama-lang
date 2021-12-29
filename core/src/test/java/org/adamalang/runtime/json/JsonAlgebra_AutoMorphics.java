/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_AutoMorphics {
    @Test
    public void merge_stream() {
        AutoMorphicAccumulator<String> accum = JsonAlgebra.mergeAccumulator();
        Assert.assertTrue(accum.empty());
        accum.next("{\"x\":1}");
        Assert.assertFalse(accum.empty());
        accum.next("{\"x\":2}");
        accum.next("{\"x\":3}");
        accum.next("{\"x\":4}");
        Assert.assertEquals("{\"x\":4}", accum.finish());
    }

    @Test
    public void roll_forward_1() {
        AutoMorphicAccumulator<String> accum = JsonAlgebra.rollUndoForwardAccumulator("{\"x\":1}");
        accum.next("{\"x\":3}");
        Assert.assertEquals("{}", accum.finish());
    }

    @Test
    public void roll_forward_2() {
        AutoMorphicAccumulator<String> accum = JsonAlgebra.rollUndoForwardAccumulator("{\"x\":1}");
        Assert.assertFalse(accum.empty());
        accum.next("{\"y\":3}");
        Assert.assertEquals("{\"x\":1}", accum.finish());
    }
}
