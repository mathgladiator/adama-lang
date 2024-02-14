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
package org.adamalang.runtime.natives.algo;

import org.adamalang.common.csv.LineWriter;
import org.adamalang.runtime.natives.*;

public class MessageCSVWriter extends LineWriter {

  public void write(NtTime t) {
    write(t.toString());
  }

  public void write(NtDateTime t) {
    write(t.toString());
  }

  public void write(NtDate t) {
    write(t.toString());
  }

  public void write(NtTimeSpan t) {
    write(t.seconds);
  }

  public void write(NtPrincipal p) {
    write(p.agent + "@" + p.authority);
  }

  public void write(NtComplex c) {
    write(c.toString());
  }
}
