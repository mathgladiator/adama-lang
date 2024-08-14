/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.rxhtml.routing;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class PathTests {
  @Test
  public void root() {
    Path root = new Path("root");
    root.set(new MockTarget(123));
    Assert.assertEquals(123, ((MockTarget) root.route(0, new String[] {}, new TreeMap<>())).value);
    Assert.assertEquals(48, root.memory());
  }

  @Test
  public void root_as_empty() {
    Path root = new Path("root");
    root.diveFixed("").set(new MockTarget(123));
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/"), new TreeMap<>())).value);
    Assert.assertEquals(152, root.memory());
  }

  @Test
  public void fixed() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").set(new MockTarget(123)));
    Assert.assertTrue(root.diveFixed("a").set(new MockTarget(100)));
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz"), new TreeMap<>())).value);
    Assert.assertEquals(100, ((MockTarget) root.route(0, Path.parsePath("/a"), new TreeMap<>())).value);
    Assert.assertEquals(264, root.memory());
  }

  @Test
  public void var_number_1() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newNumber("x").set(new MockTarget(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/42"), captured)).value);
    Assert.assertEquals("42", captured.get("x"));
    Assert.assertEquals(200, root.memory());
  }


  @Test
  public void var_number_1_fail() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").set(new MockTarget(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/joe"), captured));
    Assert.assertNull(captured.get("x"));
  }

  @Test
  public void var_number_2() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").newNumber("y").set(new MockTarget(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/42/13"), captured)).value);
    Assert.assertEquals("42", captured.get("x"));
    Assert.assertEquals("13", captured.get("y"));
    Assert.assertEquals(242, root.memory());
  }

  @Test
  public void var_text() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(new MockTarget(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/joe"), captured)).value);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void no_leading_slash() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(new MockTarget(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("xyz/joe"), captured)).value);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void eat_trailing_slash() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(new MockTarget(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/joe///"), captured)).value);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void dupes() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").set(new MockTarget(123)));
    Assert.assertFalse(root.diveFixed("xyz").set(new MockTarget(42)));
  }

  @Test
  public void var_backtracking_text() {
    Path root = new Path("root");
    root.diveFixed("xyz").newText("x").diveFixed("edit").set(new MockTarget(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/joe/nope"), captured));
    Assert.assertNull(captured.get("x"));
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/joe/edit"), captured)).value);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void var_backtracking_num() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").diveFixed("edit").set(new MockTarget(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/42/nope"), captured));
    Assert.assertNull(captured.get("x"));
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/42/edit"), captured)).value);
    Assert.assertEquals("42", captured.get("x"));
  }

  @Test
  public void suffix() {
    Path root = new Path("root");
    root.diveFixed("xyz").setSuffix("v").set(new MockTarget(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, ((MockTarget) root.route(0, Path.parsePath("/xyz/joe/edit"), captured)).value);
    Assert.assertEquals("joe/edit", captured.get("v"));
    Assert.assertEquals(200, root.memory());
  }
}
