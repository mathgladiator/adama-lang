package org.adamalang.web.client.pool;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class WebEndpointTests {
  @Test
  public void coverage() {
    WebEndpoint we = new WebEndpoint(URI.create("https://www.adama-platform.com"));
    Assert.assertTrue(we.secure);
    Assert.assertEquals(443, we.port);
    Assert.assertEquals("www.adama-platform.com", we.host);
    Assert.assertFalse(we.equals(null));
    Assert.assertFalse(we.equals("www"));
    Assert.assertTrue(we.equals(we));
    Assert.assertTrue(we.equals(new WebEndpoint(URI.create("https://www.adama-platform.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("https://www.adama-platformx.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("http://www.adama-platform.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("https://www.adama-platform.com:444"))));
    we.hashCode();
  }

  @Test
  public void port_override() {
    WebEndpoint we = new WebEndpoint(URI.create("https://www.adama-platform.com:77"));
    Assert.assertEquals(77, we.port);
  }
}
