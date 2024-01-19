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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class JsonSumTests {
  @Test
  public void top_level() {
    Assert.assertEquals("{\"x\":3}", JsonSum.sum(Json.parseJsonObject("{\"x\":1}"), Json.parseJsonObject("{\"x\":2}")).toString());
  }
  @Test
  public void recurse() {
    Assert.assertEquals("{\"z\":{\"x\":3}}", JsonSum.sum(Json.parseJsonObject("{\"z\":{\"x\":1}}"), Json.parseJsonObject("{\"z\":{\"x\":2}}")).toString());
  }
  @Test
  public void top_level_al() {
    ArrayList<ObjectNode> sum = new ArrayList<>();
    sum.add(Json.parseJsonObject("{\"x\":1}"));
    sum.add(Json.parseJsonObject("{\"x\":2}"));
    Assert.assertEquals("{\"x\":3}", JsonSum.sum(sum).toString());
  }
  @Test
  public void recurse_al() {
    ArrayList<ObjectNode> sum = new ArrayList<>();
    sum.add(Json.parseJsonObject("{\"z\":{\"x\":1}}"));
    sum.add(Json.parseJsonObject("{\"z\":{\"x\":2}}"));
    Assert.assertEquals("{\"z\":{\"x\":3}}", JsonSum.sum(sum).toString());
  }
}
