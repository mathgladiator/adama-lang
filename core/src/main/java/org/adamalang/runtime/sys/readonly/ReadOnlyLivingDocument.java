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

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.StreamHandle;

/** a version of the document that is read only */
public class ReadOnlyLivingDocument {
  private final LivingDocument document;

  public ReadOnlyLivingDocument(LivingDocument document) {
    this.document = document;
  }

  public StreamHandle join(NtPrincipal who, Perspective perspective) {
    PrivateView view = document.__createPrivateView(who, perspective);
    StreamHandle handle = new StreamHandle(view);
    return handle;
  }

    /*
  public void spawnLocalReadOnly(SimpleExecutor executor, Callback<ReadOnlyLivingDocument> callback) throws ErrorCodeException {
    LivingDocument clone = currentFactory.create(document.__monitor);
    clone.__lateBind(key.space, key.key, new Deliverer() {
      @Override
      public void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
        executor.execute(new NamedRunnable("bounce-to-readonly") {
          @Override
          public void execute() throws Exception {
            try {
              clone.__forceDeliverResult(id, result);
              clone.__forceBroadcastToKeepReadonlyObserversUpToDate();
              callback.success(0);
            } catch (ErrorCodeException ex) {
              callback.failure(ex);
            }
          }
        });
      }
    }, currentFactory.registry);

    watch(new DataObserver() {
      @Override
      public void start(String snapshot) {
        executor.execute(new NamedRunnable("bounce-to-readonly") {
          @Override
          public void execute() throws Exception {
            clone.__insert(new JsonStreamReader(snapshot));
            clone.__forceBroadcastToKeepReadonlyObserversUpToDate();
          }
        });
      }

      @Override
      public void change(String delta) {
        executor.execute(new NamedRunnable("bounce-to-readonly") {
          @Override
          public void execute() throws Exception {
            clone.__patch(new JsonStreamReader(delta));
            clone.__forceBroadcastToKeepReadonlyObserversUpToDate();
          }
        });

      }

      @Override
      public void failure(ErrorCodeException exception) {
        executor.execute(new NamedRunnable("bounce-to-readonly") {
          @Override
          public void execute() throws Exception {
            clone.__nukeViews();
          }
        });
      }
    });
  }
  */
}
