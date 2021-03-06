/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.jvm;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentFactoryTests {
  @Test
  public void almostOK() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(DocumentMonitor dm) {} }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(3002, nsme.code);
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
      Assert.assertEquals(3001, nsme.code);
    }
    Assert.assertTrue(failed);
  }

  @Test
  public void castFailure() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "\nimport org.adamalang.runtime.contracts.DocumentMonitor;\n class Foo { public Foo(DocumentMonitor dm) {} }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(3002, nsme.code);
    }
    Assert.assertFalse(success);
  }

  @Test
  public void noConstructor() throws Exception {
    try {
      new LivingDocumentFactory("Foo", "class Foo {}", "{}");
      Assert.fail();
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(3000, nsme.code);
    }
  }
}
