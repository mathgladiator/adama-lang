/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {
  public static String encode(final String... kvp) {
    return encodeObject(kvp).toString();
  }

  public static ObjectNode encodeObject(final String... kvp) {
    final var map = Utility.createObjectNode();
    for (var k = 0; k + 1 < kvp.length; k += 2) {
      if (kvp[k + 1].equals("true") || kvp[k + 1].equals("false")) {
        map.put(kvp[k], Boolean.parseBoolean(kvp[k + 1]));
      } else {
        try {
          final var val = Integer.parseInt(kvp[k + 1]);
          map.put(kvp[k], val);
        } catch (final NumberFormatException nfe) {
          map.put(kvp[k], kvp[k + 1]);
        }
      }
    }
    return map;
  }
}
