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
package org.adamalang.train.sm;

import org.adamalang.train.message.Messages;

import java.util.TreeMap;

/** This represents a clean slate state machine for a Raft style state machine */
public class LogStateMachine {
  private long currentTerm;
  private int votedFor;
  // Log[]

  private long commitIndex;
  private long lastApplied;

  // Leadership state
  private final TreeMap<Integer, Long> nextIndex;
  private final TreeMap<Integer, Long> matchIndex;

  public LogStateMachine() {
    this.nextIndex = new TreeMap<>();
    this.matchIndex = new TreeMap<>();
  }

  public Messages.AppendEntriesResponse handle(Messages.AppendEntriesRequest append) {
    Messages.AppendEntriesResponse response = new Messages.AppendEntriesResponse();
    if (append.term < currentTerm) {
      response.accepted = false;
      return response;
    }

    // TODO: if log doesn't contain an entry at prevLogIndex whose term matches PrevLogTerm --> return false

    // TODO: if an existing entry (in log) conflicts with append, then delete the existing entry and it's tail (Replace)

    // TODO merge
    // append.entries


    if (append.commitIndex > commitIndex) {
      int indexOfLastEntry = -100; // from append.entries
      commitIndex = Math.min(append.commitIndex, indexOfLastEntry);
    }
    return response;
  }

  public Messages.VoteResponse handle(Messages.VoteRequest vote) {
    Messages.VoteResponse response = new Messages.VoteResponse();

    if (vote.term < currentTerm) {
      response.voteGranted = false;
      return response;
    }
    boolean canVote = votedFor == 0 || votedFor == vote.candidateId;
    if (canVote && vote.lastLogIndex >= commitIndex) {
      // response.term = ?
      response.voteGranted = true;
      return response;
    }

    return response;
  }
}
