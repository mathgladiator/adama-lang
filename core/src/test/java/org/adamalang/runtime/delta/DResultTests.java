/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
