/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibAdamaTests {
  @Test
  public void flow_problems() {
    Assert.assertEquals("null", LibAdama.reflect("{").json);
    Assert.assertEquals("[{\"error\":true}]", LibAdama.validate("{").json);
    Assert.assertEquals("null", LibAdama.reflect("int x = \"x\"").json);
    Assert.assertEquals("[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":3,\"byte\":3}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'r<int>' is unable to store type 'string'. (TypeCheckReferences)\"}]", LibAdama.validate("int x = \"x\";").json);
  }
}
