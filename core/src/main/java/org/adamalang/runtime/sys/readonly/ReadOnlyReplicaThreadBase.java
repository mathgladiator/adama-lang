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
package org.adamalang.runtime.sys.readonly;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.sys.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.HashMap;
import java.util.function.Consumer;

/** we create a readonly version of the entire system for simplicity sake */
public class ReadOnlyReplicaThreadBase {
  public final int threadId;
  public final ServiceShield shield;
  public final CoreMetrics metrics;
  public final SimpleExecutor executor;
  public final HashMap<Key, ReadOnlyLivingDocument> map;
  private final HashMap<String, PredictiveInventory> inventoryBySpace;
  private final LivingDocumentFactoryFactory livingDocumentFactoryFactory;
  private final ReplicationInitiator initiator;

  public ReadOnlyReplicaThreadBase(int threadId, ServiceShield shield, CoreMetrics metrics, LivingDocumentFactoryFactory livingDocumentFactoryFactory, ReplicationInitiator initiator, SimpleExecutor executor) {
    this.threadId = threadId;
    this.shield = shield;
    this.metrics = metrics;
    this.executor = executor;
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    this.initiator = initiator;
    this.map = new HashMap<>();
    this.inventoryBySpace = new HashMap<>();
  }

  private void killFromWithinExecutor(Key key) {
    ReadOnlyLivingDocument document = map.remove(key);
    document.kill();
  }

  private void initiateReplication(Key key, ReadOnlyLivingDocument document) {
    initiator.startDocumentReplication(key, new DataObserver() {
      @Override
      public String machine() {
        // the higher level proxy will inject the machine name
        return null;
      }

      @Override
      public void start(String snapshot) {
        executor.execute(new NamedRunnable("replication-start") {
          @Override
          public void execute() throws Exception {
            document.start(snapshot);
          }
        });
      }

      @Override
      public void change(String delta) {
        executor.execute(new NamedRunnable("replication-start") {
          @Override
          public void execute() throws Exception {
            document.change(delta);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException exception) {
        executor.execute(new NamedRunnable("replication-start") {
          @Override
          public void execute() throws Exception {
            killFromWithinExecutor(key);
          }
        });
      }
    }, new Callback<Runnable>() {
      @Override
      public void success(Runnable cancel) {
        executor.execute(new NamedRunnable("replication-start") {
          @Override
          public void execute() throws Exception {
            document.setCancel(cancel);
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        executor.execute(new NamedRunnable("replication-start") {
          @Override
          public void execute() throws Exception {
            killFromWithinExecutor(key);
          }
        });
      }
    });
  }

  private void bindClone(Key key, LivingDocument clone, LivingDocumentFactory factory) {
    clone.__lateBind(key.space, key.key, (agent, key1, id, result, firstParty, callback) -> executor.execute(new NamedRunnable("bounce-to-readonly") {
      @Override
      public void execute() throws Exception {
        try {
          clone.__forceDeliverResult(id, result);
          clone.__forceBroadcastToKeepReadonlyObserverUpToDate();
          callback.success(0);
        } catch (ErrorCodeException ex) {
          callback.failure(ex);
        }
      }
    }), factory.registry);
  }

  private void constructDocument(Key key, Consumer<ReadOnlyLivingDocument> success, ReadOnlyStream stream) {
    livingDocumentFactoryFactory.fetch(key, new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory factory) {
        executor.execute(new NamedRunnable("post-create-document") {
          @Override
          public void execute() throws Exception {
            ReadOnlyLivingDocument document = map.get(key);
            if (document == null) {
              LivingDocument clone = factory.create(null);
              bindClone(key, clone, factory);

              // then we wrap it stash it
              document = new ReadOnlyLivingDocument(clone);
              map.put(key, document);
              initiateReplication(key, document);
            }
            success.accept(document);
          };
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        stream.failure(ex);
      }
    });
  }

  public void observe(CoreRequestContext context, Key key, ReadOnlyStream stream) {
    Consumer<ReadOnlyLivingDocument> onDocument = (document) -> {
      StreamHandle handle = document.join(context.who, new Perspective() {
        @Override
        public void data(String data) {
          stream.next(data);
        }

        @Override
        public void disconnect() {
          stream.close();
        }
      });
      stream.setupComplete(new ReadOnlyViewHandle(handle, executor));
    };
    executor.execute(new NamedRunnable("find-document") {
      @Override
      public void execute() throws Exception {
        ReadOnlyLivingDocument document = map.get(key);
        if (document != null) {
          onDocument.accept(document);
        } else {
          constructDocument(key, onDocument, stream);
        }
      }
    });
  }

  public PredictiveInventory getOrCreateInventory(String space) {
    PredictiveInventory inventory = inventoryBySpace.get(space);
    if (inventory == null) {
      inventory = new PredictiveInventory();
      inventoryBySpace.put(space, inventory);
    }
    return inventory;
  }
}
