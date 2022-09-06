/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.mocks.MockMessage;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class WebResponseTests {
  @Test
  public void flow_xml() {
    WebResponse response = new WebResponse();
    response.xml("x");
    Assert.assertEquals("x", response.body);
    Assert.assertEquals("application/xml", response.contentType);
    PredictiveInventory inventory = new PredictiveInventory();
    response.account(inventory);
    Assert.assertEquals(1, inventory.sample().bandwidth);
  }

  @Test
  public void flow_json() {
    WebResponse response = new WebResponse();
    response.json(new MockMessage());
    Assert.assertEquals("{\"x\":42,\"y\":13}", response.body);
    Assert.assertEquals("application/json", response.contentType);
    PredictiveInventory inventory = new PredictiveInventory();
    response.account(inventory);
    Assert.assertEquals(15, inventory.sample().bandwidth);
  }

  @Test
  public void flow_html() {
    WebResponse response = new WebResponse();
    response.html("HTTTTMMMEl");
    Assert.assertEquals("HTTTTMMMEl", response.body);
    Assert.assertEquals("text/html; charset=utf-8", response.contentType);
    PredictiveInventory inventory = new PredictiveInventory();
    response.account(inventory);
    Assert.assertEquals(10, inventory.sample().bandwidth);
  }

  @Test
  public void flow_asset() {
    WebResponse response = new WebResponse();
    response.asset(new NtAsset("id", "name", "contentType", 42, "md5", "sha384"));
    response.asset_transform("transform");
    Assert.assertEquals("id", response.asset.id);
    Assert.assertEquals("contentType", response.contentType);
    Assert.assertEquals("transform", response.asset_transform);
    PredictiveInventory inventory = new PredictiveInventory();
    response.account(inventory);
    Assert.assertEquals(42, inventory.sample().bandwidth);
  }
}
