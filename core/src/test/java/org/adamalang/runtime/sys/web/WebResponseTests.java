/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
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

  @Test
  public void save_empty() {
    WebResponse response = new WebResponse();
    JsonStreamWriter writer = new JsonStreamWriter();
    response.writeAsObject(writer);
    Assert.assertEquals("{}", writer.toString());
  }

  @Test
  public void save_many() {
    WebResponse response = new WebResponse();
    response.cors = true;
    response.asset = NtAsset.NOTHING;
    response.asset_transform = "transform";
    response.body = "body";
    response.contentType = "type";
    response.cache_ttl_seconds = 42;
    JsonStreamWriter writer = new JsonStreamWriter();
    response.writeAsObject(writer);
    Assert.assertEquals("{\"content-type\":\"type\",\"body\":\"body\",\"asset\":{\"id\":\"\",\"size\":\"0\",\"name\":\"\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\",\"@gc\":\"@yes\"},\"asset-transform\":\"transform\",\"cors\":true,\"cache-ttl-seconds\":42}", writer.toString());
  }

  @Test
  public void load_many() {
    JsonStreamReader reader = new JsonStreamReader("{\"content-type\":\"type\",\"body\":\"body\",\"asset\":{\"id\":\"\",\"size\":\"0\",\"name\":\"\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\",\"@gc\":\"@yes\"},\"asset-transform\":\"transform\",\"cors\":true,\"cache-ttl-seconds\":42}");
    WebResponse response = WebResponse.readFromObject(reader);
    Assert.assertEquals("transform", response.asset_transform);
    Assert.assertEquals("body", response.body);
    Assert.assertEquals("type", response.contentType);
    Assert.assertEquals(42, response.cache_ttl_seconds);
    Assert.assertEquals(NtAsset.NOTHING, response.asset);
    Assert.assertTrue(response.cors);
  }


}
