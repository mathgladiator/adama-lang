/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import org.adamalang.api.ClientHostInitResponse;
import org.adamalang.api.ClientRegionalInitHostRequest;
import org.adamalang.api.SelfClient;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** library to remotely initialize the host */
public class RegionInit {
  public final int publicKeyId;

  private RegionInit(int publicKeyId) {
    this.publicKeyId = publicKeyId;
  }

  public static RegionInit init(SelfClient client, String identity, String region, String machine, String role, String publicKey) throws Exception {
    ClientRegionalInitHostRequest request = new ClientRegionalInitHostRequest();
    request.identity = identity;
    request.region = region;
    request.machine = machine;
    request.role = role;
    request.publicKey = publicKey;

    int attempts = 3;
    while (attempts > 0) {
      attempts--;
      CountDownLatch latch = new CountDownLatch(1);
      AtomicInteger publicKeyId = new AtomicInteger(-1);
      client.regionalInitHost(request, new Callback<ClientHostInitResponse>() {
        @Override
        public void success(ClientHostInitResponse value) {
          publicKeyId.set(value.keyId);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          latch.countDown();
        }
      });
      if (latch.await(5000, TimeUnit.MILLISECONDS)) {
        int got = publicKeyId.get();
        if (got > 0) {
          return new RegionInit(got);
        }
      }
    }
    throw new Exception("failed to init remote region host");
  }
}
