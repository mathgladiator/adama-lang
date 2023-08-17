package org.adamalang.runtime.sys.domains;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CachedDomainFinderTests {
  @Test
  public void passthrough() throws Exception {
    MockDomainFinder mock = new MockDomainFinder() //
        .with("host", new Domain("domain", 1, "space", "key", false, "", null, 123L));
    CachedDomainFinder finder = new CachedDomainFinder(TimeSource.REAL_TIME, 100, 100000, SimpleExecutor.NOW, mock);
    CountDownLatch latch = new CountDownLatch(1);
    finder.find("host", new Callback<Domain>() {
      @Override
      public void success(Domain value) {
        Assert.assertEquals("domain", value.domain);
        Assert.assertEquals("space", value.space);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }
}
