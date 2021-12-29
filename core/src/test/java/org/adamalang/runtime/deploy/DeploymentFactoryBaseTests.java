/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class DeploymentFactoryBaseTests {
    @Test
    public void notFound() {
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        base.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
            @Override
            public void success(LivingDocumentFactory value) {
                Assert.fail();
            }

            @Override
            public void failure(ErrorCodeException ex) {
                Assert.assertEquals(134214, ex.code);
            }
        });
    }
}
