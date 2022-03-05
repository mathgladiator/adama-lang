/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.mocks.LatchedVoidCallback;
import org.junit.Test;

public class CallbackByteStreamWriterTests {

  @Test
  public void proxy() {
    LatchedVoidCallback callback = new LatchedVoidCallback();
    CallbackByteStreamWriter<Void> test = new CallbackByteStreamWriter<Void>(callback) {
      @Override
      public void write(ByteStream stream) {

      }
    };
    test.failure(new ErrorCodeException(123));
    callback.assertFail(123);
  }
}
