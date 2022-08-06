package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.junit.Assert;
import org.junit.Test;

public class DResultTests {
  @Test
  public void flow() {
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    DResult<DComplex> result = new DResult<>();
    DComplex cmp = result.get(() -> new DComplex());
    result.show(new NtResult<>(new NtComplex(1.0, 2.0), false, 100, "message"), writer).end();
    result.hide(writer);
    result.show(new NtResult<>(null, true, 500, "error message"), writer).end();
    Assert.assertEquals("{\"failed\":false,\"message\":\"OK\",\"code\":100,null,{\"failed\":true,\"message\":\"error message\",\"code\":500", stream.toString());
    Assert.assertEquals(40, result.__memory());
    result.clear();
  }
}
