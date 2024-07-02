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
package org.adamalang.runtime.sys.web.rxhtml;

import org.adamalang.common.Json;
import org.adamalang.rxhtml.RxHtmlBundle;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class RxHtmlResultTests {
  @Test
  public void testing() {
    ArrayList<String> patterns = new ArrayList<>();
    patterns.add("/hi/there");
    patterns.add("/hi/$name/ok");
    RxHtmlResult result = new RxHtmlResult(new RxHtmlBundle("js", "css", new Shell(ShellConfig.start().end()), patterns, new HashMap<>(), new ArrayList<>(), Json.newJsonObject()));
    Assert.assertFalse(result.test("/"));
    Assert.assertFalse(result.test("///"));
    Assert.assertFalse(result.test("/////"));
    Assert.assertFalse(result.test("/hi"));
    Assert.assertFalse(result.test("/hi/"));
    Assert.assertFalse(result.test("/hi/nope"));
    Assert.assertFalse(result.test("/hi/nope/"));
    Assert.assertTrue(result.test("/hi/there"));
    Assert.assertFalse(result.test("/hi/xyz/nope"));
    Assert.assertTrue(result.test("/hi/xyz/ok"));
    Assert.assertTrue(result.test("/hi/xyz/ok//"));
    result.toString();
  }
}
