/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedCrashReportTests extends GeneratedBase {
  private String cached_March21_1 = null;
  private String get_March21_1() {
    if (cached_March21_1 != null) {
      return cached_March21_1;
    }
    cached_March21_1 = generateTestOutput(false, "March21_1", "./test_code/CrashReport_March21_failure.a");
    return cached_March21_1;
  }

  @Test
  public void testMarch21Failure() {
    assertLiveFail(get_March21_1());
  }

  @Test
  public void testMarch21NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_March21_1());
  }

  @Test
  public void testMarch21ExceptionFree() {
    assertExceptionFree(get_March21_1());
  }

  @Test
  public void testMarch21TODOFree() {
    assertTODOFree(get_March21_1());
  }

  @Test
  public void stable_March21_1() {
    String live = get_March21_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:CrashReport_March21_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":77},\"end\":{\"line\":3,\"character\":14,\"byte\":89}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'maybe<String>' is using a type that was not found. (TypeCheckReferences)\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":60},\"end\":{\"line\":3,\"character\":27,\"byte\":102}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'bound_type' has no type (StructureTyping)\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":60},\"end\":{\"line\":3,\"character\":27,\"byte\":102}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'bound_type' has no backing type (EnvironmentDefine)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
