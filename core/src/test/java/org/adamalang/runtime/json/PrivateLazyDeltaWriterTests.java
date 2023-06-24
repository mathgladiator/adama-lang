/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class PrivateLazyDeltaWriterTests {

  @Test
  public void bunchAdoAboutNothing() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    lazy.planObject().planField("x").planArray();
    Assert.assertEquals("", writer.toString());
  }

  @Test
  public void manifestAbove() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    lazy.planObject().planField("x").planArray().manifest();
    Assert.assertEquals("{\"x\":[", writer.toString());
  }

  @Test
  public void manifestX() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    lazy.planObject().planField("x").planArray().writeFastString("x");
    Assert.assertEquals("{\"x\":[\"x\"", writer.toString());
  }

  @Test
  public void donothing() {
    PrivateLazyDeltaWriter.DO_NOTHING.run();
  }

  @Test
  public void complex() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").writeNtComplex(new NtComplex(1, 2));
    obj.end();
    Assert.assertEquals("{\"x\":{\"r\":1.0,\"i\":2.0}}", writer.toString());
  }

  @Test
  public void force() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").force();
    obj.end();
    Assert.assertEquals("{\"x\":}", writer.toString());
  }

  @Test
  public void inject() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").injectJson(">INJECT<");
    obj.end();
    Assert.assertEquals("{\"x\":>INJECT<}", writer.toString());
  }

  @Test
  public void simpleObject() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").writeInt(123);
    obj.planField("y").writeNull();
    ;
    obj.planField(42).writeString("hi");
    obj.planField(13).writeDouble(13.271);
    obj.planField("z").writeBool(true);
    obj.end();
    Assert.assertEquals(
        "{\"x\":123,\"y\":null,\"42\":\"hi\",\"13\":13.271,\"z\":true}", writer.toString());
  }

  @Test
  public void simpleArray() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, TestKey.ENCODER);
    PrivateLazyDeltaWriter obj = lazy.planArray();
    obj.writeInt(123);
    obj.writeNull();
    ;
    obj.writeString("hi");
    obj.writeDouble(13.271);
    obj.writeBool(true);
    obj.end();
    Assert.assertEquals("[123,null,\"hi\",13.271,true]", writer.toString());
  }
}
