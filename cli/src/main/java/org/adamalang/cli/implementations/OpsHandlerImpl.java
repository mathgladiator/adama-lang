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
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.caravan.events.RestoreLoader;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.OpsHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.runtime.contracts.AutoMorphicAccumulator;
import org.adamalang.runtime.json.JsonAlgebra;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;

public class OpsHandlerImpl implements OpsHandler {

  private static long jsonStateMachine(RandomAccessFile store, long start, long max) throws IOException  {
    int bracketCount = 0;
    boolean inString = false;
    for (long at = start; at < max; at++) {
      store.seek(at);
      byte ch = (byte) store.read();
      if (inString) {
        switch (ch) {
          case '\\':
            at++;
            break;
          case '"':
            inString = false;
        }
      } else {
        switch (ch) {
          case '{':
            bracketCount++;
            break;
          case '}':
            bracketCount--;
            if (bracketCount == 0) {
              return at + 1;
            }
            break;
          case '"':
            inString = true;
        }
      }
    }
    return -1;
  }

  private static void scan(File file, Function<ObjectNode, Boolean> criteria, String output) throws Exception {
    RandomAccessFile store = new RandomAccessFile(file, "r");
    try {
      long size = file.length();
      System.out.println("Scanning Size:" + size);
      for (long at = 0; at < size - 64; at++) {
        store.seek(at);
        if (store.read() == '{') { // HRMM
          long end = jsonStateMachine(store, at, size);
          try {
            if (at < end && end < size) {
              byte[] found = new byte[(int) (end - at)];
              store.seek(at);
              store.readFully(found);
              String str = new String(found, StandardCharsets.UTF_8);
              ObjectNode recovered = Json.parseJsonObject(str);
              if (recovered.has("__seq")) {
                if (criteria.apply(recovered)) {
                  System.out.println("Found:" + recovered.size() + " bytes at " + at);
                  Files.writeString(new File(output + "." + at + ".json").toPath(), recovered.toPrettyString());
                }
              }
            }
          } catch (Exception failedToParse) {
          }
        }
      }
    } finally {
      store.close();
    }
  }

  @Override
  public void forensics(Arguments.OpsForensicsArgs args, Output.YesOrError output) throws Exception {
    scan(new File(args.input), (node) -> true, args.output);
    output.out();
  }

  @Override
  public void compact(Arguments.OpsCompactArgs args, Output.YesOrError output) throws Exception {
    ArrayList<byte[]> writes = RestoreLoader.load(new File(args.input));

    ArrayList<String> toMerge = new ArrayList<>();
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), new EventCodec.HandlerEvent() {
        @Override
        public void handle(Events.Snapshot snapshot) {
          toMerge.clear();
          toMerge.add(snapshot.document);
        }

        @Override
        public void handle(Events.Batch batch) {
          for (Events.Change change : batch.changes) {
            handle(change);
          }

        }

        @Override
        public void handle(Events.Change change) {
          toMerge.add(change.redo);
        }
      });
    }

    AutoMorphicAccumulator<String> merge = JsonAlgebra.mergeAccumulator();
    for (String delta : toMerge) {
      merge.next(delta);
    }
    File writeTo = new File(args.output);
    Files.writeString(writeTo.toPath(), merge.finish());
  }

  @Override
  public void explain(Arguments.OpsExplainArgs args, Output.YesOrError output) throws Exception {
    ArrayList<byte[]> writes = RestoreLoader.load(new File(args.input));
    String[] query = args.jquery.split(Pattern.quote("."));
    for (byte[] write : writes) {
      EventCodec.route(Unpooled.wrappedBuffer(write), new EventCodec.HandlerEvent() {

        private String test(String json) {
          JsonNode parsed = Json.parseJsonObject(json);
          for (int k = 0; k < query.length; k++) {
            if (parsed.has(query[k])) {
              JsonNode test = parsed.get(query[k]);
              if (test.isObject() || k == query.length - 1) {
                parsed = test;
              } else {
                return null;
              }
            } else {
              return null;
            }
          }
          return parsed.toString();
        }

        @Override
        public void handle(Events.Snapshot snapshot) {
          String result = test(snapshot.document);
          if (result != null) {
            System.out.println("SNAPSHOT => " + result);
          }
        }

        @Override
        public void handle(Events.Batch batch) {
          for (Events.Change change : batch.changes) {
            handle(change);
          }
        }

        @Override
        public void handle(Events.Change change) {
          String result = test(change.redo);
          if (result != null) {
            System.out.println(change.request + " => " + result);
          }
        }
      });
    }
    output.out();
  }
}
