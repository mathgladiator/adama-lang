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
package org.adamalang.common.html;

import org.junit.Assert;
import org.junit.Test;

public class TokenizerTests {
  @Test
  public void just_text() {
    Tokenizer t = Tokenizer.of("Hello");
    Assert.assertTrue(t.hasNext());
    Token t0 = t.next();
    Assert.assertEquals("Hello", t0.text);
    Assert.assertEquals("0;0;0;5", t0.coords());
    Assert.assertEquals(Type.Text, t0.type);
    Assert.assertFalse(t.hasNext());
  }

  @Test
  public void just_element_open() {
    Tokenizer t = Tokenizer.of("<Hello>");
    Assert.assertTrue(t.hasNext());
    Token t0 = t.next();
    Assert.assertEquals("<Hello>", t0.text);
    Assert.assertEquals("0;0;0;7", t0.coords());
    Assert.assertEquals(Type.ElementOpen, t0.type);
    Assert.assertFalse(t.hasNext());
  }

  @Test
  public void just_element_close() {
    Tokenizer t = Tokenizer.of("</Hello>");
    Assert.assertTrue(t.hasNext());
    Token t0 = t.next();
    Assert.assertEquals("</Hello>", t0.text);
    Assert.assertEquals("0;0;0;8", t0.coords());
    Assert.assertEquals(Type.ElementClose, t0.type);
    Assert.assertFalse(t.hasNext());
  }

  @Test
  public void text_element_text_close() {
    Tokenizer t = Tokenizer.of("Hello <b>Mr</b>");
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("Hello ", t0.text);
      Assert.assertEquals("0;0;0;6", t0.coords());
      Assert.assertEquals(Type.Text, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("<b>", t0.text);
      Assert.assertEquals("0;6;0;9", t0.coords());
      Assert.assertEquals(Type.ElementOpen, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("Mr", t0.text);
      Assert.assertEquals("0;9;0;11", t0.coords());
      Assert.assertEquals(Type.Text, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("</b>", t0.text);
      Assert.assertEquals("0;11;0;15", t0.coords());
      Assert.assertEquals(Type.ElementClose, t0.type);
    }
    Assert.assertFalse(t.hasNext());
  }

  @Test
  public void multi_line() {
    Tokenizer t = Tokenizer.of("Hello\n<b>\nWorld\n</b>");
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("Hello\n", t0.text);
      Assert.assertEquals("0;0;1;0", t0.coords());
      Assert.assertEquals(Type.Text, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("<b>", t0.text);
      Assert.assertEquals("1;0;1;3", t0.coords());
      Assert.assertEquals(Type.ElementOpen, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("\nWorld\n", t0.text);
      Assert.assertEquals("1;3;3;0", t0.coords());
      Assert.assertEquals(Type.Text, t0.type);
    }
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("</b>", t0.text);
      Assert.assertEquals("3;0;3;4", t0.coords());
      Assert.assertEquals(Type.ElementClose, t0.type);
    }
    Assert.assertFalse(t.hasNext());
  }

  @Test
  public void attr() {
    Tokenizer t = Tokenizer.of("<b attr=\"x<yz\">");
    {
      Assert.assertTrue(t.hasNext());
      Token t0 = t.next();
      Assert.assertEquals("<b attr=\"x<yz\">", t0.text);
      Assert.assertEquals("0;0;0;15", t0.coords());
      Assert.assertEquals(Type.ElementOpen, t0.type);
    }
    Assert.assertFalse(t.hasNext());
  }
}
