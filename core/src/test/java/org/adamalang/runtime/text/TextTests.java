/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.text;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class TextTests {

  @Test
  public void setFlow() {
    Text text = new Text(0);
    text.set("Hello World\nHow are you", 0);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"e\":\"Hello World\",\"x\":\"How are you\"},\"order\":{\"0\":\"e\",\"1\":\"x\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("Hello World\nHow are you", clone.get().value);
      Assert.assertEquals(clone.get().value, new Text(clone).get().value);
    }
    text.set("Ok!\nHello World\nHow are you\n123", 0);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"11\":\"123\",\"1\":\"Ok!\",\"e\":\"Hello World\",\"x\":\"How are you\"},\"order\":{\"0\":\"1\",\"1\":\"e\",\"2\":\"x\",\"3\":\"11\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("Ok!\nHello World\nHow are you\n123", clone.get().value);
      Assert.assertEquals(clone.get().value, new Text(clone).get().value);
    }
    text.set("1\n1\n1\n1\n1\n1\n\n\n\n", 0);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"0\":\"\",\"1\":\"1\"},\"order\":{\"0\":\"1\",\"1\":\"1\",\"2\":\"1\",\"3\":\"1\",\"4\":\"1\",\"5\":\"1\",\"6\":\"0\",\"7\":\"0\",\"8\":\"0\",\"9\":\"0\"},\"changes\":{},\"seq\":0}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("1\n1\n1\n1\n1\n1\n\n\n\n", clone.get().value);
      Assert.assertEquals(clone.get().value, new Text(clone).get().value);
    }
  }

  @Test
  public void readStringAsUpgrade() {
    Text text = new Text(new JsonStreamReader("\"text\""), 0);
    Assert.assertTrue(text.upgraded);
    Assert.assertEquals("text", text.get().value);
    JsonStreamWriter writer = new JsonStreamWriter();
    text.write(writer);
    Assert.assertEquals("{\"fragments\":{\"2\":\"text\"},\"order\":{\"0\":\"2\"},\"changes\":{},\"seq\":0}", writer.toString());
  }

  @Test
  public void changeLogBatch() {
    Text text = new Text(0);
    text.set("/* adama */", 0);
    Assert.assertTrue(text.append(0, "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}"));
    Assert.assertTrue(text.append(1, "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}"));
    Assert.assertTrue(text.append(2, "{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}"));
    Assert.assertTrue(text.append(3, "{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}"));
    Assert.assertFalse(text.append(10, "{}"));
    Assert.assertEquals("z/*  */x", text.get().value);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"p\":\"/* adama */\"},\"order\":{\"0\":\"p\"},\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":0}", writer.toString());
    }
    text.commit();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"p\":\"/* adama */\"},\"order\":{\"0\":\"p\"},\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":0}", writer.toString());
    }
  }

  @Test
  public void compact() {
    Text text = new Text(0);
    text.set("/* adama */", 0);
    Assert.assertTrue(text.append(0, "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}"));
    Assert.assertTrue(text.append(1, "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}"));
    Assert.assertTrue(text.append(2, "{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}"));
    Assert.assertTrue(text.append(3, "{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}"));
    text.compact(0.8);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"2\":\"z/* adama adama */x\"},\"order\":{\"0\":\"2\"},\"changes\":{\"3\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":3}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("z/*  */x", clone.get().value);
    }
  }

  @Test
  public void compactMassive() {
    Text text = new Text(0);
    text.set("/* adama */", 0);
    Assert.assertTrue(text.append(0, "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}"));
    Assert.assertTrue(text.append(1, "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}"));
    Assert.assertTrue(text.append(2, "{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}"));
    Assert.assertTrue(text.append(3, "{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}"));
    text.compact(0.4);
    text.compact(1.8);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"z\":\"z/*  */x\"},\"order\":{\"0\":\"z\"},\"changes\":{},\"seq\":4}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("z/*  */x", clone.get().value);
    }
  }

  @Test
  public void multiLineCompact() {
    Text text = new Text(0);
    text.set("/* adama */", 0);
    Assert.assertTrue(text.append(0, "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}"));
    Assert.assertTrue(text.append(1, "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\",\"x\",\"whoop\"],12]}"));
    text.compact(1.8);
    text.compact(1.8);
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      text.write(writer);
      Assert.assertEquals("{\"fragments\":{\"3c\":\"x\",\"3\":\"z\",\"l\":\"whoop/* adama */x\"},\"order\":{\"0\":\"3\",\"1\":\"3c\",\"2\":\"l\"},\"changes\":{},\"seq\":2}", writer.toString());
      Text clone = new Text(new JsonStreamReader(writer.toString()), 0);
      Assert.assertEquals("z\nx\nwhoop/* adama */x", clone.get().value);
    }
  }

  @Test
  public void skipJunk() {
    new Text(new JsonStreamReader("{\"fragments\":123,\"order\":42,\"changes\":99}"), 0);
  }
}
