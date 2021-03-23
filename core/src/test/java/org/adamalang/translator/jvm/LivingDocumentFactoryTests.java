/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.jvm;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentFactoryTests {
  @Test
  public void almostOK() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "import com.fasterxml.jackson.databind.node.ObjectNode;\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(DocumentMonitor dm) {} }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(5002, nsme.code);
    }
    Assert.assertFalse(success);
  }

  @Test
  public void badCode() throws Exception {
    var failed = true;
    try {
      new LivingDocumentFactory("Foo", "import org.adamalang.runtime.reactives.RxObject;\n class Foo { public Foo(}", "{}");
      failed = false;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(5004, nsme.code);
    }
    Assert.assertTrue(failed);
  }

  @Test
  public void castFailure() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "import com.fasterxml.jackson.databind.node.ObjectNode;\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(DocumentMonitor dm) {} }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(5002, nsme.code);
    }
    Assert.assertFalse(success);
  }

  @Test
  public void noConstructor() throws Exception {
    try {
      new LivingDocumentFactory("Foo", "class Foo {}", "{}");
      Assert.fail();
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(5005, nsme.code);
    }
  }
}
