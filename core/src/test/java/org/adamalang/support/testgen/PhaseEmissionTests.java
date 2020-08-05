/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import org.junit.Test;

public class PhaseEmissionTests {
    @Test
    public void coverage() {
        StringBuilder output = new StringBuilder();
        PhaseEmission.report("X", "Y", output);
    }
}
