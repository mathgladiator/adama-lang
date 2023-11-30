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
package org.adamalang.runtime.deploy;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class CachedByteCodeTests {
  @Test
  public void empty() throws Exception {
    final byte[] p;
    {
      CachedByteCode cbc = new CachedByteCode("space", "class", "reflect", new HashMap<>());
      p = cbc.pack();
    }
    {
      CachedByteCode u = CachedByteCode.unpack(p);
      Assert.assertEquals("space", u.spaceName);
      Assert.assertEquals("class", u.className);
      Assert.assertEquals("reflect", u.reflection);
      Assert.assertEquals(0, u.classBytes.size());
    }
  }

  @Test
  public void solo() throws Exception {
    final byte[] p;
    {
      HashMap<String, byte[]> m = new HashMap<>();
      m.put("xyz", "ABC".getBytes());
      CachedByteCode cbc = new CachedByteCode("space", "class", "reflect", m);
      p = cbc.pack();
    }
    {
      CachedByteCode u = CachedByteCode.unpack(p);
      Assert.assertEquals("space", u.spaceName);
      Assert.assertEquals("class", u.className);
      Assert.assertEquals("reflect", u.reflection);
      Assert.assertEquals(1, u.classBytes.size());
      Assert.assertEquals("ABC", new String(u.classBytes.get("xyz")));
    }
  }

  @Test
  public void two() throws Exception {
    final byte[] p;
    {
      HashMap<String, byte[]> m = new HashMap<>();
      m.put("xyz", "ABC".getBytes());
      m.put("x", "DEF".getBytes());
      CachedByteCode cbc = new CachedByteCode("space", "class", "reflect", m);
      p = cbc.pack();
    }
    {
      CachedByteCode u = CachedByteCode.unpack(p);
      Assert.assertEquals("space", u.spaceName);
      Assert.assertEquals("class", u.className);
      Assert.assertEquals("reflect", u.reflection);
      Assert.assertEquals(2, u.classBytes.size());
      Assert.assertEquals("ABC", new String(u.classBytes.get("xyz")));
      Assert.assertEquals("DEF", new String(u.classBytes.get("x")));
    }
  }
}
