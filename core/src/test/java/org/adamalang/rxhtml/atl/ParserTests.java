/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.atl.tree.Text;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class ParserTests {
  @Test
  public void fail() {
    String[] strs = new String[] {
        "{xyz",
        "[xyz",
        "[[",
        "[}",
        "{]",
        "]",
        "|",
        "xyz|",
        "}",
        "{{",
    };
    for (String str: strs) {
      try {
        Parser.parse(str);
        Assert.fail();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }


  @Test
  public void simple() {
    Tree tree = Parser.parse("xyz");
    Assert.assertTrue(tree instanceof Text);
    Assert.assertEquals(((Text) tree).text, "xyz");
    Assert.assertEquals("TEXT(xyz)", tree.debug());
    Assert.assertEquals("\"xyz\"", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(0, vars.size());
  }

  @Test
  public void href_regression() {
    Tree tree = Parser.parse("/#project/{view:space}/manage");
    Assert.assertEquals("[TEXT(/#project/),LOOKUP[space],TEXT(/manage)]", tree.debug());
    Assert.assertEquals("\"/#project/\" + $X['space'] + \"/manage\"", tree.js(Context.DEFAULT, "$X"));
  }

  @Test
  public void variable() {
    Tree tree = Parser.parse("hi {first|trim} {last}");
    Assert.assertEquals("[TEXT(hi ),TRANSFORM(LOOKUP[first],trim),TEXT( ),LOOKUP[last]]", tree.debug());
    Assert.assertEquals("\"hi \" + (function(x) { return ('' + x).trim(); })($X['first']) + \" \" + $X['last']", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(2, vars.size());
    Assert.assertTrue(vars.containsKey("first"));
    Assert.assertTrue(vars.containsKey("last"));
  }

  @Test
  public void normal_white_space() {
    Tree tree = Parser.parse("BLAH{nope}      many    {more}     ");
    Assert.assertEquals("[TEXT(BLAH),LOOKUP[nope],TEXT(      many    ),LOOKUP[more],TEXT(     )]", tree.debug());
    Assert.assertEquals("\"BLAH\" + $X['nope'] + \"      many    \" + $X['more'] + \"     \"", tree.js(Context.DEFAULT, "$X"));
  }

  @Test
  public void normalize_css() {
    Tree tree = Parser.parse("BLAH{nope}      many    {more}     ");
    Assert.assertEquals("[TEXT(BLAH),LOOKUP[nope],TEXT(      many    ),LOOKUP[more],TEXT(     )]", tree.debug());
    Context class_context = Context.makeClassContext();
    Assert.assertEquals("\" BLAH \" + $X['nope'] + \" many \" + $X['more']", tree.js(class_context, "$X"));
  }

  @Test
  public void condition_trailing() {
    Tree tree = Parser.parse("hi [b]active");
    Assert.assertEquals("[TEXT(hi ),(LOOKUP[b]) ? (TEXT(active)) : (EMPTY)]", tree.debug());
    Assert.assertEquals("\"hi \" + (($X['b']) ? (\"active\") : (\"\"))", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(1, vars.size());
    Assert.assertTrue(vars.containsKey("b"));
  }

  @Test
  public void condition_trailing_negate() {
    Tree tree = Parser.parse("hi [!b]inactive");
    Assert.assertEquals("[TEXT(hi ),(!(LOOKUP[b])) ? (TEXT(inactive)) : (EMPTY)]", tree.debug());
    Assert.assertEquals("\"hi \" + ((!($X['b'])) ? (\"inactive\") : (\"\"))", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(1, vars.size());
    Assert.assertTrue(vars.containsKey("b"));
  }

  @Test
  public void condition() {
    Tree tree = Parser.parse("hi [b]A[#b]B[/b] there");
    Assert.assertEquals("[TEXT(hi ),(LOOKUP[b]) ? (TEXT(A)) : (TEXT(B)),TEXT( there)]", tree.debug());
    Assert.assertEquals("\"hi \" + (($X['b']) ? (\"A\") : (\"B\")) + \" there\"", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(1, vars.size());
    Assert.assertTrue(vars.containsKey("b"));
  }

  @Test
  public void condition_eq() {
    Tree tree = Parser.parse("hi [b=xyz]A[#b=xyz]B[/b=xyz] there");
    Assert.assertEquals("[TEXT(hi ),(EQUALS[LOOKUP[b],'xyz']) ? (TEXT(A)) : (TEXT(B)),TEXT( there)]", tree.debug());
    Assert.assertEquals("\"hi \" + ((($X['b']=='xyz')) ? (\"A\") : (\"B\")) + \" there\"", tree.js(Context.DEFAULT, "$X"));
    Map<String, String> vars = tree.variables();
    Assert.assertEquals(1, vars.size());
    Assert.assertTrue(vars.containsKey("b"));
  }
}
