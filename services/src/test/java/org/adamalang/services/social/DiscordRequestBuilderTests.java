/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.services.social;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.client.SimpleHttpRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class DiscordRequestBuilderTests {
  private TreeMap<String, String> headers = new TreeMap<>();
  private ObjectNode requestNode = new ObjectNode(JsonNodeFactory.instance);
  private String body = "";
  private Callback<String> callback = new Callback<String>() {
    @Override
    public void success(String value) {

    }

    @Override
    public void failure(ErrorCodeException ex) {

    }
  };
  @Test
  public void standardRequest() {
    requestNode.put("channel_id", "channelid");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels", "/messages"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithRequiredBody(){
    requestNode.put("guild_id", "guildid");
    requestNode.put("name", "nameOfChannel");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/guilds", "/channels"}, new String[]{"guild_id", "_name"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/guilds/guildid/channels", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithIncorrectOrder() {
    requestNode.put("guild_id", "guildid");
    requestNode.put("name", "nameOfChannel");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/guilds", "/channels"}, new String[]{"_name", "guild_id"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertNotEquals("https://discord.com/api/guilds/guildid/channels", req.url);
    Assert.assertEquals("https://discord.com/api/guilds/channels", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithIncorrectOrder2() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("message_id", "messageid");
    requestNode.put("name", "nameOfThread");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels", "/messages", "/threads"}, new String[]{"channel_id", "_name", "message_id"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertNotEquals("https://discord.com/api/channels/channelid/messages/messageid/thread", req.url);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/threads", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithCorrectOrder() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("message_id", "messageid");
    requestNode.put("name", "nameOfThread");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels", "/messages", "/threads"}, new String[]{"channel_id", "message_id", "_name"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/messageid/threads", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithCorrectOrderComplex() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("name", "nameOfThread");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels", "/messages", "/specificmessageid", "/threads"}, new String[]{"channel_id", "_name"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/specificmessageid/threads", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithCorrectOrderComplexAlternate() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("name", "nameOfThread");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels", "/messages/specificmessageid", "/threads"}, new String[]{"channel_id", "_name"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/specificmessageid/threads", req.url);
    Assert.assertEquals("POST", req.method);
  }

  @Test
  public void requestWithoutAnyRequiredParams() {
    SimpleHttpRequest req = DiscordRequestBuilder.build("GET", new String[]{"/channels/adamachannel/messages/copyrightMessage"}, new String[]{}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/adamachannel/messages/copyrightMessage", req.url);
    Assert.assertEquals("GET", req.method);
  }

  @Test
  public void incomingRequestMissingRequiredParams() {
    requestNode.put("uselessparam", "hello!");
    SimpleHttpRequest req = DiscordRequestBuilder.build("GET", new String[]{"/channels"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
    Assert.assertNull(req);
  }

  @Test
  public void requestEmptyParamsAndURI() {
    SimpleHttpRequest req = DiscordRequestBuilder.build("GET", new String[]{}, new String[]{}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api", req.url);
    Assert.assertEquals("GET", req.method);
  }

  @Test
  public void requestExtraRequiredParams() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("message_id", "messageid");
    requestNode.put("emoji", "emoji");
    requestNode.put("user_id", "userid");
    SimpleHttpRequest req = DiscordRequestBuilder.build("DELETE", new String[]{"/channels","/messages","/reactions"}, new String[]{"channel_id", "message_id", "emoji", "user_id"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/messageid/reactions/emoji/userid", req.url);
    Assert.assertEquals("DELETE", req.method);
  }

  @Test
  public void requestExtraRequiredParamsWithBodyParams() {
    requestNode.put("channel_id", "channelid");
    requestNode.put("message_id", "messageid");
    requestNode.put("emoji", "emoji");
    requestNode.put("user_id", "userid");
    requestNode.put("requiredBody", "true");
    SimpleHttpRequest req = DiscordRequestBuilder.build("POST", new String[]{"/channels","/messages","/reactions"}, new String[]{"channel_id", "message_id", "emoji", "user_id", "_requiredBody"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/channelid/messages/messageid/reactions/emoji/userid", req.url);
    Assert.assertEquals("POST", req.method);

  }

  @Test
  public void requestManyURIPathsLittleRequiredParams() {
    requestNode.put("channel_id", "{channel.id}");
    SimpleHttpRequest req = DiscordRequestBuilder.build("GET", new String[]{"/channels", "/users", "/@me", "/threads", "/archived", "/private"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
    Assert.assertNotNull(req);
    Assert.assertEquals("https://discord.com/api/channels/{channel.id}/users/@me/threads/archived/private", req.url);
    Assert.assertEquals("GET", req.method);
  }
}
