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
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DAssetTests {
  @Test
  public void flow() {
    DAsset da = new DAsset();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    da.show(NtAsset.NOTHING, writer);
    da.hide(writer);
    da.show(new NtAsset("12", "name", "type", 42, "md5", "sha"), writer);
    da.show(new NtAsset("32", "name", "type", 42, "md5", "sha"), writer);

    da.hide(writer);
    String toTest = stream.toString();
    Assert.assertTrue(toTest.contains("\",\"size\":\"0\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\"}null{\"id\":\""));
    Assert.assertTrue(toTest.contains("\",\"size\":\"42\",\"type\":\"type\",\"md5\":\"md5\",\"sha384\":\"sha\"}{\"id\":\""));
    Assert.assertTrue(toTest.contains("\",\"size\":\"42\",\"type\":\"type\",\"md5\":\"md5\",\"sha384\":\"sha\"}null"));
    Assert.assertEquals(32, da.__memory());
  }

  @Test
  public void flowNoKey() {
    DAsset da = new DAsset();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, null);
    da.show(NtAsset.NOTHING, writer);
    da.hide(writer);
    da.show(new NtAsset("12", "name", "type", 42, "md5", "sha"), writer);
    da.show(new NtAsset("32", "name", "type", 42, "md5", "sha"), writer);

    da.hide(writer);
    Assert.assertEquals(
        "nullnull",
        stream.toString());
    Assert.assertEquals(32, da.__memory());
  }
}
