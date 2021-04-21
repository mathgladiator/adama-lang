/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty;

import org.adamalang.api.commands.contracts.*;
import org.adamalang.netty.api.GameSpace;
import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.DurableLivingDocument;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DevKitBackbone implements Backbone {
  private final GameSpaceDB gameSpaceDB;
  private final TimeSource time;
  private final ScheduledExecutorService executor;

  public DevKitBackbone(GameSpaceDB gameSpaceDB, ScheduledExecutorService executor) {
    this.gameSpaceDB = gameSpaceDB;
    this.time = gameSpaceDB.time;
    this.executor = executor;
  }

  @Override
  public TimeSource getTimeSource() {
    return time;
  }

  @Override
  public DocumentMonitor monitorFor(String space, long key) {
    return null;
  }

  @Override
  public void findDataService(String space, CommandRequiresDataService cmd, CommandResponder responder) {
    try {
      GameSpace gs = gameSpaceDB.getOrCreate(space);
      cmd.onDataServiceFound(gs.service);
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }

  @Override
  public void findDocument(String space, long key, CommandRequiresDocument cmd, CommandResponder responder) {
    try {
      GameSpace gs = gameSpaceDB.getOrCreate(space);
      gs.get(key, Callback.bind(executor, 0, new Callback<DurableLivingDocument>() {
        @Override
        public void success(DurableLivingDocument value) {
          cmd.onDurableDocumentFound(value);
          witness(value, executor);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      }));
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }

  @Override
  public void findLivingDocumentFactory(String space, long key, CommandRequiresLivingDocumentFactory cmd, CommandResponder responder) {
    try {
      GameSpace gs = gameSpaceDB.getOrCreate(space);
      cmd.onLivingDocumentFactory(gs.getFactory());
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }

  public static CommandResponder wrap(JsonResponder responder) {
    return new CommandResponder() {
      @Override
      public void stream(String json) {
        responder.respond(json, false, null);
      }

      @Override
      public void finish(String response) {
        responder.respond(response, true, null);
      }

      @Override
      public void error(ErrorCodeException ex) {
        responder.failure(ex);
      }
    };
  }

  public void invalidateAndSchedule(DurableLivingDocument document) {
    witness(document, executor);
  }

  private void witness(DurableLivingDocument document, ScheduledExecutorService executor) {
    Integer ms = document.getAndCleanRequiresInvalidateMilliseconds();
    if (ms != null) {
      executor.schedule(() -> {
        document.invalidate(Callback.DONT_CARE_INTEGER);
      }, ms, TimeUnit.MILLISECONDS);
    }
  }
}
