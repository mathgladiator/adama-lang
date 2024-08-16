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

import org.adamalang.rxhtml.template.config.ShellConfig;
import org.junit.Test;

public class SimpleTests {

  @Test
  public void emptyvalue() {
    drive("<template name=\"foo\">how<input checked /></template>");
  }

  private static void drive(String rxhtml) {
    System.err.println(RxHtmlTool.convertStringToTemplateForest(rxhtml, null, ShellConfig.start().withFeedback((e, x) -> System.err.println(x)).end()));
  }

  @Test
  public void basic() {
    drive("<template name=\"foo\">how<b class=\"foo bar\">d</b>y<img src=\"imgurl\"/></template>");
  }

  @Test
  public void single_var() {
    drive("<template name=\"foo\"><lookup name=\"x\"/></template>");
  }

  @Test
  public void repeat_var() {
    drive("<template name=\"foo\"><lookup name=\"x\"/><lookup name=\"x\"/><lookup name=\"x\"/></template>");
  }

  @Test
  public void sanityStyle() {
    drive("<forest><style>XYZ</style></forest>");
  }

  @Test
  public void preWS() {
    drive("<forest><page uri=\"/\"><pre>X\nY\nZ\n</page></forest>");
  }

  @Test
  public void adamaWS() {
    drive("<forest><page uri=\"/\"><pre adama>X\nY\nZ\n</pre></page></forest>");
  }

  @Test
  public void highlightWS() {
    drive("<forest><page uri=\"/\"><pre highlight=\"css\">X\nY\nZ\n</pre></forest>");
  }
}
