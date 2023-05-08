/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.acl;

import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.acl.commands.*;
import org.adamalang.rxhtml.template.Environment;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ParserTests {
  private static void assertIs(Command command, String expected) {
    Environment env = Environment.fresh(Feedback.NoOp);
    command.write(env.stateVar("State"), "type", "DOM");
    Assert.assertEquals(expected, env.writer.toString());
  }

  @Test
  public void fragments() {
    ArrayList<String> f;
    {
      f = Parser.fragmentize("set:x='42 42'");
      Assert.assertEquals(1, f.size());
      Assert.assertEquals("set:x=42 42", f.get(0));
    }
    {
      f = Parser.fragmentize("set:x='42 42' toggle:x   goto:'[ happy ]'   ");
      Assert.assertEquals(3, f.size());
      Assert.assertEquals("set:x=42 42", f.get(0));
      Assert.assertEquals("toggle:x", f.get(1));
      Assert.assertEquals("goto:[ happy ]", f.get(2));
    }
  }

  @Test
  public void cmd_goto1() {
    Goto goto_ = (Goto) (Parser.parse("goto:/view/x-yz-{data}").get(0));
    assertIs(goto_, "var a = {};\n" + "$.YS(State,a,'data');\n" + "$.onGO(DOM,'type',State,function(){ return \"/view/x-yz-\" + a['data'];});\n");
  }

  @Test
  public void cmd_goto2() {
    Goto goto_ = (Goto) (Parser.parse("goto:/fixed").get(0));
    assertIs(goto_, "$.onGO(DOM,'type',State,'/fixed');\n");
  }

  @Test
  public void custom() {
    Custom custom = (Custom) (Parser.parse("custom:xyz").get(0));
    Assert.assertEquals("xyz", custom.command);
    assertIs(custom, "$.exCC(DOM,'type',State,'xyz');\n");
  }

  @Test
  public void forceAuth() {
    ForceAuth fa = (ForceAuth) (Parser.parse("force-auth:name=identity").get(0));
    Assert.assertEquals("name", fa.name);
    Assert.assertEquals("identity", fa.identity);
    assertIs(fa, "$.onFORCE_AUTH(DOM,'type','name','identity');\n");
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
  public void dec1() {
    Decrement dec = (Decrement) (Parser.parse("dec:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void dec2() {
    Decrement dec = (Decrement) (Parser.parse("dec:data:xyz").get(0));
    Assert.assertEquals("data:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pD(State),'xyz', -1);\n");
  }

  @Test
  public void decrement() {
    Decrement dec = (Decrement) (Parser.parse("decrement:view:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void inc1() {
    Increment inc = (Increment) (Parser.parse("inc:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void inc2() {
    Increment inc = (Increment) (Parser.parse("inc:data:xyz").get(0));
    Assert.assertEquals("data:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pD(State),'xyz', 1);\n");
  }

  @Test
  public void increment() {
    Increment inc = (Increment) (Parser.parse("increment:view:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void raise1() {
    Raise raise = (Raise) (Parser.parse("raise:xyz").get(0));
    Assert.assertEquals("view:xyz", raise.path);
    assertIs(raise, "$.onS(DOM,'type',$.pV(State),'xyz',true);\n");
  }

  @Test
  public void raise2() {
    Raise raise = (Raise) (Parser.parse("raise:data:xyz").get(0));
    Assert.assertEquals("data:xyz", raise.path);
    assertIs(raise, "$.onS(DOM,'type',$.pD(State),'xyz',true);\n");
  }

  @Test
  public void lower1() {
    Lower lower = (Lower) (Parser.parse("lower:xyz").get(0));
    Assert.assertEquals("view:xyz", lower.path);
    assertIs(lower, "$.onS(DOM,'type',$.pV(State),'xyz',false);\n");
  }

  @Test
  public void lower2() {
    Lower lower = (Lower) (Parser.parse("lower:data:xyz").get(0));
    Assert.assertEquals("data:xyz", lower.path);
    assertIs(lower, "$.onS(DOM,'type',$.pD(State),'xyz',false);\n");
  }

  @Test
  public void toggle1() {
    Toggle toggle = (Toggle) (Parser.parse("toggle:xyz").get(0));
    Assert.assertEquals("view:xyz", toggle.path);
    assertIs(toggle, "$.onT(DOM,'type',$.pV(State),'xyz');\n");
  }

  @Test
  public void toggle2() {
    Toggle toggle = (Toggle) (Parser.parse("toggle:data:xyz").get(0));
    Assert.assertEquals("data:xyz", toggle.path);
    assertIs(toggle, "$.onT(DOM,'type',$.pD(State),'xyz');\n");
  }

  @Test
  public void set1() {
    Set set = (Set) (Parser.parse("set:xyz=val").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("val", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pV(State),'xyz','val');\n");
  }

  @Test
  public void set2() {
    Set set = (Set) (Parser.parse("set:data:xyz=true").get(0));
    Assert.assertEquals("data:xyz", set.path);
    Assert.assertEquals("true", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pD(State),'xyz',true);\n");
  }

  @Test
  public void set3() {
    Set set = (Set) (Parser.parse("set:xyz=123").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("123", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pV(State),'xyz',123);\n");
  }

  @Test
  public void set4() {
    Set set = (Set) (Parser.parse("set:xyz={xyz}").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("{xyz}", set.value);
    assertIs(set, "var a = {};\n" + "$.YS(State,a,'xyz');\n" + "$.onS(DOM,'type',$.pV(State),'xyz',function(){ return a['xyz'];});\n");
  }

}
