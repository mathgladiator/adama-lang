package org.adamalang.rxhtml;

import org.adamalang.rxhtml.tree.Node;
import org.adamalang.rxhtml.tree.Text;
import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Assert;
import org.junit.Test;

public class ParserTests {
  @Test
  public void simple() throws Exception {
    String html = "<root>\"text\"</root>";
    Parser parser = new Parser(new TokenEngine("internal", html.codePoints().iterator()));
    Node node = parser.root();
    Assert.assertEquals("root", node.tag);
    Assert.assertEquals(1, node.children.length);
    Assert.assertTrue(node.children[0] instanceof Text);
    Assert.assertEquals("\"text\"", ((Text) node.children[0]).token.text);
  }
}
