package org.adamalang.validators;

import org.adamalang.EndToEnd_SpaceTests;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class ValidatePlanTests {
  @Test
  public void coverage() throws Exception {
    try {
      ValidatePlan.validate(Json.newJsonObject());
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(115788, ece.code);
    }
    ValidatePlan.validate(Json.parseJsonObject(EndToEnd_SpaceTests.planFor("@static {}")));
  }
}
