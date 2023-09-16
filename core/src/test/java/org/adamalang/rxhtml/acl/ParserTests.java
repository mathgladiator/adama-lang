/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.rxhtml.acl;

import org.adamalang.rxhtml.acl.commands.*;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.config.Feedback;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ParserTests {
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
  public void cmd_goto1() throws Exception {
    Goto goto_ = (Goto) (Parser.parse("goto:/view/x-yz-{data}").get(0));
    assertIs(goto_, "var a = {};\n" + "$.YS(State,a,'data');\n" + "$.onGO(DOM,'type',State,function(){ return \"/view/x-yz-\" + a['data'];});\n");
  }

  private static void assertIs(Command command, String expected) {
    Environment env = Environment.fresh(Feedback.NoOp);
    command.write(env.stateVar("State"), "type", "DOM");
    Assert.assertEquals(expected, env.writer.toString());
  }

  @Test
  public void cmd_goto2() throws Exception {
    Goto goto_ = (Goto) (Parser.parse("goto:/fixed").get(0));
    assertIs(goto_, "$.onGO(DOM,'type',State,'/fixed');\n");
  }

  @Test
  public void custom() throws Exception {
    Custom custom = (Custom) (Parser.parse("custom:xyz").get(0));
    Assert.assertEquals("xyz", custom.command);
    assertIs(custom, "$.exCC(DOM,'type',State,'xyz');\n");
  }

  @Test
  public void forceAuth() throws Exception {
    ForceAuth fa = (ForceAuth) (Parser.parse("force-auth:name=identity").get(0));
    Assert.assertEquals("name", fa.name);
    Assert.assertEquals("identity", fa.identity);
    assertIs(fa, "$.onFORCE_AUTH(DOM,'type','name','identity');\n");
  }

  @Test
  public void decide1() throws Exception {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("id", decide.key);
    Assert.assertEquals("id", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'id','xzzzzzzzzzz','id');\n");
  }

  @Test
  public void decide2() throws Exception {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz|wut").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("wut", decide.key);
    Assert.assertEquals("id", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'id','xzzzzzzzzzz','wut');\n");
  }

  @Test
  public void decide3() throws Exception {
    Decide decide = (Decide) (Parser.parse("decide:xzzzzzzzzzz|wut|the").get(0));
    Assert.assertEquals("xzzzzzzzzzz", decide.channel);
    Assert.assertEquals("wut", decide.key);
    Assert.assertEquals("the", decide.path);
    assertIs(decide, "$.exD(DOM,'type',State,'the','xzzzzzzzzzz','wut');\n");
  }

  @Test
  public void dec1() throws Exception {
    Decrement dec = (Decrement) (Parser.parse("dec:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void dec2() throws Exception {
    Decrement dec = (Decrement) (Parser.parse("dec:data:xyz").get(0));
    Assert.assertEquals("data:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pD(State),'xyz', -1);\n");
  }

  @Test
  public void decrement() throws Exception {
    Decrement dec = (Decrement) (Parser.parse("decrement:view:xyz").get(0));
    Assert.assertEquals("view:xyz", dec.path);
    assertIs(dec, "$.onD(DOM,'type',$.pV(State),'xyz', -1);\n");
  }

  @Test
  public void inc1() throws Exception {
    Increment inc = (Increment) (Parser.parse("inc:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void inc2() throws Exception {
    Increment inc = (Increment) (Parser.parse("inc:data:xyz").get(0));
    Assert.assertEquals("data:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pD(State),'xyz', 1);\n");
  }

  @Test
  public void increment() throws Exception {
    Increment inc = (Increment) (Parser.parse("increment:view:xyz").get(0));
    Assert.assertEquals("view:xyz", inc.path);
    assertIs(inc, "$.onD(DOM,'type',$.pV(State),'xyz', 1);\n");
  }

  @Test
  public void raise1() throws Exception {
    Raise raise = (Raise) (Parser.parse("raise:xyz").get(0));
    Assert.assertEquals("view:xyz", raise.path);
    assertIs(raise, "$.onS(DOM,'type',$.pV(State),'xyz',true);\n");
  }

  @Test
  public void raise2() throws Exception {
    Raise raise = (Raise) (Parser.parse("raise:data:xyz").get(0));
    Assert.assertEquals("data:xyz", raise.path);
    assertIs(raise, "$.onS(DOM,'type',$.pD(State),'xyz',true);\n");
  }

  @Test
  public void lower1() throws Exception {
    Lower lower = (Lower) (Parser.parse("lower:xyz").get(0));
    Assert.assertEquals("view:xyz", lower.path);
    assertIs(lower, "$.onS(DOM,'type',$.pV(State),'xyz',false);\n");
  }

  @Test
  public void lower2() throws Exception {
    Lower lower = (Lower) (Parser.parse("lower:data:xyz").get(0));
    Assert.assertEquals("data:xyz", lower.path);
    assertIs(lower, "$.onS(DOM,'type',$.pD(State),'xyz',false);\n");
  }

  @Test
  public void toggle1() throws Exception {
    Toggle toggle = (Toggle) (Parser.parse("toggle:xyz").get(0));
    Assert.assertEquals("view:xyz", toggle.path);
    assertIs(toggle, "$.onT(DOM,'type',$.pV(State),'xyz');\n");
  }

  @Test
  public void toggle2() throws Exception {
    Toggle toggle = (Toggle) (Parser.parse("toggle:data:xyz").get(0));
    Assert.assertEquals("data:xyz", toggle.path);
    assertIs(toggle, "$.onT(DOM,'type',$.pD(State),'xyz');\n");
  }

  @Test
  public void set1() throws Exception {
    Set set = (Set) (Parser.parse("set:xyz=val").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("val", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pV(State),'xyz','val');\n");
  }

  @Test
  public void set2() throws Exception {
    Set set = (Set) (Parser.parse("set:data:xyz=true").get(0));
    Assert.assertEquals("data:xyz", set.path);
    Assert.assertEquals("true", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pD(State),'xyz',true);\n");
  }

  @Test
  public void set3() throws Exception {
    Set set = (Set) (Parser.parse("set:xyz=123").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("123", set.value);
    assertIs(set, "$.onS(DOM,'type',$.pV(State),'xyz',123);\n");
  }

  @Test
  public void set4() throws Exception {
    Set set = (Set) (Parser.parse("set:xyz={xyz}").get(0));
    Assert.assertEquals("view:xyz", set.path);
    Assert.assertEquals("{xyz}", set.value);
    assertIs(set, "var a = {};\n" + "$.YS(State,a,'xyz');\n" + "$.onS(DOM,'type',$.pV(State),'xyz',function(){ return a['xyz'];});\n");
  }

}
