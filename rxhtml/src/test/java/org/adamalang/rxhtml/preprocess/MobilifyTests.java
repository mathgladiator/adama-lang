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
package org.adamalang.rxhtml.preprocess;

import org.adamalang.rxhtml.ProductionMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class MobilifyTests {
  @Test
  public void flow_web_ow() {
    Document document = Jsoup.parse("<html><x attr=value mobile:attr=value></html>");
    Mobilify.go(document, ProductionMode.Web);
    Assert.assertEquals("<x attr=\"value\"></x>", document.getElementsByTag("x").get(0).outerHtml());
  }

  @Test
  public void flow_mobile_ow() {
    Document document = Jsoup.parse("<html><x attr=value mobile:attr=\"value2\"></html>");
    Mobilify.go(document, ProductionMode.MobileApp);
    Assert.assertEquals("<x attr=\"value2\"></x>", document.getElementsByTag("x").get(0).outerHtml());
  }

  @Test
  public void flow_web_inj() {
    Document document = Jsoup.parse("<html><x mobile:attr=value></html>");
    Mobilify.go(document, ProductionMode.Web);
    Assert.assertEquals("<x></x>", document.getElementsByTag("x").get(0).outerHtml());
  }

  @Test
  public void flow_mobile_inj() {
    Document document = Jsoup.parse("<html><x mobile:attr=\"value2\"></html>");
    Mobilify.go(document, ProductionMode.MobileApp);
    Assert.assertEquals("<x attr=\"value2\"></x>", document.getElementsByTag("x").get(0).outerHtml());
  }

  @Test
  public void flow_web_no_arg() {
    Document document = Jsoup.parse("<html><x attr mobile:attr></html>");
    Mobilify.go(document, ProductionMode.Web);
    Assert.assertEquals("<x attr></x>", document.getElementsByTag("x").get(0).outerHtml());
  }

  @Test
  public void flow_mobile_no_arg() {
    Document document = Jsoup.parse("<html><x mobile:attr></html>");
    Mobilify.go(document, ProductionMode.MobileApp);
    Assert.assertEquals("<x attr></x>", document.getElementsByTag("x").get(0).outerHtml());
  }
}
