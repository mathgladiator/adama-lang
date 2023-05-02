/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
        switch (reader.fieldName()) {
          case "changes": {
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
          }
          break;
          default:
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
