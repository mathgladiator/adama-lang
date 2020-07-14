/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

/** responsible for parts of the document which are common */
public class CodeGenDocument {
  public static void writePrelude(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.state.options.packageName != null) {
      sb.append("package ").append(environment.state.options.packageName).append(";").writeNewline().writeNewline();
    }
    sb.append("import com.fasterxml.jackson.databind.JsonNode;").writeNewline();
    sb.append("import com.fasterxml.jackson.databind.node.ArrayNode;").writeNewline();
    sb.append("import com.fasterxml.jackson.databind.node.ObjectNode;").writeNewline();
    sb.append("import org.adamalang.runtime.*;").writeNewline();
    sb.append("import org.adamalang.runtime.async.*;").writeNewline();
    sb.append("import org.adamalang.runtime.bridges.*;").writeNewline();
    sb.append("import org.adamalang.runtime.contracts.*;").writeNewline();
    sb.append("import org.adamalang.runtime.exceptions.*;").writeNewline();
    sb.append("import org.adamalang.runtime.index.*;").writeNewline();
    sb.append("import org.adamalang.runtime.natives.*;").writeNewline();
    sb.append("import org.adamalang.runtime.natives.lists.*;").writeNewline();
    sb.append("import org.adamalang.runtime.ops.*;").writeNewline();
    sb.append("import org.adamalang.runtime.reactives.*;").writeNewline();
    sb.append("import org.adamalang.runtime.stdlib.*;").writeNewline();
    sb.append("import java.util.function.Consumer;").writeNewline();
    sb.append("import java.util.function.Function;").writeNewline();
    sb.append("import java.util.ArrayList;").writeNewline();
    sb.append("import java.util.Comparator;").writeNewline();
    for (final String imp : environment.state.globals.imports()) {
      if (imp.startsWith("org.adamalang.runtime.stdlib")) {
        continue;
      }
      sb.append("import ").append(imp).append(";").writeNewline();
    }
  }
}
