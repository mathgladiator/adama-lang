/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedBugsAprilTests extends GeneratedBase {
  private String cached_Issues_1 = null;
  private String get_Issues_1() {
    if (cached_Issues_1 != null) {
      return cached_Issues_1;
    }
    cached_Issues_1 = generateTestOutput(false, "Issues_1", "./test_code/BugsApril_Issues_failure.a");
    return cached_Issues_1;
  }

  @Test
  public void testIssuesFailure() {
    assertLiveFail(get_Issues_1());
  }

  @Test
  public void testIssuesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_Issues_1());
  }

  @Test
  public void testIssuesExceptionFree() {
    assertExceptionFree(get_Issues_1());
  }

  @Test
  public void testIssuesTODOFree() {
    assertTODOFree(get_Issues_1());
  }

  @Test
  public void stable_Issues_1() {
    String live = get_Issues_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BugsApril_Issues_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":6,\"byte\":24},\"end\":{\"line\":3,\"character\":14,\"byte\":32}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":3,\"character\":6,\"byte\":24},\"end\":{\"line\":3,\"character\":10,\"byte\":28}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":3,\"character\":13,\"byte\":31},\"end\":{\"line\":3,\"character\":14,\"byte\":32}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":3,\"character\":6,\"byte\":24},\"end\":{\"line\":3,\"character\":14,\"byte\":32}},\"severity\":1,\"source\":\"error\",\"message\":\"Could not find a meaning for 'bool' + 'int' (OperatorTable)\"},{\"range\":{\"start\":{\"line\":4,\"character\":3,\"byte\":39},\"end\":{\"line\":4,\"character\":11,\"byte\":47}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":4,\"character\":3,\"byte\":39},\"end\":{\"line\":4,\"character\":7,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":4,\"character\":10,\"byte\":46},\"end\":{\"line\":4,\"character\":11,\"byte\":47}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to (Environment)\"},{\"range\":{\"start\":{\"line\":4,\"character\":3,\"byte\":39},\"end\":{\"line\":4,\"character\":11,\"byte\":47}},\"severity\":1,\"source\":\"error\",\"message\":\"Could not find a meaning for 'bool' + 'int' (OperatorTable)\"},{\"range\":{\"start\":{\"line\":5,\"character\":10,\"byte\":63},\"end\":{\"line\":5,\"character\":20,\"byte\":73}},\"severity\":1,\"source\":\"error\",\"message\":\"Type not found: a message named 'Foo' was not found. (TypeCheckReferences)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
