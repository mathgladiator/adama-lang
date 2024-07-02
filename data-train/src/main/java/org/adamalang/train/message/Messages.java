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
package org.adamalang.train.message;

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;

public class Messages {
  @TypeId(500)
  @Flow("Data")
  public static class Entry {
    @FieldOrder(1)
    public String space;
    @FieldOrder(2)
    public String key;
    @FieldOrder(3)
    public int seqBegin;
    @FieldOrder(4)
    public int seqEnd;
  }

  @TypeId(1000)
  @Flow("Client")
  public static class AppendEntriesRequest {
    @FieldOrder(1)
    public int logId;
    @FieldOrder(2)
    public long term;
    @FieldOrder(3)
    public int leaderId;
    @FieldOrder(4)
    public long prevLogTerm;
    @FieldOrder(5)
    public Entry[] entries;
    @FieldOrder(6)
    public long commitIndex;
  }

  @TypeId(1001)
  @Flow("Server")
  public static class AppendEntriesResponse {
    @FieldOrder(1)
    public long term;
    @FieldOrder(2)
    public boolean accepted;
  }

  @TypeId(2000)
  @Flow("Client")
  public static class VoteRequest {
    @FieldOrder(1)
    public int logId;
    @FieldOrder(2)
    public long term;
    @FieldOrder(3)
    public int candidateId;
    @FieldOrder(4)
    public int lastLogIndex;
    @FieldOrder(5)
    public long lastLogTerm;
  }

  @TypeId(2001)
  @Flow("Server")
  public static class VoteResponse {
    @FieldOrder(1)
    public long term;
    @FieldOrder(2)
    public boolean voteGranted;
  }

  @TypeId(3001)
  @Flow("Client")
  public static class LogGossipStart {
    @FieldOrder(1)
    public int logId;
  }
}
