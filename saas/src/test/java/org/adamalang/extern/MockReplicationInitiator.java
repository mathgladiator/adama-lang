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
package org.adamalang.extern;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.readonly.ReplicationInitiator;

public class MockReplicationInitiator implements ReplicationInitiator {
  @Override
  public void startDocumentReplication(Key key, DataObserver observer, Callback<Runnable> cancel) {
    observer.failure(new ErrorCodeException(1000));
    cancel.failure(new ErrorCodeException(-1));
  }
}
