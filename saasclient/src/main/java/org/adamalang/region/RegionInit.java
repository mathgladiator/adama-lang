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
