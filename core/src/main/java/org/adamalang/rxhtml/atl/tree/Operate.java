/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.atl.Context;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;

import java.util.Map;
import java.util.TreeMap;

/** a simple way of doing string equality */
public class Operate implements Tree {
  public static final String[] OPERATORS = new String[] { "<=", ">=", "!=", "<", ">", "=" };

  public static String convertOp(String op) {
    if ("=".equals(op)) {
      return "==";
    }
    return op;
  }

  public final Tree tree;
  public final Tree value;
  public final String operator;

  public Operate(Tree tree, String value, String operator) throws ParseException  {
    this.tree = tree;
    this.value = Parser.parse(value);
    this.operator = operator;
  }

  @Override
  public Map<String, String> variables() {
    TreeMap<String, String> union = new TreeMap<>();
    union.putAll(tree.variables());
    union.putAll(value.variables());
    return union;
  }

  @Override
  public String debug() {
    return "OP(" + operator + ")[" + tree.debug() + ",'" + value.debug() + "']";
  }

  @Override
  public String js(Context context, String env) {
    return "(" + tree.js(Context.DEFAULT, env) + operator + value.js(Context.DEFAULT, env) + ")";
  }
}
