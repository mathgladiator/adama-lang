/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.linq.OrderBy;
import org.adamalang.translator.tree.expressions.linq.OrderDyn;
import org.adamalang.translator.tree.expressions.linq.OrderPair;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsOrderable;

import java.util.ArrayList;

public class CodeGenDynCompare {
  public static void writeDynCompare(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final String className) {
    sb.append("class DynCmp_").append(className).append(" implements Comparator<").append(className).append("> {").tabUp().writeNewline();
    sb.append("private final CompareField[] parsed;").writeNewline();
    sb.append("DynCmp_").append(className).append("(String instructions) {").tabUp().writeNewline();
    sb.append("this.parsed = DynCompareParser.parse(instructions);").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public int compare(").append(className).append(" __a, ").append(className).append(" __b) {").tabUp().writeNewline();

    sb.append("for (CompareField field : parsed) {").tabUp().writeNewline();
    sb.append("int delta = 0;").writeNewline();
    sb.append("switch (field.name) {").tabUp().writeNewline();
    ArrayList<FieldDefinition> orderableFields = new ArrayList<>();
    for (FieldDefinition fd : storage.fieldsByOrder) {
      var fieldType = OrderBy.getOrderableType(fd, environment);
      if ((fieldType instanceof IsOrderable)) {
        orderableFields.add(fd);
      }
    }
    int count = orderableFields.size();
    for (FieldDefinition fd : orderableFields) {
      sb.append("case \"").append(fd.name).append("\":").tabUp().writeNewline();
      sb.append("delta = ").append(OrderBy.getCompareLine(fd, environment, new OrderPair(null, Token.WRAP(fd.name), null))).append(";").writeNewline();
      sb.append("break;").tabDown();
      count--;
      if (count == 0) {
        sb.tabDown();
      }
      sb.writeNewline();
    }
    sb.append("}").writeNewline();
    sb.append("if (delta != 0) {").tabUp().writeNewline();
    sb.append("return field.desc ? -delta : delta;").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("return 0;").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
