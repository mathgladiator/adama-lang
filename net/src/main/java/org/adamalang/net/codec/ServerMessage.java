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
package org.adamalang.net.codec;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;
import org.adamalang.common.rate.TokenGrant;
import org.adamalang.runtime.data.LocalDocumentChange;

/** messages from server to client */
public class ServerMessage {
  @TypeId(24322)
  @Flow("Ping")
  public static class PingResponse {
  }


  @TypeId(24324)
  @Flow("Drain")
  public static class DrainResponse {
  }

  @TypeId(24326)
  @Flow("Load")
  public static class LoadResponse {
    @FieldOrder(1)
    public int documents;
    @FieldOrder(2)
    public int connections;
  }

  @TypeId(9001)
  @Flow("Finder")
  public static class FindResponse {
    @FieldOrder(1)
    public long id;
    @FieldOrder(2)
    public int location;
    @FieldOrder(3)
    public String archive;
    @FieldOrder(4)
    public String region;
    @FieldOrder(5)
    public String machine;
    @FieldOrder(6)
    public boolean deleted;
  }

  @TypeId(1018)
  @Flow("Probe")
  public static class ProbeCommandResponse {
    @FieldOrder(1)
    public String json;
    @FieldOrder(2)
    public String[] errors;
  }

  @TypeId(12524)
  @Flow("Creation")
  public static class CreateResponse {
  }

  @TypeId(12526)
  @Flow("Deletion")
  public static class DeleteResponse {
  }

  @TypeId(6736)
  @Flow("Reflection")
  public static class ReflectResponse {
    @FieldOrder(1)
    public String schema;
  }

  @TypeId(8922)
  @Flow("Deployment")
  public static class ScanDeploymentResponse {
  }

  @TypeId(12546)
  @Flow("Document")
  public static class StreamStatus {
    @FieldOrder(1)
    public int code;
  }

  @TypeId(10546)
  @Flow("Document")
  public static class StreamData {
    @FieldOrder(1)
    public String delta;
  }

  @TypeId(19546)
  @Flow("Document")
  public static class StreamError {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public int code;
  }

  @TypeId(15546)
  @Flow("Document")
  public static class StreamAskAttachmentResponse {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public boolean allowed;
  }

  @TypeId(1632)
  @Flow("Document")
  public static class StreamSeqResponse {
    @FieldOrder(1)
    public int op;
    @FieldOrder(2)
    public int seq;
  }

  @TypeId(5122)
  @Flow("Info")
  public static class HeatPayload {
    @FieldOrder(1)
    public double cpu;
    @FieldOrder(2)
    public double mem;
  }

  @TypeId(7232)
  @Flow("Info")
  public static class InventoryHeartbeat {
    @FieldOrder(1)
    public String[] spaces;
  }

  @TypeId(1721)
  @Flow("Web")
  public static class WebResponseNet { // search commonWebHandle in Handler.java AND commonWebReturn in InstanceClient.java
    @FieldOrder(1)
    public String contentType;
    @FieldOrder(2)
    public String body;
    @FieldOrder(3)
    public String assetId;
    @FieldOrder(4)
    public String assetName;
    @FieldOrder(5)
    public long assetSize;
    @FieldOrder(6)
    public String assetMD5;
    @FieldOrder(7)
    public String assetSHA384;
    @FieldOrder(8)
    public boolean cors;
    @FieldOrder(9)
    public int cache_ttl_seconds;
    @FieldOrder(10)
    public String asset_transform;
  }

  @TypeId(2123)
  @Flow("Auth")
  public static class AuthResponse {
    @FieldOrder(1)
    public String agent;
  }

  @TypeId(2125)
  @Flow("Authorization")
  public static class AuthorizationResponse {
    @FieldOrder(1)
    public String agent;
    @FieldOrder(2)
    public String hash;
    @FieldOrder(3)
    public String channel;
    @FieldOrder(4)
    public String success;
  }

  @TypeId(2001)
  @Flow("Query")
  public static class QueryResult {
    @FieldOrder(1)
    public String result;
  }

  @TypeId(1783)
  @Flow("Direct")
  public static class DirectSendResponse {
    @FieldOrder(1)
    public int seq;
  }

  @TypeId(10548)
  @Flow("Replica")
  public static class ReplicaData {
    @FieldOrder(1)
    public boolean reset;
    @FieldOrder(2)
    public String change;
  }

  @TypeId(3045)
  @Flow("RateLimiting")
  public static class RateLimitResult {
    @FieldOrder(1)
    public int tokens;
    @FieldOrder(2)
    public int milliseconds;

    public TokenGrant toTokenGrant() {
      return new TokenGrant(tokens, milliseconds);
    }
  }
}
