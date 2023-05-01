/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class FreeEnvironmentTests {
  @Test
  public void simple() {
    FreeEnvironment environment = FreeEnvironment.root();
    environment.define("x");
    environment.require("x");
    environment.require("y");
    Assert.assertEquals("free:y", environment.toString());
  }

  @Test
  public void record() {
    HashMap<String, String> translate = new HashMap<>();
    translate.put("z", "R::u");
    FreeEnvironment environment = FreeEnvironment.record(translate).push();
    environment.define("x");
    environment.require("x");
    environment.require("z");
    Assert.assertEquals("free:R::u", environment.toString());
  }

  @Test
  public void simple_scope() {
    FreeEnvironment environment = FreeEnvironment.root();
    environment.define("x");
    environment.require("x");
    FreeEnvironment child = environment.push();
    child.define("y");
    environment.require("y");
    Assert.assertEquals("free:y", environment.toString());
  }

  @Test
  public void record_scope() {
    HashMap<String, String> translate = new HashMap<>();
    translate.put("z", "R::u");
    FreeEnvironment environment = FreeEnvironment.record(translate).push();
    environment.define("x");
    environment.require("x");
    environment.require("z");
    Assert.assertEquals("free:R::u", environment.toString());
  }
}
