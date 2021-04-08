package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DAssetTests {
  @Test
  public void flow() {
    DAsset da = new DAsset();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    da.show(NtAsset.NOTHING, writer);
    da.hide(writer);
    da.show(new NtAsset(12, "name", "type", 42, "md5", "sha"), writer);
    da.show(new NtAsset(32, "name", "type", 42, "md5", "sha"), writer);
    da.hide(writer);
    Assert.assertEquals("{\"id\":\"0\",\"size\":\"0\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\",null,{\"id\":\"12\",\"size\":\"42\",\"type\":\"type\",\"md5\":\"md5\",\"sha384\":\"sha\",{\"id\":\"32\",\"size\":\"42\",\"type\":\"type\",\"md5\":\"md5\",\"sha384\":\"sha\",null", stream.toString());
  }
}
