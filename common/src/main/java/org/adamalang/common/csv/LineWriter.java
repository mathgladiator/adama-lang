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
package org.adamalang.common.csv;

import java.util.PrimitiveIterator;

/** write a valid CSV (RFC 4180) line/record */
public class LineWriter {
  private StringBuilder sb = new StringBuilder();
  private boolean notFirst;

  public LineWriter() {
    this.notFirst = false;
  }

  private void writeCommaIfNotFirst() {
    if (notFirst) {
      sb.append(",");
    }
    notFirst = true;
  }

  public void write(boolean b) {
    write(b ? "true" : "false");
  }

  public void write(int x) {
    writeCommaIfNotFirst();
    sb.append(x);
  }

  public void write(double x) {
    writeCommaIfNotFirst();
    sb.append(x);
  }

  public void write(long x) {
    writeCommaIfNotFirst();
    sb.append(x);
  }

  public void write(String s) {
    writeCommaIfNotFirst();
    if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
      sb.append("\"");
      PrimitiveIterator.OfInt it = s.codePoints().iterator();
      while (it.hasNext()) {
        int cp = it.nextInt();
        switch (cp) {
          case '"':
            sb.append("\"");
          default:
            sb.append(Character.toString(cp));
        }
      }
      sb.append("\"");
    } else {
      sb.append(s);
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
