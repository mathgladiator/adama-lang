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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Test;

public class NtMessageBaseTests {
  @Test
  public void coverage() {
    NtMessageBase.NULL.__writeOut(new JsonStreamWriter());
    NtMessageBase.NULL.to_dynamic();
    NtMessageBase.NULL.ingest_dynamic(new NtDynamic("{}"));
    NtMessageBase.NULL.__hash(null);
    NtMessageBase.NULL.__ingest(new JsonStreamReader("{}"));
    NtMessageBase.NULL.__getIndexValues();
    NtMessageBase.NULL.__getIndexColumns();
  }
}
