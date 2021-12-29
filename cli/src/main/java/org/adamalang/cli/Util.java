/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Locale;

public class Util {
    public static String normalize(String x) {
        return x.trim().toLowerCase(Locale.ROOT);
    }

    public static String[] tail(String[] y) {
        String[] x = new String[y.length - 1];
        for (int k = 1; k < y.length; k++) {
            x[k-1] = y[k];
        }
        return x;
    }

    public static enum ANSI {
        Black("\u001b[30m"),
        Red("\u001b[31m"),
        Green("\u001b[32m"),
        Yellow("\u001b[33m"),
        Blue("\u001b[34m"),
        Magenta("\u001b[35m"),
        Cyan("\u001b[36m"),
        White("\u001b[37m"),
        Reset("\u001b[0m");
        public final String ansi;
        private ANSI(String ansi) {
            this.ansi = ansi;
        }
    }

    public static String prefix(String x, ANSI c) {
        return c.ansi + x + ANSI.Reset.ansi;
    }

    public static final JsonMapper MAPPER = new JsonMapper();

    public static ObjectNode newJsonObject() {
        return MAPPER.createObjectNode();
    }

    public static ObjectNode parseJsonObject(final String json) {
        try {
            return parseJsonObjectThrows(json);
        } catch (final Exception jpe) {
            throw new RuntimeException(jpe);
        }
    }

    public static ObjectNode parseJsonObjectThrows(final String json) throws Exception {
        final var node = MAPPER.readTree(json);
        if (node instanceof ObjectNode) { return (ObjectNode) node; }
        throw new Exception("given json is not an ObjectNode at root");
    }
}
