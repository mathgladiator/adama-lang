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
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class PrivateLazyDeltaWriterTests {

  @Test
  public void bunchAdoAboutNothing() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    lazy.planObject().planField("x").planArray();
    Assert.assertEquals("", writer.toString());
  }

  @Test
  public void manifestAbove() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    lazy.planObject().planField("x").planArray().manifest();
    Assert.assertEquals("{\"x\":[", writer.toString());
  }

  @Test
  public void manifestX() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
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
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").writeNtComplex(new NtComplex(1, 2));
    obj.end();
    Assert.assertEquals("{\"x\":{\"r\":1.0,\"i\":2.0}}", writer.toString());
  }

  @Test
  public void force() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").force();
    obj.end();
    Assert.assertEquals("{\"x\":}", writer.toString());
  }

  @Test
  public void inject() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").injectJson(">INJECT<");
    obj.end();
    Assert.assertEquals("{\"x\":>INJECT<}", writer.toString());
  }

  @Test
  public void simpleObject() {
    JsonStreamWriter writer = new JsonStreamWriter();
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    PrivateLazyDeltaWriter obj = lazy.planObject();
    obj.planField("x").writeInt(123);
    obj.planField("y").writeNull();
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
    PrivateLazyDeltaWriter lazy = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, writer, null, 0);
    PrivateLazyDeltaWriter obj = lazy.planArray();
    obj.writeInt(123);
    obj.writeNull();
    obj.writeString("hi");
    obj.writeDouble(13.271);
    obj.writeBool(true);
    obj.end();
    Assert.assertEquals("[123,null,\"hi\",13.271,true]", writer.toString());
  }
}
