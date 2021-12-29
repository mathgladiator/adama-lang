/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client.contracts;

import io.grpc.stub.StreamObserver;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.grpc.proto.CreateResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateCallbackTests {
  @Test
  public void coverage() {
    AtomicInteger failure = new AtomicInteger(0);
    AtomicBoolean success = new AtomicBoolean(false);

    StreamObserver<CreateResponse> observer =
        CreateCallback.WRAP(
            new CreateCallback() {
              @Override
              public void created() {
                success.set(true);
              }

              @Override
              public void error(int code) {
                failure.set(code);
              }
            },
            new ExceptionLogger() {
              @Override
              public void convertedToErrorCode(Throwable t, int errorCode) {}
            });
    observer.onNext(CreateResponse.newBuilder().setSuccess(true).build());
    observer.onCompleted();
    observer.onError(new NullPointerException());
    Assert.assertEquals(723982, failure.get());
    Assert.assertTrue(success.get());
    success.set(false);
    observer.onNext(CreateResponse.newBuilder().setFailureReason(32).build());
    Assert.assertEquals(32, failure.get());
    Assert.assertFalse(success.get());
  }
}
