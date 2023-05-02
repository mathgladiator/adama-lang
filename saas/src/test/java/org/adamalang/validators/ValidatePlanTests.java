/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.validators;

import org.adamalang.EndToEnd_SpaceInfoTests;
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
    ValidatePlan.validate(Json.parseJsonObject(EndToEnd_SpaceInfoTests.planFor("@static {}")));
  }
}
