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
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.natives.NtPrincipal;

/** a trivial private view is for write only tracking and provides complete experience for developers */
public class TrivialPrivateView extends PrivateView {
  public TrivialPrivateView(int viewId, NtPrincipal who, Perspective perspective) {
    super(viewId, who, perspective);
  }

  @Override
  public void ingest(JsonStreamReader reader) {
    reader.skipValue();
  }

  @Override
  public void dumpViewer(JsonStreamWriter writer) {
    writer.beginObject();
    writer.endObject();
  }

  @Override
  public long memory() {
    return 1024;
  }

  @Override
  public void update(JsonStreamWriter writer) {
  }

  @Override
  public boolean hasRead() {
    return false;
  }
}
