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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibHTMLTests {
  @Test
  public void parse1_direct() {
    NtMaybe<NtDynamic> result = LibHTML.convertHTMLtoJSON("<food x=\"abc\">Hello World<br /></food>", "food");
    Assert.assertTrue(result.has());
    Assert.assertEquals("{\"t\":\"food\",\"a\":{\"x\":\"abc\"},\"c\":[\"Hello World\",{\"t\":\"br\"}]}", result.get().json);
    Assert.assertEquals("<food x=\"abc\">Hello World<br /></food>", LibHTML.convertJSONtoHTML(result.get()));
  }

  @Test
  public void parse1_body() { // verify JSoup injection
    NtMaybe<NtDynamic> result = LibHTML.convertHTMLtoJSON("<food x=\"abc\">Hello World<br /></food>", "body");
    Assert.assertTrue(result.has());
    Assert.assertEquals("{\"t\":\"body\",\"c\":[{\"t\":\"food\",\"a\":{\"x\":\"abc\"},\"c\":[\"Hello World\",{\"t\":\"br\"}]}]}", result.get().json);
    Assert.assertEquals("<body><food x=\"abc\">Hello World<br /></food></body>", LibHTML.convertJSONtoHTML(result.get()));
  }

  @Test
  public void parse1_html() { // verify JSoup injection
    NtMaybe<NtDynamic> result = LibHTML.convertHTMLtoJSON("<food x=\"abc\">Hello World<br /></food>", "html");
    Assert.assertTrue(result.has());
    Assert.assertEquals("{\"t\":\"html\",\"c\":[{\"t\":\"head\",\"c\":[]},{\"t\":\"body\",\"c\":[{\"t\":\"food\",\"a\":{\"x\":\"abc\"},\"c\":[\"Hello World\",{\"t\":\"br\"}]}]}]}", result.get().json);
    Assert.assertEquals("<html><head></head><body><food x=\"abc\">Hello World<br /></food></body></html>", LibHTML.convertJSONtoHTML(result.get()));
  }

  @Test
  public void parse2_escape() {
    NtMaybe<NtDynamic> result = LibHTML.convertHTMLtoJSON("<food x=\"&quot;hi&quot; &apos;Mr. Bond&apos; (&lt; &gt;) &amp; Mr. Cat\"></food>", "html");
    Assert.assertTrue(result.has());
    Assert.assertEquals("{\"t\":\"html\",\"c\":[{\"t\":\"head\",\"c\":[]},{\"t\":\"body\",\"c\":[{\"t\":\"food\",\"a\":{\"x\":\"\\\"hi\\\" 'Mr. Bond' (< >) & Mr. Cat\"},\"c\":[]}]}]}", result.get().json);
    Assert.assertEquals("<html><head></head><body><food x=\"&quot;hi&quot; &apos;Mr. Bond&apos; (&lt; &gt;) &amp; Mr. Cat\"></food></body></html>", LibHTML.convertJSONtoHTML(result.get()));
  }

  @Test
  public void parseNope() {
    NtMaybe<NtDynamic> result = LibHTML.convertHTMLtoJSON("<food x=\"abc\">Hello World<br /></food>", "bar");
    Assert.assertFalse(result.has());
  }
}
