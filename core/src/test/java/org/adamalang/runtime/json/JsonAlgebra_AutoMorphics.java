package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_AutoMorphics {
    @Test
    public void merge_stream() {
        AutoMorphicAccumulator<String> accum = JsonAlgebra.mergeAccumulator();
        accum.next("{\"x\":1}");
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
        accum.next("{\"y\":3}");
        Assert.assertEquals("{\"x\":1}", accum.finish());
    }
}
