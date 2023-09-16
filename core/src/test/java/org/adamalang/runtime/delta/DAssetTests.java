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
    da.clear();
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
    Assert.assertEquals("nullnull", stream.toString());
    Assert.assertEquals(32, da.__memory());
  }
}
