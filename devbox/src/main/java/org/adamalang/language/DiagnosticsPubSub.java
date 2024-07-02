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
package org.adamalang.language;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.adamalang.devbox.DiagnosticsSubscriber;
import org.adamalang.translator.tree.SymbolIndex;

import java.util.ArrayList;
import java.util.HashMap;

/** this allows multiple diagnostic subscribers to bind to the same event stream */
public class DiagnosticsPubSub implements DiagnosticsSubscriber {
  private final HashMap<Integer, DiagnosticsSubscriber> subscribers;
  private int id;
  private ArrayNode lastDiagnostics = null;
  private SymbolIndex lastIndex = null;

  public DiagnosticsPubSub() {
    this.id = 1;
    this.subscribers = new HashMap<>();
    this.lastDiagnostics = null;
    this.lastIndex = null;
  }

  public synchronized Runnable create(DiagnosticsSubscriber subscriber) {
    int boundTo = id++;
    subscribers.put(boundTo, subscriber);
    if (lastDiagnostics != null) {
      subscriber.updated(lastDiagnostics);
    }
    if (lastIndex != null) {
      subscriber.indexed(lastIndex);
    }
    return () -> {
      remove(boundTo);
    };
  }

  private synchronized void remove(int id) {
    subscribers.remove(id);
  }

  @Override
  public synchronized void updated(ArrayNode report) {
    lastDiagnostics = report;
    for (DiagnosticsSubscriber subscriber : new ArrayList<>(subscribers.values())) {
      subscriber.updated(report);
    }
  }

  @Override
  public synchronized void indexed(SymbolIndex index) {
    lastIndex = index;
    for (DiagnosticsSubscriber subscriber : new ArrayList<>(subscribers.values())) {
      subscriber.indexed(index);
    }
  }
}
