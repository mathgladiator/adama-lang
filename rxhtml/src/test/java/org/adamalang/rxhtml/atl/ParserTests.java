package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.tree.Node;
import org.adamalang.rxhtml.atl.tree.Text;
import org.junit.Assert;
import org.junit.Test;

public class ParserTests {

  @Test
  public void simple() {
    Node node = Parser.parse("xyz");
    Assert.assertTrue(node instanceof Text);
    Assert.assertEquals(((Text) node).text, "xyz");
    Assert.assertEquals("TEXT(xyz)", node.debug());
  }

  @Test
  public void variable() {
    Node node = Parser.parse("hi {first|trim} {last}");
    Assert.assertEquals("[TEXT(hi ),TRANSFORM(LOOKUP[first],trim),TEXT( ),LOOKUP[last]]", node.debug());
  }

  @Test
  public void condition_trailing() {
    Node node = Parser.parse("hi [b]active");
    Assert.assertEquals("[TEXT(hi ),(LOOKUP[b]) ? (TEXT(active)) : (EMPTY)]", node.debug());
  }

  @Test
  public void condition_trailing_negate() {
    Node node = Parser.parse("hi [!b]inactive");
    Assert.assertEquals("[TEXT(hi ),(!(LOOKUP[b])) ? (TEXT(inactive)) : (EMPTY)]", node.debug());
  }

  @Test
  public void condition() {
    Node node = Parser.parse("hi [b]A[#b]B[/b] there");
    Assert.assertEquals("[TEXT(hi ),(LOOKUP[b]) ? (TEXT(A)) : (TEXT(B)),TEXT( there)]", node.debug());
  }
}
