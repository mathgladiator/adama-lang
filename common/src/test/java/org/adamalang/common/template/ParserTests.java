/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.template.tree.T;
import org.junit.Assert;
import org.junit.Test;

public class ParserTests {

  @Test
  public void constant() {
    Assert.assertEquals("Hello World", eval(Parser.parse("Hello World")));
  }

  public String eval(T template, String... args) {
    Settings settings = new Settings();
    ObjectNode node = new ObjectMapper().createObjectNode();
    for (int k = 0; k + 1 < args.length; k += 2) {
      String value = args[k + 1];
      if ("true".equals(value)) {
        node.put(args[k], true);
      } else if ("false".equals(value)) {
        node.put(args[k], false);
      } else {
        node.put(args[k], value);
      }
    }
    StringBuilder sb = new StringBuilder();
    template.render(settings, node, sb);
    return sb.toString();
  }

  @Test
  public void single_var() {
    Assert.assertEquals("Hi", eval(Parser.parse("[[msg]]"), "msg", "Hi"));
  }

  @Test
  public void compound_constant_var_constant() {
    Assert.assertEquals("Oh Hi There", eval(Parser.parse("Oh [[msg]] There"), "msg", "Hi"));
  }

  @Test
  public void simple_if_true() {
    Assert.assertEquals("You! Oh Hi There", eval(Parser.parse("You! Oh [[#guard]][[msg]][[/guard]] There"), "msg", "Hi", "guard", "true"));
  }

  @Test
  public void simple_if_false() {
    Assert.assertEquals("You! Oh  There", eval(Parser.parse("You! Oh [[#guard]][[msg]][[/guard]] There"), "msg", "Hi", "guard", "false"));
  }

  @Test
  public void simple_ifnot_true() {
    Assert.assertEquals("You! Oh  There", eval(Parser.parse("You! Oh [[^guard]][[msg]][[/guard]] There"), "msg", "Hi", "guard", "true"));
  }

  @Test
  public void simple_ifnot_false() {
    Assert.assertEquals("You! Oh Hi There", eval(Parser.parse("You! Oh [[^guard]][[msg]][[/guard]] There"), "msg", "Hi", "guard", "false"));
  }

  @Test
  public void nested_if() {
    Assert.assertEquals("{{ACD}}", eval(Parser.parse("{{[[#g0]]A[[#g1]]B[[/g1]][[^g1]]C[[^g1]]D[[/g1]][[/g1]][[/g0]]}}"), "msg", "Hi", "g0", "true", "g1", "false"));
  }

  @Test
  public void html_escaped() {
    Assert.assertEquals("&lt;b&gt;Bold&lt;/b&gt;", eval(Parser.parse("[[html]]"), "html", "<b>Bold</b>"));
  }

  @Test
  public void html_unescaped() {
    Assert.assertEquals("<b>Bold</b>", eval(Parser.parse("[[html|unescape]]"), "html", "<b>Bold</b>"));
  }
}
