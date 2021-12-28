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

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class LivingDocumentFactoryTests {
  @Test
  public void almostOK() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "\nimport org.adamalang.runtime.contracts.DocumentMonitor;import org.adamalang.runtime.natives.*;\n class Foo { public Foo(DocumentMonitor dm) {} public static boolean __onCanCreate(NtClient who, NtCreateContext context) { return false; } }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(115747, nsme.code);
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
      Assert.assertEquals(180258, nsme.code);
    }
    Assert.assertTrue(failed);
  }

  @Test
  public void castFailure() throws Exception {
    final var compiler = new LivingDocumentFactory("Foo", "\nimport org.adamalang.runtime.contracts.DocumentMonitor;import org.adamalang.runtime.natives.*;\n class Foo { public Foo(DocumentMonitor dm) {} public static boolean __onCanCreate(NtClient who, NtCreateContext context) { return false; } }", "{}");
    var success = false;
    try {
      compiler.create(null);
      success = true;
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(115747, nsme.code);
    }
    Assert.assertFalse(success);
  }

  @Test
  public void noConstructor() throws Exception {
    try {
      new LivingDocumentFactory("Foo", "import org.adamalang.runtime.natives.*; class Foo { public static boolean __onCanCreate(NtClient who, NtCreateContext context) { return false; } }", "{}");
      Assert.fail();
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(198174, nsme.code);
    }
  }

  @Test
  public void noPolicy() throws Exception {
    try {
      new LivingDocumentFactory("Foo", "import org.adamalang.runtime.contracts.DocumentMonitor; class Foo { public Foo(DocumentMonitor dm) {} }", "{}");
      Assert.fail();
    } catch (final ErrorCodeException nsme) {
      Assert.assertEquals(198174, nsme.code);
    }
  }
}
