package org.adamalang.support.testgen;


import org.adamalang.runtime.stdlib.Utility;

public class PhaseReflect {
    public static void go(final PhaseValidate.ValidationResults results, final StringBuilder outputFile) throws Exception {
        outputFile.append("--REFLECTION RESULTS-------------------------------------").append("\n");
        outputFile.append(results.reflection); // TODO: pretty print
        outputFile.append("\n");
    }
}
