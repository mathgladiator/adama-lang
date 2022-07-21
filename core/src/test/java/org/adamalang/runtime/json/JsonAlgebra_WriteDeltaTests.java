package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonAlgebra_WriteDeltaTests {

  private Object of(String x) {
    return new JsonStreamReader(x).readJavaTree();
  }

  private String delta(Object from, Object to) {
    JsonStreamWriter writer = new JsonStreamWriter();
    JsonAlgebra.writeDelta(from, to, writer);
    return writer.toString();
  }

  @Test
  public void change1() {
    Object from = of("null");
    Object to = of("{}");
    Assert.assertEquals("{}", delta(from, to));
  }

  @Test
  public void change2() {
    Object from = of("{\"x\":true}");
    Object to = of("{}");
    Assert.assertEquals("{\"x\":null}", delta(from, to));
  }

  @Test
  public void change3() {
    Object from = of("{}");
    Object to = of("{\"x\":true}");
    Assert.assertEquals("{\"x\":true}", delta(from, to));
  }

  @Test
  public void change4() {
    Object from = of("{\"x\":false}");
    Object to = of("{\"x\":{\"x\":true}}");
    Assert.assertEquals("{\"x\":{\"x\":true}}", delta(from, to));
  }

  @Test
  public void change5() {
    Object from = of("{\"x\":{\"x\":false}}");
    Object to = of("{\"x\":{\"x\":true}}");
    Assert.assertEquals("{\"x\":{\"x\":true}}", delta(from, to));
  }

}
