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
package org.adamalang.runtime.remote.replication;

import org.adamalang.runtime.contracts.Caller;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.Service;

public class MockCallerForReplication implements Caller  {
  public final MockReplicationService service;

  public MockCallerForReplication() {
    this.service = new MockReplicationService();
  }

  @Override
  public Deliverer __getDeliverer() {
    return null;
  }

  @Override
  public String __getKey() {
    return "key";
  }

  @Override
  public String __getSpace() {
    return "space";
  }

  @Override
  public Service __findService(String name) {
    if ("service".equals(name)) {
      return service;
    }
    if ("fail".equals(name)) {
      return Service.FAILURE;
    }
    return null;
  }
}
