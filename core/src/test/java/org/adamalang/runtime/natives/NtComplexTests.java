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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtComplexTests {
    @Test
    public void equality() {
        NtComplex a = new NtComplex(1.2, 3.4);
        NtComplex b = new NtComplex(1.2, 3.4);
        NtComplex c = new NtComplex(3.4, -1.2);
        Assert.assertEquals(a, a);
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(a, "z");
        Assert.assertNotEquals("z", a);
    }
}
