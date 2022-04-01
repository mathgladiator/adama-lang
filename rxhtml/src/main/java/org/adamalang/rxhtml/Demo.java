package org.adamalang.rxhtml;

import org.adamalang.rxhtml.tree.Node;
import org.adamalang.translator.parser.token.TokenEngine;

public class Demo {

  public static void main(String[] args) throws Exception {
    String xml = "<hi x v=0 id=\"attr\">\"name\"<x><y>  </y></x><z></z></hiz>";
    Parser parser = new Parser(new TokenEngine("demo", xml.codePoints().iterator()));

    Node node = parser.root();

    System.err.println(node.html());
    node.dump(0);


  }
}
