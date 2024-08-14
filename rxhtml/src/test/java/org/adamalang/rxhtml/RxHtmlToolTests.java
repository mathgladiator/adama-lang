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
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.routing.Target;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class RxHtmlToolTests {
  @Test
  public void redirect_eval() {
    Target t = RxHtmlTool.createRedirectRule(301, "https://admin.[[$host.apex]]/[[path]]");
    TreeMap<String, String> map = new TreeMap<>();
    map.put("$host.apex", "mydomain.com");
    map.put("path", "xyz/abc");
    String newLocation = t.mutation.apply(t, map).headers.get("location");
    Assert.assertEquals("https://admin.mydomain.com/xyz/abc", newLocation);
  }
}
