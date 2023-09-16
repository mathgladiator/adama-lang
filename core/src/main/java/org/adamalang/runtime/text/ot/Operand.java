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
package org.adamalang.runtime.text.ot;

import org.adamalang.runtime.json.JsonStreamReader;

/** An operand is a string that may have an inlight operation. We do this so operations can merge with minimal string manipulation until a final assembly. This allows many operations to build up */
public interface Operand {
  /** the core algorithm of applying a OT (json encoded changes) to an operand returning another operand */
  static Operand apply(Operand start, String changes) {
    Operand result = start;
    JsonStreamReader reader = new JsonStreamReader(changes);
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        if ("changes".equals(reader.fieldName())) {
          if (reader.startArray()) {
            Join next = new Join();
            int at = 0;
            while (reader.notEndOfArray()) {
              if (reader.startArray()) {
                at += reader.readInteger();
                boolean notFirst = false;
                while (reader.notEndOfArray()) {
                  if (notFirst) {
                    next.children.add(new Raw("\n"));
                  }
                  next.children.add(new Raw(reader.readString()));
                  notFirst = true;
                }
              } else {
                int copy = reader.readInteger();
                result.transposeRangeIntoJoin(at, copy, next);
                at += copy;
              }
            }
            result = next;
          }
        } else {
          reader.skipValue();
        }
      }
    } else {
      reader.skipValue();
    }
    return result;
  }

  /** transpose range through this operation into another join */
  void transposeRangeIntoJoin(int at, int length, Join join);

  /** get the string value */
  String get();

  /** how long is the string */
  int length();
}
