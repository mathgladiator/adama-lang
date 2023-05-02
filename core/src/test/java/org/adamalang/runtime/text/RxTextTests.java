/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.text;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.reactives.RxInt32;
import org.junit.Assert;
import org.junit.Test;

public class RxTextTests {
  private static String PATCH_0 = "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]}";
  private static String PATCH_1 = "{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}";
  private static String PATCH_2 = "{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}";
  private static String PATCH_3 = "{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}";
  private static String PATCH_4 = "{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}";

  @Test
  public void memory() {
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.__memory();
  }

  @Test
  public void commit_set() {
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.set("Howdy");
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      text.__commit("x", redo, undo);
      Assert.assertEquals("Howdy", text.get());
      Assert.assertEquals("\"x\":{\"fragments\":{\"1\":\"Howdy\"},\"order\":{\"0\":\"1\"},\"changes\":{},\"gen\":1}", redo.toString());
      Assert.assertEquals("\"x\":{\"fragments\":{\"1\":null},\"order\":{\"0\":null},\"changes\":{},\"gen\":0}", undo.toString());
    }
    text.set("Yo");
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      text.__commit("x", redo, undo);
      Assert.assertEquals("Yo", text.get());
      Assert.assertEquals("\"x\":{\"fragments\":{\"2\":\"Yo\",\"1\":null},\"order\":{\"0\":\"2\"},\"changes\":{},\"gen\":2}", redo.toString());
      Assert.assertEquals("\"x\":{\"fragments\":{\"2\":null,\"1\":\"Howdy\"},\"order\":{\"0\":\"1\"},\"changes\":{},\"gen\":1}", undo.toString());
    }
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}}}", redo.toString());
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null}}", undo.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void commit_change_batch() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertEquals("", text.get());
    text.append(0, new NtDynamic("[" + PATCH_0 + "," + PATCH_1 + "," + PATCH_2 + "," + PATCH_3 + "," + PATCH_4 + "]"));
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}}}", redo.toString());
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null}}", undo.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void commit_change_compact() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.compact(0.7);
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"fragments\":{\"t\":\"z/* adama */x\"},\"order\":{\"0\":\"t\"},\"changes\":{\"4\":\"{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[4,[11],4]}\",\"3\":\"{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[9,[0,\\\" adama\\\"],4]}\"},\"seq\":3}", redo.toString());
    Assert.assertEquals("\"x\":{\"fragments\":{\"t\":null},\"order\":{\"0\":null},\"changes\":{\"4\":null,\"3\":null},\"seq\":0}", undo.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.__revert();
    text.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void revert_compat() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.current().compact(1.0);
    text.__revert();
    text.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void dump_pre_commit() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{},\"order\":{},\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":0}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void dump_compact() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    text.current().compact(0.7);
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{\"t\":\"z/* adama */x\"},\"order\":{\"0\":\"t\"},\"changes\":{\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":3}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void dump_post_commit() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertFalse(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}}}", redo.toString());
    Assert.assertEquals("\"x\":{\"changes\":{\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null}}", undo.toString());
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{},\"order\":{},\"changes\":{\"0\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]},\"1\":{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]},\"2\":{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]},\"3\":{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]},\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":0}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }


  @Test
  public void dump_post_compact_commit() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertFalse(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    Assert.assertEquals(511, text.__memory());
    text.compact(0.8);
    Assert.assertEquals(303, text.__memory());
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"fragments\":{\"2\":\"z/* adama adama */x\"},\"order\":{\"0\":\"2\"},\"changes\":{\"4\":\"{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[4,[11],4]}\"},\"seq\":4}", redo.toString());
    Assert.assertEquals("\"x\":{\"fragments\":{\"2\":null},\"order\":{\"0\":null},\"changes\":{\"4\":null},\"seq\":0}", undo.toString());
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{\"2\":\"z/* adama adama */x\"},\"order\":{\"0\":\"2\"},\"changes\":{\"4\":{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}},\"seq\":4}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
    Assert.assertEquals(311, text.__memory());
  }

  @Test
  public void insert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertEquals("", text.get());
    text.append(0, new NtDynamic("[" + PATCH_0 + "," + PATCH_1 + "," + PATCH_2 + "," + PATCH_3 + "," + PATCH_4 + "]"));
    text.__commit("x", redo, undo);
    JsonStreamWriter writer = new JsonStreamWriter();
    text.__dump(writer); {
      RxText clone = new RxText(null, new RxInt32(null, 0));
      clone.__insert(new JsonStreamReader(writer.toString()));
      Assert.assertEquals("z/*  */x", clone.get());
    }
  }

  @Test
  public void patchViaUndoJustChanges() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertEquals("", text.get());
    text.append(0, new NtDynamic("[" + PATCH_0 + "," + PATCH_1 + "," + PATCH_2 + "," + PATCH_3 + "," + PATCH_4 + "]"));
    text.__commit("x", redo, undo);
    Assert.assertEquals("z/*  */x", text.get());
    text.__patch(new JsonStreamReader(undo.toString().substring(4)));
    Assert.assertEquals("", text.get());
  }

  @Test
  public void patchViaUndoCompacted() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null, new RxInt32(null, 0));
    Assert.assertEquals("", text.get());
    text.append(0, new NtDynamic("[" + PATCH_0 + "," + PATCH_1 + "," + PATCH_2 + "," + PATCH_3 + "," + PATCH_4 + "]"));
    text.compact(0.7);
    text.__commit("x", redo, undo);
    Assert.assertEquals("z/*  */x", text.get());
    text.__patch(new JsonStreamReader(undo.toString().substring(4)));
    Assert.assertEquals("", text.get());
  }
}
