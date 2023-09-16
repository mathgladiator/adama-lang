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
package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_WriteDeltaTests {

  private Object of(String x) {
    return new JsonStreamReader(x).readJavaTree();
  }

  private String delta(Object from, Object to) {
    JsonStreamWriter writer = new JsonStreamWriter();
    JsonAlgebra.writeObjectFieldDelta(from, to, writer);
    return writer.toString();
  }

  @Test
  public void change1() {
    Object from = of("null");
    Object to = of("{}");
    Assert.assertEquals("{}", delta(from, to));
  }

  @Test
  public void change2() {
    Object from = of("{\"x\":true}");
    Object to = of("{}");
    Assert.assertEquals("{\"x\":null}", delta(from, to));
  }

  @Test
  public void change3() {
    Object from = of("{}");
    Object to = of("{\"x\":true}");
    Assert.assertEquals("{\"x\":true}", delta(from, to));
  }

  @Test
  public void change4() {
    Object from = of("{\"x\":false}");
    Object to = of("{\"x\":{\"x\":true}}");
    Assert.assertEquals("{\"x\":{\"x\":true}}", delta(from, to));
  }

  @Test
  public void change5() {
    Object from = of("{\"x\":{\"x\":false}}");
    Object to = of("{\"x\":{\"x\":true}}");
    Assert.assertEquals("{\"x\":{\"x\":true}}", delta(from, to));
  }

}
