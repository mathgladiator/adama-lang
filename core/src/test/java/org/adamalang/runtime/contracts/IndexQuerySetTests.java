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
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class IndexQuerySetTests {
  @Test
  public void coverage() {
    ArrayList<String> log = new ArrayList<>();
    IndexQuerySet iqs = new IndexQuerySet() {
      @Override
      public void intersect(int column, int value, LookupMode mode) {
        log.add("c:" + column + "==" + value + ";" + mode);
      }

      @Override
      public void primary(int value) {
        log.add("p:" + value);
      }

      @Override
      public void push() {
        log.add("PUSH");
      }

      @Override
      public void finish() {
        log.add("FINISH");
      }
    };
    iqs.intersect(5, new NtMaybe<>(5), IndexQuerySet.LookupMode.Equals);
    iqs.intersect(5, new NtMaybe<>(), IndexQuerySet.LookupMode.Equals);
    iqs.intersect(5, new NtMaybe<>(7), IndexQuerySet.LookupMode.Equals);
    iqs.primary(100);
    iqs.push();
    iqs.finish();
    Assert.assertEquals("c:5==5;Equals", log.get(0));
    Assert.assertEquals("c:5==0;Equals", log.get(1));
    Assert.assertEquals("c:5==7;Equals", log.get(2));
    Assert.assertEquals("p:100", log.get(3));
    Assert.assertEquals("PUSH", log.get(4));
    Assert.assertEquals("FINISH", log.get(5));
  }
}
