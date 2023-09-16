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
    response.asset_transform("transform").cache_ttl_seconds(100);
    Assert.assertEquals("id", response.asset.id);
    Assert.assertEquals("contentType", response.contentType);
    Assert.assertEquals("transform", response.asset_transform);
    Assert.assertEquals(100, response.cache_ttl_seconds);
    PredictiveInventory inventory = new PredictiveInventory();
    response.account(inventory);
    Assert.assertEquals(42, inventory.sample().bandwidth);
  }

  @Test
  public void flow_error() {
    WebResponse response = new WebResponse();
    response.error("message");
    Assert.assertEquals("message", response.body);
    Assert.assertEquals("text/error", response.contentType);
  }

  @Test
  public void flow_sign() {
    WebResponse response = new WebResponse();
    response.sign("agent");
    Assert.assertEquals("agent", response.body);
    Assert.assertEquals("text/agent", response.contentType);
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
    JsonStreamReader reader = new JsonStreamReader("{\"content-type\":\"type\",\"body\":\"body\",\"asset\":{\"id\":\"\",\"size\":\"0\",\"name\":\"\",\"type\":\"\",\"md5\":\"\",\"sha384\":\"\",\"@gc\":\"@yes\"},\"asset-transform\":\"transform\",\"cors\":true,\"cache-ttl-seconds\":42,\"junk\":1}");
    WebResponse response = WebResponse.readFromObject(reader);
    Assert.assertEquals("transform", response.asset_transform);
    Assert.assertEquals("body", response.body);
    Assert.assertEquals("type", response.contentType);
    Assert.assertEquals(42, response.cache_ttl_seconds);
    Assert.assertEquals(NtAsset.NOTHING, response.asset);
    Assert.assertTrue(response.cors);
  }

  @Test
  public void skip() {
    JsonStreamReader reader = new JsonStreamReader("\"123\"");
    WebResponse response = WebResponse.readFromObject(reader);
    Assert.assertNull(response);
  }

}
