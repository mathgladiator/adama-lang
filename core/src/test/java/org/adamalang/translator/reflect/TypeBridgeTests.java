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
package org.adamalang.translator.reflect;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class TypeBridgeTests {
  @Test
  public void basics() {
    Assert.assertEquals("int", TypeBridge.getAdamaType(Integer.class, null).getAdamaType());
    Assert.assertEquals("int", TypeBridge.getAdamaType(int.class, null).getAdamaType());
    Assert.assertEquals("bool", TypeBridge.getAdamaType(Boolean.class, null).getAdamaType());
    Assert.assertEquals("bool", TypeBridge.getAdamaType(boolean.class, null).getAdamaType());
    Assert.assertEquals("double", TypeBridge.getAdamaType(Double.class, null).getAdamaType());
    Assert.assertEquals("double", TypeBridge.getAdamaType(double.class, null).getAdamaType());
    Assert.assertEquals("string", TypeBridge.getAdamaType(String.class, null).getAdamaType());
    Assert.assertEquals("long", TypeBridge.getAdamaType(Long.class, null).getAdamaType());
    Assert.assertEquals("long", TypeBridge.getAdamaType(long.class, null).getAdamaType());
    Assert.assertEquals("principal", TypeBridge.getAdamaType(NtPrincipal.class, null).getAdamaType());
  }

  @Test
  public void ntlistNoAnnotation() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(NtList.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }

  @Test
  public void ntMaybeNoAnnotation() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(NtMaybe.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }

  @Test
  public void sanityTestVoid() {
    Assert.assertEquals(null, TypeBridge.getAdamaType(Void.class, null));
    Assert.assertEquals(null, TypeBridge.getAdamaType(void.class, null));
  }

  @Test
  public void unknownType() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(TypeBridgeTests.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }
}
