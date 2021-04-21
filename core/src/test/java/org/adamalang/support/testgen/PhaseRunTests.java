/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class PhaseRunTests {
    @Test
    public void disconnect() {
        PhaseRun.wrap((x) -> {}).disconnect();
    }

    @Test
    public void mustBeTrueCoverage() {
        PhaseRun.mustBeTrue(true, "x");
        try {
            PhaseRun.mustBeTrue(false, "xyz");
            Assert.fail();
        } catch(RuntimeException re) {
            Assert.assertEquals("xyz", re.getMessage());
        }
    }
}
