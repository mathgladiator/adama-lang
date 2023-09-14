/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibAdamaTests {
  @Test
  public void flow_problems() {
    Assert.assertEquals("null", LibAdama.reflect("{").json);
    Assert.assertEquals("[{\"error\":true}]", LibAdama.validate("{").json);
    Assert.assertEquals("[]", LibAdama.validate("").json);
    Assert.assertEquals("null", LibAdama.reflect("int x = \"x\"").json);
    Assert.assertEquals("[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":3,\"byte\":3}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'r<int>' is unable to store type 'string'.\",\"file\":\"<direct code>\"}]", LibAdama.validate("int x = \"x\";").json);
    Assert.assertEquals("{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}", LibAdama.reflect("").json);
  }
}
