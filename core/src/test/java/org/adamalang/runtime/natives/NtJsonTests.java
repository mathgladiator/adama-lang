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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class NtJsonTests {
  @Test
  public void flow_int0() {
    NtJson x = new NtJson(null);
    Assert.assertFalse(x.to_i().has());
    Assert.assertEquals("null", x.to_dynamic().json);
  }
  @Test
  public void flow_int1() {
    NtJson x = new NtJson(12);
    Assert.assertTrue(x.to_i().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("12", x.to_dynamic().json);
  }
  @Test
  public void flow_int2() {
    NtJson x = new NtJson(12L);
    Assert.assertTrue(x.to_i().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("\"12\"", x.to_dynamic().json);
  }
  @Test
  public void flow_int3() {
    NtJson x = new NtJson(12.5);
    Assert.assertTrue(x.to_i().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("12.5", x.to_dynamic().json);
  }
  @Test
  public void flow_long0() {
    NtJson x = new NtJson(null);
    Assert.assertFalse(x.to_l().has());
    Assert.assertEquals("null", x.to_dynamic().json);
  }
  @Test
  public void flow_long1() {
    NtJson x = new NtJson(12);
    Assert.assertTrue(x.to_l().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("12", x.to_dynamic().json);
  }
  @Test
  public void flow_long2() {
    NtJson x = new NtJson(12L);
    Assert.assertTrue(x.to_l().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("\"12\"", x.to_dynamic().json);
  }
  @Test
  public void flow_long3() {
    NtJson x = new NtJson(12.5);
    Assert.assertTrue(x.to_l().has());
    Assert.assertEquals(12, (int) x.to_i().get());
    Assert.assertEquals("12.5", x.to_dynamic().json);
  }
  @Test
  public void flow_bool0() {
    NtJson x = new NtJson();
    Assert.assertFalse(x.to_b().has());
    Assert.assertEquals("null", x.to_dynamic().json);
  }
  @Test
  public void flow_bool1() {
    NtJson x = new NtJson(12);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(true, x.to_b().get());
    Assert.assertEquals("12", x.to_dynamic().json);
  }
  @Test
  public void flow_bool2() {
    NtJson x = new NtJson(0);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(false, x.to_b().get());
    Assert.assertEquals("0", x.to_dynamic().json);
  }
  @Test
  public void flow_bool3() {
    NtJson x = new NtJson(12L);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(true, x.to_b().get());
    Assert.assertEquals("\"12\"", x.to_dynamic().json);
  }
  @Test
  public void flow_bool4() {
    NtJson x = new NtJson(0L);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(false, x.to_b().get());
  }
  @Test
  public void flow_bool5() {
    NtJson x = new NtJson(true);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(true, x.to_b().get());
  }
  @Test
  public void flow_bool6() {
    NtJson x = new NtJson(false);
    Assert.assertTrue(x.to_b().has());
    Assert.assertEquals(false, x.to_b().get());
  }

  @Test
  public void flow_dbl0() {
    NtJson x = new NtJson(null);
    Assert.assertFalse(x.to_d().has());
  }
  @Test
  public void flow_dbl1() {
    NtJson x = new NtJson(12);
    Assert.assertTrue(x.to_d().has());
    Assert.assertEquals(12, x.to_d().get(), 0.0001);
  }
  @Test
  public void flow_dbl2() {
    NtJson x = new NtJson(12L);
    Assert.assertTrue(x.to_d().has());
    Assert.assertEquals(12, x.to_d().get(), 0.0001);
  }
  @Test
  public void flow_dbl3() {
    NtJson x = new NtJson(12.5);
    Assert.assertTrue(x.to_d().has());
    Assert.assertEquals(12.5, x.to_d().get(), 0.0001);
  }

  @Test
  public void flow_str0() {
    NtJson x = new NtJson(null);
    Assert.assertFalse(x.to_s().has());
  }
  @Test
  public void flow_str1() {
    NtJson x = new NtJson("hello world");
    Assert.assertTrue(x.to_s().has());
    Assert.assertEquals("hello world", x.to_s().get());
    Assert.assertEquals("\"hello world\"", x.to_dynamic().json);
  }
  @Test
  public void flow_str2() {
    NtJson x = new NtJson(123);
    Assert.assertTrue(x.to_s().has());
    Assert.assertEquals("123", x.to_s().get());
  }
  @Test
  public void flow_str3() {
    NtJson x = new NtJson(new TreeMap<>());
    Assert.assertFalse(x.to_s().has());
  }

  @Test
  public void deref1() {
    HashMap<String, String> m = new HashMap<>();
    m.put("x", "xyz");
    NtJson x = new NtJson(m);
    Assert.assertEquals("xyz", x.deref("x").to_s().get());
  }

  @Test
  public void deref2() {
    HashMap<String, String> m = new HashMap<>();
    m.put("1", "xyz");
    NtJson x = new NtJson(m);
    Assert.assertEquals("xyz", x.deref(1).to_s().get());
  }

  @Test
  public void deref3() {
    ArrayList<String> l = new ArrayList<>();
    l.add("a");
    l.add("b");
    l.add("c");
    NtJson x = new NtJson(l);
    Assert.assertEquals("b", x.deref("1").to_s().get());
  }

  @Test
  public void deref4() {
    ArrayList<String> l = new ArrayList<>();
    l.add("a");
    l.add("b");
    l.add("c");
    NtJson x = new NtJson(l);
    Assert.assertEquals("b", x.deref(1).to_s().get());
  }
  @Test
  public void deref5() {
    NtJson x = new NtJson(new HashMap<>());
    Assert.assertFalse(x.deref("x").to_s().has());
  }
  @Test
  public void deref6() {
    NtJson x = new NtJson(new HashMap<>());
    Assert.assertFalse(x.deref(1).to_s().has());
  }
  @Test
  public void deref7() {
    NtJson x = new NtJson(new ArrayList<>());
    Assert.assertFalse(x.deref("1").to_s().has());
  }
  @Test
  public void deref8() {
    NtJson x = new NtJson(new ArrayList<>());
    Assert.assertFalse(x.deref(1).to_s().has());
  }
  @Test
  public void deref9() {
    NtJson x = new NtJson(new ArrayList<>());
    Assert.assertFalse(x.deref("x").to_s().has());
  }
}
