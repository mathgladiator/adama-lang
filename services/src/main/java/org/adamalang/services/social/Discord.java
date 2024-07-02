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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.XWWWFormUrl;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Discord extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Discord.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String botToken;

  private final String userToken;


  public Discord(FirstPartyMetrics metrics, WebClientBase base, String botToken, String userToken) {
    super("discord", new NtPrincipal("discord", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.botToken = botToken;
    this.userToken = userToken;
  }

  public static Discord build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    return new Discord(metrics, base, config.getString("bot_token", ""), config.getString("user_token", ""));
  }

  private void apiCall(String method, String[] uri, String[] requiredParams, TreeMap<String, String> headers, ObjectNode requestNode, String body, Callback<String> callback) {
    SimpleHttpRequest discordReq = DiscordRequestBuilder.build(method, uri, requiredParams, headers, requestNode, body, callback);
    // Not sure if redundant, but useful for testing, maybe add execution into DiscordRequestBuilder? Would take away from the name and proper testing though.
    if (discordReq != null) {
      base.executeShared(discordReq, new StringCallbackHttpResponder(LOGGER, metrics.discord_apicall.start(), callback));
    }
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _DiscordChannel { string id; int type; string guild_id; int position; string name; string topic; bool nsfw; string last_message_id; int bitrate; ");
    sb.append("int user_limit; int rate_limit_per_user; string icon; string owner_id; string application_id; bool managed; string parent_id; string last_pin_timestamp; ");
    sb.append("string rtc_region; int video_quality_mode; int message_count; int member_count; int default_auto_archive_duration; string permissions; int flags; int total_message_sent; ");
    sb.append("int default_thread_rate_limit_per_user; int default_sort_order; int default_forum_layout; }\n");

    sb.append("service discord {\n");
    sb.append("  class=\"discord\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("bot_token") && !names.contains("user_token")) {
      error.accept("bot_token or user_token is required");
    } else if (names.contains("bot_token") && names.contains("user_token")) {
      error.accept("only one type of token is allowed");
    }
    sb.append("  method<dynamic, dynamic> GetUserGuilds;\n");
    // CHANNELS
    sb.append("  method<dynamic, dynamic> GetChannel;\n");
    sb.append("  method<dynamic, dynamic> ModifyChannel;\n");
    sb.append("  method<dynamic, dynamic> DeleteChannel;\n");
    sb.append("  method<dynamic, dynamic> GetChannelMessages;\n");
    sb.append("  method<dynamic, dynamic> GetChannelMessage;\n");
    sb.append("  method<dynamic, dynamic> CreateMessage;\n");
    sb.append("  method<dynamic, dynamic> CrosspostMessage;\n");
    sb.append("  method<dynamic, dynamic> CreateReaction;\n");
    sb.append("  method<dynamic, dynamic> DeleteOwnReaction;\n");
    sb.append("  method<dynamic, dynamic> DeleteUserReaction;\n");
    sb.append("  method<dynamic, dynamic> GetReactions;\n");
    sb.append("  method<dynamic, dynamic> DeleteAllReactions;\n");
    sb.append("  method<dynamic, dynamic> DeleteAllReactionsForEmoji;\n");
    sb.append("  method<dynamic, dynamic> EditMessage;\n");
    sb.append("  method<dynamic, dynamic> DeleteMessage;\n");
    sb.append("  method<dynamic, dynamic> BulkDeleteMessages;\n");
    sb.append("  method<dynamic, dynamic> EditChannelPermissions;\n");
    sb.append("  method<dynamic, dynamic> GetChannelInvites;\n");
    sb.append("  method<dynamic, dynamic> CreateChannelInvite;\n");
    sb.append("  method<dynamic, dynamic> DeleteChannelPermission;\n");
    sb.append("  method<dynamic, dynamic> FollowAnnouncementChannel;\n");
    sb.append("  method<dynamic, dynamic> TriggerTypingIndicator;\n");
    sb.append("  method<dynamic, dynamic> GetPinnedMessages;\n");
    sb.append("  method<dynamic, dynamic> PinMessage;\n");
    sb.append("  method<dynamic, dynamic> UnpinMessage;\n");
    sb.append("  method<dynamic, dynamic> GroupDMAddRecipient;\n");
    sb.append("  method<dynamic, dynamic> GroupDMRemoveRecipient;\n");
    sb.append("  method<dynamic, dynamic> StartThreadFromMessage;\n");
    sb.append("  method<dynamic, dynamic> StartThreadWithoutMessage;\n");
    sb.append("  method<dynamic, dynamic> StartThreadInForumOrMediaChannel;\n");
    sb.append("  method<dynamic, dynamic> JoinThread;\n");
    sb.append("  method<dynamic, dynamic> AddThreadMember;\n");
    sb.append("  method<dynamic, dynamic> LeaveThread;\n");
    sb.append("  method<dynamic, dynamic> RemoveThreadMember;\n");
    sb.append("  method<dynamic, dynamic> GetThreadMember;\n");
    sb.append("  method<dynamic, dynamic> ListThreadMembers;\n");
    sb.append("  method<dynamic, dynamic> ListPublicArchivedThreads;\n");
    sb.append("  method<dynamic, dynamic> ListPrivateArchivedThreads;\n");
    sb.append("  method<dynamic, dynamic> ListJoinedPrivateArchivedThreads;\n");
    // EMOJI
    sb.append("  method<dynamic, dynamic> ListGuildEmojis;\n");
    sb.append("  method<dynamic, dynamic> GetGuildEmoji;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildEmoji;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildEmoji;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuildEmoji;\n");
    // GUILD
    sb.append("  method<dynamic, dynamic> CreateGuild;\n");
    sb.append("  method<dynamic, dynamic> GetGuild;\n");
    sb.append("  method<dynamic, dynamic> GetGuildPreview;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuild;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuild;\n");
    sb.append("  method<dynamic, dynamic> GetGuildChannels;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildChannel;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildChannelPositions;\n");
    sb.append("  method<dynamic, dynamic> ListActiveGuildThreads;\n");
    sb.append("  method<dynamic, dynamic> GetGuildMember;\n");
    sb.append("  method<dynamic, dynamic> ListGuildMembers;\n");
    sb.append("  method<dynamic, dynamic> SearchGuildMembers;\n");
    sb.append("  method<dynamic, dynamic> AddGuildMember;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildMember;\n");
    sb.append("  method<dynamic, dynamic> ModifyCurrentMember;\n");
    sb.append("  method<dynamic, dynamic> AddGuildMemberRole;\n");
    sb.append("  method<dynamic, dynamic> RemoveGuildMemberRole;\n");
    sb.append("  method<dynamic, dynamic> RemoveGuildMember;\n");
    sb.append("  method<dynamic, dynamic> GetGuildBans;\n");
    sb.append("  method<dynamic, dynamic> GetGuildBan;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildBan;\n");
    sb.append("  method<dynamic, dynamic> RemoveGuildBan;\n");
    sb.append("  method<dynamic, dynamic> BulkGuildBan;\n");
    sb.append("  method<dynamic, dynamic> GetGuildRoles;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildRole;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildRolePositions;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildRole;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildMFALevel;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuildRole;\n");
    sb.append("  method<dynamic, dynamic> GetGuildPruneCount;\n");
    sb.append("  method<dynamic, dynamic> BeginGuildPrune;\n");
    sb.append("  method<dynamic, dynamic> GetGuildVoiceRegions;\n");
    sb.append("  method<dynamic, dynamic> GetGuildInvites;\n");
    sb.append("  method<dynamic, dynamic> GetGuildIntegrations;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuildIntegration;\n");
    sb.append("  method<dynamic, dynamic> GetGuildWidgetSettings;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildWidget;\n");
    sb.append("  method<dynamic, dynamic> GetGuildWidget;\n");
    sb.append("  method<dynamic, dynamic> GetGuildVanityURL;\n");
    sb.append("  method<dynamic, dynamic> GetGuildWidgetImage;\n");
    sb.append("  method<dynamic, dynamic> GetGuildWelcomeScreen;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildWelcomeScreen;\n");
    sb.append("  method<dynamic, dynamic> GetGuildOnboarding;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildOnboarding;\n");
    sb.append("  method<dynamic, dynamic> ModifyCurrentUserVoiceState;\n");
    sb.append("  method<dynamic, dynamic> ModifyUserVoiceState;\n");
    // GUILD SCHEDULED EVENT
    sb.append("  method<dynamic, dynamic> ListScheduledEventsForGuild;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildScheduledEvent;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildScheduledEvent;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuildScheduledEvent;\n");
    sb.append("  method<dynamic, dynamic> GetGuildScheduledEventUsers;\n");
    // GUILD TEMPLATE
    sb.append("  method<dynamic, dynamic> GetGuildTemplate;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildFromGuildTemplate;\n");
    sb.append("  method<dynamic, dynamic> GetGuildTemplates;\n");
    sb.append("  method<dynamic, dynamic> CreateGuildTemplate;\n");
    sb.append("  method<dynamic, dynamic> SyncGuildTemplate;\n");
    sb.append("  method<dynamic, dynamic> ModifyGuildTemplate;\n");
    sb.append("  method<dynamic, dynamic> DeleteGuildTemplate;\n");
    // INVITE
    sb.append("  method<dynamic, dynamic> GetInvite;\n");
    sb.append("  method<dynamic, dynamic> DeleteInvite;\n");
    // POLL
    sb.append("  method<dynamic, dynamic> GetAnswerVoters;\n");
    sb.append("  method<dynamic, dynamic> EndPoll;\n");
    // STAGE INSTANCE
    sb.append("  method<dynamic, dynamic> CreateStageInstance;\n");
    sb.append("  method<dynamic, dynamic> GetStageInstance;\n");
    sb.append("  method<dynamic, dynamic> ModifyStageInstance;\n");
    sb.append("  method<dynamic, dynamic> DeleteStageInstance;\n");
    // STICKER
    sb.append(" method<dynamic, dynamic> GetSticker;\n");
    sb.append(" method<dynamic, dynamic> ListStickerPacks;\n");
    sb.append(" method<dynamic, dynamic> ListGuildStickers;\n");
    sb.append(" method<dynamic, dynamic> GetGuildSticker;\n");
    sb.append(" method<dynamic, dynamic> ModifyGuildSticker;\n");
    sb.append(" method<dynamic, dynamic> DeleteGuildSticker;\n");
    // USER
    sb.append("method<dynamic, dynamic> GetCurrentUser;\n");
    sb.append("method<dynamic, dynamic> GetUser;\n");
    sb.append("method<dynamic, dynamic> ModifyCurrentUser;\n");
    sb.append("method<dynamic, dynamic> GetCurrentUserGuilds;\n");
    sb.append("method<dynamic, dynamic> GetCurrentUserGuildMember;\n");
    sb.append("method<dynamic, dynamic> LeaveGuild;\n");
    sb.append("method<dynamic, dynamic> CreateDM;\n");
    sb.append("method<dynamic, dynamic> CreateGroupDM;\n");
    sb.append("method<dynamic, dynamic> GetCurrentUserConnections;\n");
    sb.append("method<dynamic, dynamic> GetCurrentUserApplicationRoleConnection;\n");
    sb.append("method<dynamic, dynamic> UpdateCurrentUserApplicationRoleConnection;\n");
    // VOICE
    sb.append("method<dynamic, dynamic> ListVoiceRegions;\n");
    // WEBHOOKS
    sb.append("method<dynamic, dynamic> CreateWebhook;\n");
    sb.append("method<dynamic, dynamic> GetChannelWebhooks;\n");
    sb.append("method<dynamic, dynamic> GetGuildWebhooks;\n");
    sb.append("method<dynamic, dynamic> GetWebhook;\n");
    sb.append("method<dynamic, dynamic> GetWebhookWithToken;\n");
    sb.append("method<dynamic, dynamic> ModifyWebhook;\n");
    sb.append("method<dynamic, dynamic> ModifyWebhookWithToken;\n");
    sb.append("method<dynamic, dynamic> DeleteWebhook;\n");
    sb.append("method<dynamic, dynamic> DeleteWebhookWithToken;\n");
    sb.append("method<dynamic, dynamic> ExecuteWebhook;\n");
    sb.append("method<dynamic, dynamic> ExecuteSlackCompatibleWebhook;\n");
    sb.append("method<dynamic, dynamic> ExecuteGithubCompatibleWebhook;\n");
    sb.append("method<dynamic, dynamic> GetWebhookMessage;\n");
    sb.append("method<dynamic, dynamic> EditWebhookMessage;\n");
    sb.append("method<dynamic, dynamic> DeleteWebhookMessage;\n");

    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode requestNode = Json.parseJsonObject(request);
    String body = requestNode.toString();
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("Content-Type", "application/json");
    if (!botToken.equals("")) {
      headers.put("Authorization", "Bot " + botToken);
    } else {
      headers.put("Authorization", userToken);
    }

    switch (method) {
      case "GetUserGuilds": {
        apiCall("GET", new String[]{"/users/@me/guilds"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      // CHANNEL
      case "GetChannel": {
        apiCall("GET", new String[]{"/channels"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyChannel": {
        apiCall("PATCH", new String[]{"/channels"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteChannel": {
        apiCall("DELETE", new String[]{"/channels"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetChannelMessages": {
        apiCall("GET", new String[]{"/channels", "/messages"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetChannelMessage": {
        apiCall("GET", new String[]{"/channels", "/messages"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateMessage": {
        //TODO: Requires at least one out of a list
        apiCall("POST", new String[]{"/channels", "/messages"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CrosspostMessage": {
        apiCall("POST", new String[]{"/channels","/messages","/crosspost"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateReaction": {
        apiCall("PUT", new String[]{"/channels","/messages","/reactions","/@me"}, new String[]{"channel_id", "message_id", "emoji"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteOwnReaction": {
        apiCall("DELETE", new String[]{"/channels","/messages","/reactions","/@me"}, new String[]{"channel_id", "message_id", "emoji"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteUserReaction": {
        apiCall("DELETE", new String[]{"/channels","/messages","/reactions"}, new String[]{"channel_id", "message_id", "emoji", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetReactions": {
        //TODO: Query String
        apiCall("GET", new String[]{"/channels","/messages","/reactions"}, new String[]{"channel_id", "message_id", "emoji"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteAllReactions": {
        apiCall("DELETE", new String[]{"/channels","/messages","/reactions"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteAllReactionsForEmoji": {
        apiCall("DELETE", new String[]{"/channels", "/messages", "/reactions"}, new String[]{"channel_id", "message_id", "emoji"}, headers, requestNode, body, callback);
        return;
      }
      case "EditMessage": {
        apiCall("PATCH", new String[]{"/channels", "/messages"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteMessage": {
        apiCall("DELETE", new String[]{"/channels", "/messages"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "BulkDeleteMessages": {
        //TODO: Requires testing because of array, allow for array
        apiCall("POST", new String[]{"/channels", "/messages", "/bulk-delete"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "EditChannelPermissions": {
        //TODO: TEST IF NEEDS TYPE PARAM
        apiCall("PUT", new String[]{"/channels", "/permissions"}, new String[]{"channel_id", "overwrite_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetChannelInvites": {
        apiCall("GET", new String[]{"/channels","/invites"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateChannelInvite": {
        //TODO: TEST IF STILL WORKS WITHOUT BODY
        apiCall("POST", new String[]{"/channels", "/invites"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteChannelPermission": {
        apiCall("DELETE", new String[]{"/channels", "/permissions"}, new String[]{"channel_id", "overwrite_id"}, headers, requestNode, body, callback);
        return;
      }
      case "FollowAnnouncementChannel": {
        apiCall("POST", new String[]{"/channels", "/followers"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "TriggerTypingIndicator": {
        apiCall("POST", new String[]{"/channels", "/typing"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetPinnedMessages": {
        apiCall("GET", new String[]{"/channels", "/pins"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "PinMessage": {
        apiCall("PUT", new String[]{"/channels", "/pins"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "UnpinMessage": {
        apiCall("DELETE", new String[]{"/channels", "/pins"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GroupDmAddRecipient": {
        //TODO: See if Params are required
        apiCall("PUT", new String[]{"/channels", "/recipients"}, new String[]{"channel_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GroupDMRemoveRecipient": {
        apiCall("DELETE", new String[]{"/channels", "/recipients"}, new String[]{"channel_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "StartThreadFromMessage": {
        apiCall("POST", new String[]{"/channels", "/messages", "/threads"}, new String[]{"channel_id", "message_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "StartThreadWithoutMessage": {
        apiCall("POST", new String[]{"/channels", "/threads"}, new String[]{"channel_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "StartThreadInForumOrMediaChannel": {
        apiCall("POST", new String[]{"/channels", "/threads"}, new String[]{"channel_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "JoinThread": {
        apiCall("PUT", new String[]{"/channels", "/thread-members", "/@me"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "AddThreadMember": {
        apiCall("PUT", new String[]{"/channels", "/thread-members"}, new String[]{"channel_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "LeaveThread": {
        apiCall("DELETE", new String[]{"/channels", "/thread-members", "/@me"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "RemoveThreadMember": {
        apiCall("DELETE", new String[]{"/channels", "/thread-members"}, new String[]{"channel_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetThreadMember": {
        //TODO: Query String Params
        apiCall("GET", new String[]{"/channels", "/thread-members"}, new String[]{"channel_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListThreadMembers": {
        //TODO: Query String Params
        apiCall("GET", new String[]{"/channels", "/thread-members"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListPublicArchivedThreads": {
        //TODO: Query String Params
        apiCall("GET", new String[]{"/channels", "/threads", "/archived", "/public"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListPrivateArchivedThreads": {
        //TODO: Query String Params
        apiCall("GET", new String[]{"/channels", "/threads/archived/private"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListJoinedPrivateArchivedThreads": {
        apiCall("GET", new String[]{"/channels", "/users", "/@me", "/threads", "/archived", "/private"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListGuildEmojis": {
        apiCall("GET", new String[]{"/guilds", "/emojis"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildEmoji": {
        apiCall("GET", new String[]{"/guilds", "/emojis"}, new String[]{"guild_id", "emoji_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildEmoji": {
        apiCall("POST", new String[]{"/guilds", "/emojis"}, new String[]{"guild_id", "_name", "_image"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildEmoji": {
        apiCall("PATCH", new String[]{"/guilds", "/emojis"}, new String[]{"guild_id", "emoji_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildEmoji": {
        apiCall("DELETE", new String[]{"/guilds", "/emojis"}, new String[]{"guild_id", "emoji_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuild": {
        apiCall("POST", new String[]{"/guilds"}, new String[]{"_name"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuild": {
        apiCall("GET", new String[]{"/guilds"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildPreview": {
        apiCall("GET", new String[]{"/guilds", "/preview"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuild": {
        apiCall("PATCH", new String[]{"/guilds"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuild": {
        apiCall("DELETE", new String[]{"/guilds"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildChannels": {
        apiCall("GET", new String[]{"/guilds", "/channels"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildChannel": {
        apiCall("POST", new String[]{"/guilds", "/channels"}, new String[]{"guild_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildChannelPositions": {
        apiCall("PATCH", new String[]{"/guilds", "/channels"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListActiveGuildThreads": {
        apiCall("GET", new String[]{"/guilds", "/threads", "/active"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildMember": {
        apiCall("GET", new String[]{"/guilds", "/members"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListGuildMembers": {
        apiCall("GET", new String[]{"/guilds","/members"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "SearchGuildMembers": {
        apiCall("GET", new String[]{"/guilds", "/members", "/search"}, new String[]{"guild_id", "_query"}, headers, requestNode, body, callback);
        return;
      }
      case "AddGuildMember": {
        apiCall("PUT", new String[]{"/guilds", "/members"}, new String[]{"guild_id", "user_id", "_access_token"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildMember": {
        apiCall("PATCH", new String[]{"/guilds", "/members"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyCurrentMember": {
        apiCall("PATCH", new String[]{"/guilds", "/members", "/@me"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "AddGuildMemberRole": {
        apiCall("PUT", new String[]{"/guilds", "/members", "/roles"}, new String[]{"guild_id", "user_id", "role_id"}, headers, requestNode, body, callback);
        return;
      }
      case "RemoveGuildMemberRole": {
        apiCall("DELETE", new String[]{"/guilds", "/members", "/roles"}, new String[]{"guild_id", "user_id", "role_id"}, headers, requestNode, body, callback);
        return;
      }
      case "RemoveGuildMember": {
        apiCall("DELETE", new String[]{"/guilds", "/members"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildBans": {
        //TODO: Query String Params
        apiCall("GET", new String[]{"/guilds", "/bans"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildBan": {
        apiCall("GET", new String[]{"/guilds", "/bans"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildBan": {
        apiCall("PUT", new String[]{"/guilds", "/bans"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "RemoveGuildBan": {
        apiCall("DELETE", new String[]{"/guilds", "/bans"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "BulkGuildBan": {
        apiCall("POST", new String[]{"/guilds", "/bulk-ban"}, new String[]{"guild_id", "_user_ids"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildRoles": {
        apiCall("GET", new String[]{"/guilds", "/roles"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildRole": {
        apiCall("POST", new String[]{"/guilds", "/roles"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildRolePositions": {
        apiCall("PATCH", new String[]{"/guilds", "/roles"}, new String[]{"guild_id", "_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildRole": {
        apiCall("PATCH", new String[]{"/guilds", "/roles"}, new String[]{"guild_id", "role_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildMFALevel": {
        //TODO: Check if Level is necessary
        apiCall("POST", new String[]{"/guilds", "/mfa"}, new String[]{"guild_id", "_level"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildRole": {
        apiCall("DELETE", new String[]{"/guilds", "/roles"}, new String[]{"guild_id", "role_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildPruneCount": {
        apiCall("GET", new String[]{"/guilds", "/prune"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "BeginGuildPrune": {
        apiCall("POST", new String[]{"/guilds", "/prune"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildVoiceRegions": {
        apiCall("GET", new String[]{"/guilds", "/regions"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildInvites": {
        apiCall("GET", new String[]{"/guilds", "/invites"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildIntegrations": {
        apiCall("GET", new String[]{"/guilds", "/integrations"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildIntegration": {
        apiCall("DELETE", new String[]{"/guilds", "/integrations"}, new String[]{"guild_id", "integration_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildWidgetSettings": {
        apiCall("GET", new String[]{"/guilds", "/widget"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildWidget" : {
        apiCall("PATCH", new String[]{"/guilds", "/widget"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildWidget" : {
        apiCall("GET", new String[]{"/guilds", "/widget.json"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildVanityURL" : {
        apiCall("GET", new String[]{"/guilds", "/vanity-url"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildWidgetImage" : {
        apiCall("GET", new String[]{"/guilds", "/widget.png"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildWelcomeScreen" : {
        apiCall("GET", new String[]{"/guilds", "/welcome-screen"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildWelcomeScreen" : {
        apiCall("PATCH", new String[]{"/guilds", "/welcome-screen"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildOnboarding" : {
        apiCall("GET", new String[]{"/guilds", "/onboarding"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildOnboarding" : {
        //TODO: Check if requires JSON params
        apiCall("PUT", new String[]{"/guilds", "/onboarding"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyCurrentUserVoiceState" : {
        apiCall("PATCH", new String[]{"/guilds", "/voice-states", "/@me"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyUserVoiceState" : {
        //TODO: Check if channel_id is required
        apiCall("PATCH", new String[]{"/guilds", "/voice-states"}, new String[]{"guild_id", "user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListScheduledEventsForGuild": {
        apiCall("GET", new String[]{"/guilds", "/scheduled-events"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildScheduledEvent": {
        apiCall("POST", new String[]{"/guilds", "/scheduled-events"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildScheduledEvent": {
        apiCall("GET", new String[]{"/guilds", "/scheduled-events"}, new String[]{"guild_id", "guild_scheduled_event_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildScheduledEvent": {
        apiCall("PATCH", new String[]{"/guilds", "/scheduled-events"}, new String[]{"guild_id", "guild_scheduled_event_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildScheduledEvent": {
        apiCall("DELETE", new String[]{"/guilds", "/scheduled-events"}, new String[]{"guild_id", "guild_scheduled_event_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildScheduledEventUsers": {
        apiCall("GET", new String[]{"/guilds", "/scheduled-events", "/users"}, new String[]{"guild_id", "guild_scheduled_event_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildTemplate": {
        apiCall("GET", new String[]{"/guilds/templates"}, new String[]{"template_code"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildFromGuildTemplate": {
        apiCall("POST", new String[]{"/guilds/templates"}, new String[]{"template_code", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildTemplates": {
        apiCall("GET", new String[]{"/guilds", "/templates"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGuildTemplate": {
        apiCall("POST", new String[]{"/guilds", "/templates"}, new String[]{"guild_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "SyncGuildTemplate": {
        apiCall("PUT", new String[]{"/guilds", "/templates"}, new String[]{"guild_id", "template_code"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildTemplate": {
        apiCall("PATCH", new String[]{"/guilds", "/templates"}, new String[]{"guild_id", "template_code"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildTemplate": {
        apiCall("DELETE", new String[]{"/guilds", "/templates"}, new String[]{"guild_id", "template_code"}, headers, requestNode, body, callback);
        return;
      }
      case "GetInvite": {
        apiCall("GET", new String[]{"/invites"}, new String[]{"invite_code"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteInvite": {
        apiCall("DELETE", new String[]{"/invites"}, new String[]{"invite_code"}, headers, requestNode, body, callback);
        return;
      }
      case "GetAnswerVoters": {
        apiCall("GET", new String[]{"/channels", "/polls", "/answers"}, new String[]{"channel_id", "message_id", "answer_id"}, headers, requestNode, body, callback);
        return;
      }
      case "EndPoll": {
        apiCall("POST", new String[]{"/channels", "/polls", "/expire"}, new String[]{"channel_id", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateStageInstance": {
        apiCall("POST", new String[]{"/stage-instances"}, new String[]{"_channel_id", "_topic"}, headers, requestNode, body, callback);
        return;
      }
      case "GetStageInstance": {
        apiCall("GET", new String[]{"/stage-instances"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyStageInstance": {
        apiCall("PATCH", new String[]{"/stage-instances"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteStageInstance": {
        apiCall("DELETE", new String[]{"/stage-instances"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetSticker": {
        apiCall("GET", new String[]{"/stickers"}, new String[]{"sticker_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListStickerPacks": {
        apiCall("GET", new String[]{"/sticker-packs"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "ListGuildStickers": {
        apiCall("GET", new String[]{"/guilds", "/stickers"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildSticker": {
        apiCall("GET", new String[]{"/guilds", "/stickers"}, new String[]{"guild_id", "sticker_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyGuildSticker": {
        apiCall("PATCH", new String[]{"/guilds", "/stickers"}, new String[]{"guild_id", "sticker_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteGuildSticker": {
        apiCall("DELETE", new String[]{"/guilds", "/stickers"}, new String[]{"guild_id", "sticker_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetCurrentUser": {
        apiCall("GET", new String[]{"/users", "/@me"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "GetUser": {
        apiCall("GET", new String[]{"/users"}, new String[]{"user_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyCurrentUser": {
        apiCall("PATCH", new String[]{"/users", "/@me"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "GetCurrentUserGuilds": {
        apiCall("GET", new String[]{"/users", "/@me", "/guilds"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "GetCurrentUserGuildMember": {
        apiCall("GET", new String[]{"/users/@me/guilds", "/member"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "LeaveGuild": {
        apiCall("DELETE", new String[]{"/users/@me/guilds"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateDM": {
        apiCall("POST", new String[]{"/users/@me/channels"}, new String[]{"_recipient_id"}, headers, requestNode, body, callback);
        return;
      }
      case "CreateGroupDM": {
        apiCall("POST", new String[]{"/users/@me/channels"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "GetCurrentUserConnections": {
        apiCall("GET", new String[]{"/users/@me/connections"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "GetCurrentUserApplicationRoleConnection": {
        apiCall("GET", new String[]{"/users/@me/applications", "/role-connection"}, new String[]{"application_id"}, headers, requestNode, body, callback);
        return;
      }
      case "UpdateCurrentUserApplicationRoleConnection": {
        apiCall("PUT", new String[]{"/users/@me/applications", "/role-connection"}, new String[]{"application_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ListVoiceRegions": {
        apiCall("GET", new String[]{"/voice/regions"}, new String[]{}, headers, requestNode, body, callback);
        return;
      }
      case "CreateWebhook": {
        apiCall("POST", new String[]{"/channels", "/webhooks"}, new String[]{"channel_id", "_name"}, headers, requestNode, body, callback);
        return;
      }
      case "GetChannelWebhooks": {
        apiCall("GET", new String[]{"/channels", "/webhooks"}, new String[]{"channel_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetGuildWebhooks": {
        apiCall("GET", new String[]{"/guilds", "/webhooks"}, new String[]{"guild_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetWebhook": {
        apiCall("GET", new String[]{"/webhooks"}, new String[]{"webhook_id"}, headers, requestNode, body, callback);
        return;
      }
      case "GetWebhookWithToken": {
        apiCall("GET", new String[]{"/webhooks"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyWebhook": {
        apiCall("PATCH", new String[]{"/webhooks"}, new String[]{"webhook_id"}, headers, requestNode, body, callback);
        return;
      }
      case "ModifyWebhookWithToken": {
        apiCall("PATCH", new String[]{"/webhooks"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteWebhook": {
        apiCall("DELETE", new String[]{"/webhooks"}, new String[]{"webhook_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteWebhookWithToken": {
        apiCall("DELETE", new String[]{"/webhooks"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "ExecuteWebhook": {
        apiCall("POST", new String[]{"/webhooks"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "ExecuteSlackCompatibleWebhook": {
        apiCall("POST", new String[]{"/webhooks","","/slack"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "ExecuteGithubCompatibleWebhook": {
        apiCall("POST", new String[]{"/webhooks","","/github"}, new String[]{"webhook_id", "webhook_token"}, headers, requestNode, body, callback);
        return;
      }
      case "GetWebhookMessage": {
        apiCall("GET", new String[]{"/webhooks", "", "/messages"}, new String[]{"webhook_id", "webhook_token", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "EditWebhookMessage": {
        apiCall("PATCH", new String[]{"/webhooks", "", "/messages"}, new String[]{"webhook_id", "webhook_token", "message_id"}, headers, requestNode, body, callback);
        return;
      }
      case "DeleteWebhookMessage": {
        apiCall("DELETE", new String[]{"/webhooks", "", "/messages"}, new String[]{"webhook_id", "webhook_token", "message_id"}, headers, requestNode, body, callback);
        return;
      }

      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
