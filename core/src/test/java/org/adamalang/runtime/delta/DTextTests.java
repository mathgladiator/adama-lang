/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.text.RxText;
import org.junit.Assert;
import org.junit.Test;

public class DTextTests {
  @Test
  public void flow() {
    RxText text = new RxText(null, new RxInt32(null, 0));
    DText dt = new DText();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null, TestKey.ENCODER);
    dt.show(text, writer);
    text.set("xyz");
    dt.show(text, writer);
    text.set("/* adama */");
    dt.show(text, writer);
    text.append(0, new NtDynamic("{\"clientID\":\"dzg02a\",\"changes\":[11,[0,\"x\"]]}"));
    dt.show(text, writer);
    dt.hide(writer);
    dt.show(text, writer);
    Assert.assertEquals("{\"$g\":0,\"$i\":\"\",\"$s\":0}{\"$g\":1,\"$i\":\"xyz\",\"$s\":0}{\"$g\":2,\"$i\":\"/* adama */\",\"$s\":0}{\"0\":\"{\\\"clientID\\\":\\\"dzg02a\\\",\\\"changes\\\":[11,[0,\\\"x\\\"]]}\",\"$s\":1}null{\"$g\":2,\"$i\":\"/* adama */x\",\"$s\":1}", stream.toString());
    dt.clear();
  }
}
