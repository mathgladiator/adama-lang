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
import org.adamalang.runtime.natives.NtDynamic;
import org.junit.Assert;
import org.junit.Test;

public class RxTextTests {
  private static String PATCH_0 = "[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]}]";
  private static String PATCH_1 = "[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}]";
  private static String PATCH_2 = "[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}]";
  private static String PATCH_3 = "[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}]";
  private static String PATCH_4 = "[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]";

  @Test
  public void memory() {
    RxText text = new RxText(null);
    text.__memory();
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null);
    text.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null);
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"fragments\":{},\"order\":{},\"changes\":{\"0\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[[0,\\\"/* adama */\\\"]]}]\",\"1\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[11,[0,\\\"x\\\"]]}]\",\"2\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[[0,\\\"z\\\"],12]}]\",\"3\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[9,[0,\\\" adama\\\"],4]}]\",\"4\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[4,[11],4]}]\"}}", redo.toString());
    Assert.assertEquals("\"x\":{\"fragments\":{},\"order\":{},\"changes\":{\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null}}", undo.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void commit_change_compact() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null);
    text.append(0, new NtDynamic(PATCH_0));
    text.append(1, new NtDynamic(PATCH_1));
    text.append(2, new NtDynamic(PATCH_2));
    text.append(3, new NtDynamic(PATCH_3));
    text.append(4, new NtDynamic(PATCH_4));
    text.current().compact(0.7);
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"fragments\":{\"68\":\"z/* adama */x\"},\"order\":{\"0\":\"68\"},\"changes\":{\"4\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[4,[11],4]}]\",\"3\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[9,[0,\\\" adama\\\"],4]}]\"},\"seq\":3}", redo.toString());
    Assert.assertEquals("\"x\":{\"fragments\":{\"68\":null},\"order\":{\"0\":null},\"changes\":{\"4\":null,\"3\":null},\"seq\":0}", undo.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxText text = new RxText(null);
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
    RxText text = new RxText(null);
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
    RxText text = new RxText(null);
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{},\"order\":{},\"changes\":{\"0\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]}],\"1\":[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}],\"2\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}],\"3\":[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}],\"4\":[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]},\"seq\":0}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void dump_compact() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null);
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    text.current().compact(0.7);
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{\"68\":\"z/* adama */x\"},\"order\":{\"0\":\"68\"},\"changes\":{\"3\":[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}],\"4\":[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]},\"seq\":3}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void dump_post_commit() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxText text = new RxText(null);
    Assert.assertTrue(text.append(0, new NtDynamic(PATCH_0)));
    Assert.assertTrue(text.append(1, new NtDynamic(PATCH_1)));
    Assert.assertTrue(text.append(2, new NtDynamic(PATCH_2)));
    Assert.assertTrue(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertFalse(text.append(3, new NtDynamic(PATCH_3)));
    Assert.assertTrue(text.append(4, new NtDynamic(PATCH_4)));
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    text.__commit("x", redo, undo);
    Assert.assertEquals("\"x\":{\"fragments\":{},\"order\":{},\"changes\":{\"0\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[[0,\\\"/* adama */\\\"]]}]\",\"1\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[11,[0,\\\"x\\\"]]}]\",\"2\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[[0,\\\"z\\\"],12]}]\",\"3\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[9,[0,\\\" adama\\\"],4]}]\",\"4\":\"[{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[4,[11],4]}]\"}}", redo.toString());
    Assert.assertEquals("\"x\":{\"fragments\":{},\"order\":{},\"changes\":{\"0\":null,\"1\":null,\"2\":null,\"3\":null,\"4\":null}}", undo.toString());
    text.__dump(c);
    Assert.assertEquals("{\"fragments\":{},\"order\":{},\"changes\":{\"0\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"/* adama */\"]]}],\"1\":[{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}],\"2\":[{\"clientID\":\"dzg02a\",\"changes\":[[0,\"z\"],12]}],\"3\":[{\"clientID\":\"dzg02a\",\"changes\":[9,[0,\" adama\"],4]}],\"4\":[{\"clientID\":\"dzg02a\",\"changes\":[4,[11],4]}]},\"seq\":0}", c.toString());
    Assert.assertEquals("z/*  */x", text.get());
  }

  @Test
  public void insert() {
    JsonStreamWriter c = new JsonStreamWriter();

  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
  }
}
