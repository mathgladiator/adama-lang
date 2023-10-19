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
package org.adamalang.rxhtml;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public abstract class BaseRxHtmlTest {
  private RxHtmlResult cachedResult = null;
  private StringBuilder issuesLive;

  public RxHtmlResult result() {
    if (cachedResult == null) {
      issuesLive = new StringBuilder();
      Feedback feedback = (element, warning) -> issuesLive.append("WARNING:").append(warning).append("\n");
      cachedResult = RxHtmlTool.convertStringToTemplateForest(source().replaceAll("\r", ""), ShellConfig.start().withVersion("GENMODE").withEnvironment("test").withFeedback(feedback).withUseLocalAdamaJavascript(dev()).end());
    }
    return cachedResult;
  }

  @Test
  public void stable_code() throws Exception {
    RxHtmlResult result = result();
    String live = result.toString().replaceAll("/[0-9]*/devlibadama\\.js", Matcher.quoteReplacement("/DEV.js"));
    Assert.assertEquals(gold().replaceAll("\r", ""), live.trim().replaceAll("\r", ""));
  }

  @Test
  public void stable_issues() {
    RxHtmlResult result = result();
    Assert.assertEquals(issues().trim(), issuesLive.toString().trim());
  }

  @Test
  public void stable_schema() {
    RxHtmlResult result = result();
    ObjectNode expected = Json.parseJsonObject(schema());
    ObjectNode computed = Json.parseJsonObject(result.viewSchema.toPrettyString());
    Assert.assertEquals(expected, computed);
  }

  /** test any developer mode settings */
  public abstract boolean dev();

  /** the source code of the template */
  public abstract String source();

  /** the output the test is supposed to generate */
  public abstract String gold();

  /** The issues the test is supposed to have */
  public abstract String issues();

  /** get the schema */
  public abstract String schema();
}
