/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

public class PhaseTemplate {
    public static String inventEmpty() {
        final var templateBuilder = new StringBuilder();
        templateBuilder.append("\n");
        templateBuilder.append("@construct {\n");
        templateBuilder.append("}\n");
        templateBuilder.append("\n");
        templateBuilder.append("test PrimaryTest {\n");
        templateBuilder.append("  assert false;\n");
        templateBuilder.append("}\n");
        return templateBuilder.toString();
    }
}
