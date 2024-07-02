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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.Stream;
import org.adamalang.runtime.data.DocumentRestore;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** command process for documents */
public class CommandProcessor {
  private final TerminalIO terminal;
  private final AdamaMicroVerse verse;

  public CommandProcessor(TerminalIO terminal, AdamaMicroVerse verse) {
    this.terminal = terminal;
    this.verse = verse;
  }

  /** delete a key */
  public void delete(Key key) {
    verse.service.delete(new CoreRequestContext(new NtPrincipal("terminal", "overlord"), "cli", "127.0.0.1", key.key), key, new Callback<Void>() {
      @Override
      public void success(Void value) {
        terminal.info(key.space + "/" + key.key + " deleted");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        terminal.error("failed delete:" + ex.code);
      }
    });
  }

  /** initialize a key directly */
  public void init(Key key, File file) {
    try {
      ObjectNode parsed = Json.parseJsonObject(Files.readString(file.toPath()));
      parsed.put("__seq", 1);
      RemoteDocumentUpdate update = new RemoteDocumentUpdate(0, 1, NtPrincipal.NO_ONE, "{}", parsed.toString(), "{}", true, 0, 0, UpdateType.Internal);
      verse.dataService.initialize(key, update, new Callback<Void>() {
        @Override
        public void success(Void value) {
          terminal.info("init:loaded");
        }

        @Override
        public void failure(ErrorCodeException ex) {
          terminal.error("failed restoring:" + ex.code);
        }
      });
    } catch (Exception ex) {
      terminal.error("failed loading: " + ex.getMessage());
    }
  }

  /** snapshot and save to file */
  public void save(Key key, File file) {
    verse.service.saveCustomerBackup(key, new Callback<String>() {
      @Override
      public void success(String value) {
        try {
          Files.writeString(file.toPath(), value);
          terminal.info("saved!");
        } catch (Exception ex) {
          terminal.error("failed save: " + ex.getMessage());
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        terminal.error("failed save from service:" + ex.code);
      }
    });
  }

  /** restore a key from an existing file */
  public void restore(Key key, File file) {
    try {
      ObjectNode parsed = Json.parseJsonObject(Files.readString(file.toPath()));
      CoreRequestContext context = new CoreRequestContext(new NtPrincipal("terminal", "overlord"), "origin", "ip", key.key);
      DocumentRestore restore = new DocumentRestore(parsed.get("__seq").intValue(), parsed.toString(), NtPrincipal.NO_ONE);
      verse.service.restore(context, key, restore, new Callback<Void>() {
        @Override
        public void success(Void value) {
          terminal.info("restored!");
        }

        @Override
        public void failure(ErrorCodeException ex) {
          terminal.error("failed to restore:" + ex.code);
        }
      });
    } catch (Exception ex) {
      terminal.error("failed restoring: " + ex.getMessage());
    }
  }

  /** dump a log */
  public void log(Key key, File file) throws Exception {
    long started = System.currentTimeMillis();
    FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8);
    try {
      CountDownLatch readEverything = new CountDownLatch(1);
      verse.dataService.dumpLog(key, new Stream<String>() {
        @Override
        public void next(String value) {
          try {
            fw.write(value);
            fw.write("\n");
            fw.flush();
          } catch (Exception ex) {
            terminal.error("write exception:" + ex.getMessage());
          }
        }

        @Override
        public void complete() {
          readEverything.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      readEverything.await(10000, TimeUnit.MILLISECONDS);
      terminal.notice("dump-log finished in " + (System.currentTimeMillis() - started) + "ms");
    } finally {
      fw.flush();
      fw.close();
    }
  }

  /** query information about the key */
  public void query(Key key) {
    TreeMap<String, String> query = new TreeMap<>();
    query.put("space", key.space);
    query.put("key", key.key);
    verse.service.query(query, new Callback<>() {
      @Override
      public void success(String value) {
        terminal.notice("query-result|" + value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        terminal.error("query|failed to query:" + ex.code);
      }
    });
  }
}
