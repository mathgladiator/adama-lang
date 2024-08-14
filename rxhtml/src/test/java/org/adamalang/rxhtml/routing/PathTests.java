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

  private Target mock(int v) {
    return new Target(v, null, null);
  }

  @Test
  public void root() {
    Path root = new Path("root");
    root.set(mock(123));
    Assert.assertEquals(123, root.route(0, new String[] {}, new TreeMap<>()).status);
    Assert.assertEquals(112, root.memory());
  }

  @Test
  public void root_as_empty() {
    Path root = new Path("root");
    root.diveFixed("").set(mock(123));
    Assert.assertEquals(123, root.route(0, Path.parsePath("/"), new TreeMap<>()).status);
    Assert.assertEquals(216, root.memory());
  }

  @Test
  public void fixed() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").set(mock(123)));
    Assert.assertTrue(root.diveFixed("a").set(mock(100)));
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz"), new TreeMap<>()).status);
    Assert.assertEquals(100, root.route(0, Path.parsePath("/a"), new TreeMap<>()).status);
    Assert.assertEquals(392, root.memory());
  }

  @Test
  public void var_number_1() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newNumber("x").set(mock(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/42"), captured).status);
    Assert.assertEquals("42", captured.get("x"));
    Assert.assertEquals(264, root.memory());
  }


  @Test
  public void var_number_1_fail() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").set(mock(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/joe"), captured));
    Assert.assertNull(captured.get("x"));
  }

  @Test
  public void var_number_2() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").newNumber("y").set(mock(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/42/13"), captured).status);
    Assert.assertEquals("42", captured.get("x"));
    Assert.assertEquals("13", captured.get("y"));
    Assert.assertEquals(306, root.memory());
  }

  @Test
  public void var_text() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(mock(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/joe"), captured).status);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void no_leading_slash() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(mock(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("xyz/joe"), captured).status);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void eat_trailing_slash() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").newText("x").set(mock(123)));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/joe///"), captured).status);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void dupes() {
    Path root = new Path("root");
    Assert.assertTrue(root.diveFixed("xyz").set(mock(123)));
    Assert.assertFalse(root.diveFixed("xyz").set(mock(123)));
  }

  @Test
  public void var_backtracking_text() {
    Path root = new Path("root");
    root.diveFixed("xyz").newText("x").diveFixed("edit").set(mock(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/joe/nope"), captured));
    Assert.assertNull(captured.get("x"));
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/joe/edit"), captured).status);
    Assert.assertEquals("joe", captured.get("x"));
  }

  @Test
  public void var_backtracking_num() {
    Path root = new Path("root");
    root.diveFixed("xyz").newNumber("x").diveFixed("edit").set(mock(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertNull(root.route(0, Path.parsePath("/xyz/42/nope"), captured));
    Assert.assertNull(captured.get("x"));
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/42/edit"), captured).status);
    Assert.assertEquals("42", captured.get("x"));
  }

  @Test
  public void suffix() {
    Path root = new Path("root");
    root.diveFixed("xyz").setSuffix("v").set(mock(123));
    TreeMap<String, String> captured = new TreeMap<>();
    Assert.assertEquals(123, root.route(0, Path.parsePath("/xyz/joe/edit"), captured).status);
    Assert.assertEquals("joe/edit", captured.get("v"));
    Assert.assertEquals(264, root.memory());
  }
}
