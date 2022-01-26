package org.adamalang.validators;

import org.adamalang.common.Json;
import org.adamalang.transforms.results.Keystore;
import org.junit.Test;

public class ValidateKeystoreTests {
  @Test
  public void coverage() throws Exception {
    Keystore keystore = Keystore.parse("{}");
    keystore.generate("AHAAA");
    ValidateKeystore.validate(Json.parseJsonObject(keystore.persist()));
  }
}
