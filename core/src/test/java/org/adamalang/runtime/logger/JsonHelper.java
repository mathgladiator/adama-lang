/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;

public class JsonHelper {
    public static ObjectNode encodeObject(String... kvp) {
        ObjectNode map = Utility.createObjectNode();
        for (int k = 0; k + 1 < kvp.length; k += 2) {
            if (kvp[k + 1].equals("true") || kvp[k + 1].equals("false")) {
                map.put(kvp[k], Boolean.parseBoolean(kvp[k + 1]));
            } else {
                try {
                    int val = Integer.parseInt(kvp[k + 1]);
                    map.put(kvp[k], val);
                } catch (NumberFormatException nfe) {
                    map.put(kvp[k], kvp[k + 1]);
                }
            }
        }
        return map;
    }

    public static String encode(String... kvp) {
        return encodeObject(kvp).toString();
    }
}
