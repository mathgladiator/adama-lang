/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.mocks.SimpleMockCallback;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ApplyMessageCallbackTests {
  @Test
  public void proxy_error() throws Exception {
    SimpleMockCallback callback = new SimpleMockCallback();
    ApplyMessageCallback<Void> wrapped = new ApplyMessageCallback<>(null, null, callback);
    wrapped.failure(new ErrorCodeException(44));
    callback.assertFailure(44);
  }

  @Test
  public void success_applies() throws IOException {
    DocumentMemoryLog log = new DocumentMemoryLog(File.createTempFile("prefix", "suffix").getParentFile(), "temp");
    SimpleMockCallback callback = new SimpleMockCallback();
    WriteAheadMessage.Initialize initialize = new WriteAheadMessage.Initialize();
    initialize.space = "space";
    initialize.key = "key";
    initialize.initialize = new WriteAheadMessage.Change();
    initialize.initialize.redo = "YAY";
    initialize.initialize.seq_end = 40;
    ApplyMessageCallback<Void> wrapped = new ApplyMessageCallback<>(log, initialize, callback);
    wrapped.success(null);
    Assert.assertEquals("YAY", log.get_Load().patch);
  }

}
