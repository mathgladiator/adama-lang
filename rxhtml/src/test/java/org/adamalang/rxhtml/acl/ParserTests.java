/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.acl;

import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.acl.commands.*;
import org.adamalang.rxhtml.template.Environment;
import org.junit.Assert;
import org.junit.Test;

public class ParserTests {

  private static void assertIs(Command command, String expected) {
    Environment env = Environment.fresh(Feedback.NoOp);
    command.write(env.stateVar("State"), "type", "DOM");
    Assert.assertEquals(expected, env.writer.toString());
  }

  @Test
  public void custom() {
    Custom custom = (Custom) (Parser.parse("custom:xyz").get(0));
    Assert.assertEquals("xyz", custom.command);
    assertIs(custom, "$.exCC(DOM,'type',State,'xyz');\n");
  }

  @Test
  public void decide1() {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("id", decide.key);
    Assert.assertEquals("id", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'id','xzzzzzzzzzz','id');\n");
  }

  @Test
  public void decide2() {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz|wut").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("wut", decide.key);
    Assert.assertEquals("id", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'id','xzzzzzzzzzz','wut');\n");
  }

  @Test
  public void decide3() {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz|wut|the").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("wut", decide.key);
    Assert.assertEquals("the", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'the','xzzzzzzzzzz','wut');\n");
  }

  @Test
  public void dec() {
    Decrement dec = (Decrement) (Parser.parse("dec:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void decrement() {
    Decrement dec = (Decrement) (Parser.parse("decrement:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void inc() {
    Increment inc = (Increment) (Parser.parse("inc:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void increment() {
    Increment inc = (Increment) (Parser.parse("increment:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void raise() {
    Raise raise = (Raise) (Parser.parse("raise:xyz").get(0));
    Assert.assertEquals("view:xyz", raise.path);
    assertIs(raise, "$.onS(DOM,'type',$.pV(State),'xyz',true);\n");
  }

  @Test
  public void lower() {
    Lower lower = (Lower) (Parser.parse("lower:xyz").get(0));
    Assert.assertEquals("view:xyz", lower.path);
    assertIs(lower, "$.onS(DOM,'type',$.pV(State),'xyz',false);\n");
  }

  @Test
  public void toggle() {
    Toggle toggle = (Toggle) (Parser.parse("toggle:xyz").get(0));
    Assert.assertEquals("view:xyz", toggle.path);
    assertIs(toggle, "$.onT(DOM,'type',$.pV(State),'xyz');\n");
  }

  @Test
  public void set() {
    Set set = (Set) (Parser.parse("set:xyz=val").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("val", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pV(State),'xyz','val');\n");
  }
}
