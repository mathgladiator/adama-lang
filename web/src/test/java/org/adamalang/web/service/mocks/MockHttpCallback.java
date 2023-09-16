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
package org.adamalang.web.service.mocks;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MockHttpCallback implements Callback<FullHttpResponse> {
  public FullHttpResponse response;
  public ErrorCodeException ex;
  private CountDownLatch done;

  public MockHttpCallback() {
    this.done = new CountDownLatch(1);
  }

  public void awaitDone() throws Exception {
    Assert.assertTrue(done.await(1000, TimeUnit.MILLISECONDS));
  }

  public void assertErrorCode(int code) {
    Assert.assertNull(response);
    Assert.assertNotNull(ex);
    Assert.assertEquals(code, ex.code);
  }

  public void assertStatusCode(int code) {
    Assert.assertNull(ex);
    Assert.assertNotNull(response);
    Assert.assertEquals(HttpResponseStatus.valueOf(code), response.status());
  }

  public void assertBody(String body) {
    Assert.assertNull(ex);
    Assert.assertNotNull(response);
    Assert.assertEquals(
        body.trim(),
        new String(response.content().array()).trim().replaceAll(Pattern.quote("\r"), ""));
  }

  @Override
  public void success(FullHttpResponse value) {
    this.response = value;
    done.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.ex = ex;
    done.countDown();
  }
}
