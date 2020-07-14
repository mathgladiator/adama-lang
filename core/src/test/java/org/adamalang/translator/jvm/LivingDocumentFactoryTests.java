/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentFactoryTests {

    @Test
    public void noConstructor() throws Exception {
        try {
            LivingDocumentFactory compiler = new LivingDocumentFactory("Foo", "class Foo {}");
            Assert.fail();
        } catch (NoSuchMethodException nsme) {
        }
    }

    @Test
    public void castFailure() throws Exception {
        LivingDocumentFactory compiler = new LivingDocumentFactory("Foo", "import com.fasterxml.jackson.databind.node.ObjectNode;\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(ObjectNode t, DocumentMonitor dm) {} }");
        boolean success = false;
        try {
            compiler.create(Utility.createObjectNode(), null);
            success = true;
        } catch (Exception e) {
        }
        Assert.assertFalse(success);
    }

    @Test
    public void almostOK () throws Exception {
        LivingDocumentFactory compiler = new LivingDocumentFactory("Foo", "import com.fasterxml.jackson.databind.node.ObjectNode;\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(ObjectNode t, DocumentMonitor dm) {} }");
    }

    @Test
    public void badCode() throws Exception {
        boolean failed = true;
        try {
            LivingDocumentFactory compiler = new LivingDocumentFactory("Foo", "import org.adamalang.runtime.reactives.RxObject;\n class Foo { public Foo(}");
            failed = false;
        } catch (Exception e) {
        }
        Assert.assertTrue(failed);
    }}
