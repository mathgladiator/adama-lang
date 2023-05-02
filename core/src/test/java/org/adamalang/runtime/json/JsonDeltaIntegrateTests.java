/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class JsonDeltaIntegrateTests {

  private HashMap<String, Object> of(String x) {
    return (HashMap<String, Object>) (new JsonStreamReader(x).readJavaTree());
  }

  private String str(Object tree) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeTree(tree);
    return writer.toString();
  }

  @Test
  public void values() {
    HashMap<String, Object> root = of("{}");
    HashMap<String, Object> simple = of("{\"x\":123,\"y\":42}");
    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, simple);
    Assert.assertEquals("{\"x\":123,\"y\":42}", str(root));
  }

  @Test
  public void just_obj() {
    HashMap<String, Object> root = of("{\"x\":123,\"y\":42}");
    HashMap<String, Object> simple = of("{\"x\":{\"x\":123,\"y\":42},\"y\":{\"x\":123,\"y\":42}}");
    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, simple);
    Assert.assertEquals("{\"x\":{\"x\":123,\"y\":42},\"y\":{\"x\":123,\"y\":42}}", str(root));
  }

  @Test
  public void delete() {
    HashMap<String, Object> root = of("{\"x\":123,\"y\":42}");
    HashMap<String, Object> simple = of("{\"x\":null,\"y\":null}");
    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, simple);
    Assert.assertEquals("{}", str(root));
  }

  @Test
  public void build_array() {
    HashMap<String, Object> root = of("{}");
    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"@o\":[\"1\",\"3\",\"2\"],\"1\":{\"x\":42},\"2\":{\"x\":50},\"3\":{\"x\":100}}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"1\",\"x\":42},{\"__key\":\"3\",\"x\":100},{\"__key\":\"2\",\"x\":50}],\"#x\":{\"1\":{\"__key\":\"1\",\"x\":42},\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":100},\"__key\":\"x\",\"@o\":true}}", str(root));

    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"1\":{\"x\":500}}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"1\",\"x\":500},{\"__key\":\"3\",\"x\":100},{\"__key\":\"2\",\"x\":50}],\"#x\":{\"1\":{\"__key\":\"1\",\"x\":500},\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":100},\"__key\":\"x\",\"@o\":true}}", str(root));

    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"@o\":[[0,1]]}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"1\",\"x\":500},{\"__key\":\"3\",\"x\":100}],\"#x\":{\"1\":{\"__key\":\"1\",\"x\":500},\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":100},\"__key\":\"x\",\"@o\":true}}", str(root));

    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"1\":null,\"2\":null,\"3\":null,\"@s\":0}}"));
    Assert.assertEquals("{\"x\":[],\"#x\":{\"__key\":\"x\",\"@o\":true}}", str(root));
  }

  @Test
  public void array_with_values() {
    HashMap<String, Object> root = of("{}");
    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"@o\":[\"1\",\"3\",\"2\"],\"1\":{\"x\":42},\"2\":{\"x\":50},\"3\":{\"x\":100}}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"1\",\"x\":42},{\"__key\":\"3\",\"x\":100},{\"__key\":\"2\",\"x\":50}],\"#x\":{\"1\":{\"__key\":\"1\",\"x\":42},\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":100},\"__key\":\"x\",\"@o\":true}}", str(root));

    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"1\":5000,\"@o\":[3,2,1]}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"3\",\"x\":100},{\"__key\":\"2\",\"x\":50},5000],\"#x\":{\"1\":5000,\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":100},\"__key\":\"x\",\"@o\":true}}", str(root));

    JsonDeltaIntegrate.integrateDeltaIntoRoot(root, of("{\"x\":{\"3\":{\"x\":500000},\"@s\":1}}"));
    Assert.assertEquals("{\"x\":[{\"__key\":\"3\",\"x\":500000}],\"#x\":{\"1\":5000,\"2\":{\"__key\":\"2\",\"x\":50},\"3\":{\"__key\":\"3\",\"x\":500000},\"__key\":\"x\",\"@o\":true}}", str(root));
  }
}
