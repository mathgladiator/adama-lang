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
package org.adamalang.common.template.fragment;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class FragmentTests {
  @Test
  public void coverage() {
    Fragment f = new Fragment(FragmentType.Text, "X");
  }

  @Test
  public void restore_closing_bracket() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [");
    Assert.assertEquals(1, fragments.size());
    Assert.assertEquals("Text:[Hi []", fragments.get(0).toString());
  }

  @Test
  public void fragmentization_simple() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [[world]] \t\n[[person]]");
    Assert.assertEquals(4, fragments.size());
    Assert.assertEquals("Text:[Hi ]", fragments.get(0).toString());
    Assert.assertEquals("Expression:[world]", fragments.get(1).toString());
    Assert.assertEquals("Text:[ \t\n" + "]", fragments.get(2).toString());
    Assert.assertEquals("Expression:[person]", fragments.get(3).toString());
  }

  @Test
  public void fragmentization_compound() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [[world|dirty]]");
    Assert.assertEquals(2, fragments.size());
    Assert.assertEquals("Text:[Hi ]", fragments.get(0).toString());
    Assert.assertEquals("Expression:[world, |, dirty]", fragments.get(1).toString());
  }

  @Test
  public void fragmentization_brace_solo() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [ [ {");
    Assert.assertEquals(1, fragments.size());
    Assert.assertEquals("Text:[Hi [ [ {]", fragments.get(0).toString());
  }


  @Test
  public void fragmentization_brace_empty() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [[]]");
    Assert.assertEquals(2, fragments.size());
    Assert.assertEquals("Text:[Hi ]", fragments.get(0).toString());
    Assert.assertEquals("Text:[[]", fragments.get(1).toString());
  }

  @Test
  public void fragmentization_empty_param() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [[thing |+-]]");
    Assert.assertEquals(2, fragments.size());
    Assert.assertEquals("Text:[Hi ]", fragments.get(0).toString());
    Assert.assertEquals("Expression:[thing, |, +, -]", fragments.get(1).toString());
  }

  @Test
  public void fragmentization_conditions() {
    ArrayList<Fragment> fragments = Fragment.parse("Hi [[#world]] \t\n[[/world]][[^p]]not-p[[/p]]");
    Assert.assertEquals(7, fragments.size());
    Assert.assertEquals("Text:[Hi ]", fragments.get(0).toString());
    Assert.assertEquals("If:[world]", fragments.get(1).toString());
    Assert.assertEquals("Text:[ \t\n" + "]", fragments.get(2).toString());
    Assert.assertEquals("End:[world]", fragments.get(3).toString());
    Assert.assertEquals("IfNot:[p]", fragments.get(4).toString());
    Assert.assertEquals("Text:[not-p]", fragments.get(5).toString());
    Assert.assertEquals("End:[p]", fragments.get(6).toString());
  }

  @Test
  public void incomplete() {
    try {
      Fragment.parse("Hi [[#world]xyz");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("']' encountered without additional ']' during scan", re.getMessage());
    }
  }

  @Test
  public void eos1() {
    try {
      Fragment.parse("Hi [[#world");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("scan() failed due to missing ']'", re.getMessage());
    }
  }

  @Test
  public void eos2() {
    try {
      Fragment.parse("Hi [[#world]");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("']' encountered without additional ']' during scan", re.getMessage());
    }
  }
}
