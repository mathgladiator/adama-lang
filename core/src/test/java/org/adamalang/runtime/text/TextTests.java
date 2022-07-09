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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class TextTests {

  @Test
  public void setFlow() {
    Text text = new Text();
    text.set("Hello World\nHow are you");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"33\":\"Hello World\",\"78\":\"How are you\"},\"order\":{\"0\":\"33\",\"1\":\"78\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()));
      Assert.assertEquals("Hello World\nHow are you", clone.get());
      Assert.assertEquals(clone.get(), new Text(clone).get());
    }
    text.set("Ok!\nHello World\nHow are you\n123");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"33\":\"Hello World\",\"78\":\"How are you\",\"13\":\"Ok!\",\"be\":\"123\"},\"order\":{\"0\":\"13\",\"1\":\"33\",\"2\":\"78\",\"3\":\"be\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()));
      Assert.assertEquals("Ok!\nHello World\nHow are you\n123", clone.get());
      Assert.assertEquals(clone.get(), new Text(clone).get());
    }
    text.set("1\n1\n1\n1\n1\n1\n\n\n\n");
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"0X\":\"\",\"31\":\"1\"},\"order\":{\"0\":\"31\",\"1\":\"31\",\"2\":\"31\",\"3\":\"31\",\"4\":\"31\",\"5\":\"31\",\"6\":\"0X\",\"7\":\"0X\",\"8\":\"0X\",\"9\":\"0X\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()));
      Assert.assertEquals("1\n1\n1\n1\n1\n1\n\n\n\n", clone.get());
      Assert.assertEquals(clone.get(), new Text(clone).get());
    }
  }

  @Test
  public void readStringAsUpgrade() {
    Text text = new Text(new JsonStreamReader("\"text\""));
    Assert.assertTrue(text.upgraded);
    Assert.assertEquals("text", text.get());
    JsonStreamWriter writer = new JsonStreamWriter();
    text.write(writer);
    Assert.assertEquals("{\"fragments\":{\"36\":\"text\"},\"order\":{\"0\":\"36\"},\"changes\":{},\"seq\":0}", writer.toString());
  }

  @Test
  public void changeLogBatch() {
    Text text = new Text();
    text.set("/* adama */");
    Assert.assertTrue(text.change(0, "[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}]"));
    Assert.assertTrue(text.change(1, "[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}]"));
    Assert.assertTrue(text.change(2, "[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}]"));
    Assert.assertTrue(text.change(3, "[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]"));
    Assert.assertFalse(text.change(10, "{}"));
    Assert.assertEquals("z/*  */x", text.get());
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"5b\":\"/* adama */\"},\"order\":{\"0\":\"5b\"},\"changes\":{\"0\":[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}],\"1\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}],\"2\":[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}],\"3\":[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]},\"seq\":0}", writer.toString());
    }
    text.commit();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"5b\":\"/* adama */\"},\"order\":{\"0\":\"5b\"},\"changes\":{\"0\":[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}],\"1\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}],\"2\":[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}],\"3\":[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]},\"seq\":0}", writer.toString());
    }
  }

  @Test
  public void skipJunk() {
    Text text = new Text(new JsonStreamReader("{\"fragments\":123,\"order\":42,\"changes\":99}"));

  }
}
