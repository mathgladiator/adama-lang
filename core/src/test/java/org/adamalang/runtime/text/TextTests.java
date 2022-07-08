/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.text;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class TextTests {

  @Test
  public void set_1() {
    Text text = new Text();
    text.set("Hello World\nHow are you");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"33\":\"Hello World\",\"78\":\"How are you\"},\"order\":{\"0\":\"33\",\"1\":\"78\"},\"changes\":{},\"seq\":0}", writer.toString());
    }
    text.set("Ok!\nHello World\nHow are you\n123");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"33\":\"Hello World\",\"78\":\"How are you\",\"13\":\"Ok!\",\"be\":\"123\"},\"order\":{\"0\":\"13\",\"1\":\"33\",\"2\":\"78\",\"3\":\"be\"},\"changes\":{},\"seq\":0}", writer.toString());
    }
    text.set("1\n1\n1\n1\n1\n1\n\n\n\n");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"0X\":\"\",\"31\":\"1\"},\"order\":{\"0\":\"31\",\"1\":\"31\",\"2\":\"31\",\"3\":\"31\",\"4\":\"31\",\"5\":\"31\",\"6\":\"0X\",\"7\":\"0X\",\"8\":\"0X\",\"9\":\"0X\"},\"changes\":{},\"seq\":0}", writer.toString());
    }
  }
}
