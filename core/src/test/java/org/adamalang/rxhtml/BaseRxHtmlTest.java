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

import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public abstract class BaseRxHtmlTest {
  @Test
  public void stable() throws Exception {
    StringBuilder issuesLive = new StringBuilder();
    Feedback feedback = (element, warning) -> issuesLive.append("WARNING:").append(warning).append("\n");
    String live = RxHtmlTool.convertStringToTemplateForest(source(), ShellConfig.start().withFeedback(feedback).withUseLocalAdamaJavascript(dev()).end()).toString().replaceAll("/[0-9]*/devlibadama\\.js", Matcher.quoteReplacement("/DEV.js"));
    Assert.assertEquals(gold(), live.trim());
    Assert.assertEquals(issues().trim(), issuesLive.toString().trim());
  }

  /** test any developer mode settings */
  public abstract boolean dev();

  /** the source code of the template */
  public abstract String source();

  /** the output the test is supposed to generate */
  public abstract String gold();

  /** The issues the test is supposed to have */
  public abstract String issues();

  @Test
  public void codegen() throws Exception {
    String adamaCode = RxHtmlToAdama.codegen(source());
    Parser parser = new Parser(new TokenEngine("test", adamaCode.codePoints().iterator()), Scope.makeRootDocument());
    parser.document();
  }
}
