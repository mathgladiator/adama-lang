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
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class IdCodecTests {
  @Test
  public void battery() {
    Random rng = new Random();
    long v = 0;
    for (int k = 0; k < 1000; k++) {
      String enc = IdCodec.encode(v);
      long v2 = IdCodec.decode(enc);
      Assert.assertEquals(v, v2);
      v += rng.nextInt(1000);
      if (k % 25 == 0) {
        v *= 3;
      }
      v = Math.abs(v);
    }
  }
}
