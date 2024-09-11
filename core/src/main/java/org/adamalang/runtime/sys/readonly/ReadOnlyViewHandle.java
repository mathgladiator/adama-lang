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

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.StreamHandle;

/** a way to control the stream after the fact */
public class ReadOnlyViewHandle {
  private final NtPrincipal who;
  ReadOnlyLivingDocument document;
  private final StreamHandle handle;
  private final SimpleExecutor executor;

  public ReadOnlyViewHandle(NtPrincipal who, ReadOnlyLivingDocument document, StreamHandle handle, SimpleExecutor executor) {
    this.who = who;
    this.document = document;
    this.handle = handle;
    this.executor = executor;
  }

  public void update(String update) {
    executor.execute(new NamedRunnable("update-ro-view") {
      @Override
      public void execute() throws Exception {
        handle.ingestViewUpdate(new JsonStreamReader(update));
        document.forceUpdate(who);
      }
    });
  }

  public void close() {
    executor.execute(new NamedRunnable("update-ro-view") {
      @Override
      public void execute() throws Exception {
        handle.kill();
        document.garbageCollectViewsFor(who);
        handle.disconnect();
      }
    });
  }
}
