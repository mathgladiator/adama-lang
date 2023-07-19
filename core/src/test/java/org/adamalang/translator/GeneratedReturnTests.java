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

public class GeneratedReturnTests extends GeneratedBase {
  private String cached_ShouldReturnButDoesnt_1 = null;
  private String get_ShouldReturnButDoesnt_1() {
    if (cached_ShouldReturnButDoesnt_1 != null) {
      return cached_ShouldReturnButDoesnt_1;
    }
    cached_ShouldReturnButDoesnt_1 = generateTestOutput(false, "ShouldReturnButDoesnt_1", "./test_code/Return_ShouldReturnButDoesnt_failure.a");
    return cached_ShouldReturnButDoesnt_1;
  }

  @Test
  public void testShouldReturnButDoesntFailure() {
    assertLiveFail(get_ShouldReturnButDoesnt_1());
  }

  @Test
  public void testShouldReturnButDoesntNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_ShouldReturnButDoesnt_1());
  }

  @Test
  public void testShouldReturnButDoesntExceptionFree() {
    assertExceptionFree(get_ShouldReturnButDoesnt_1());
  }

  @Test
  public void testShouldReturnButDoesntTODOFree() {
    assertTODOFree(get_ShouldReturnButDoesnt_1());
  }

  @Test
  public void stable_ShouldReturnButDoesnt_1() {
    String live = get_ShouldReturnButDoesnt_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Return_ShouldReturnButDoesnt_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":2,\"byte\":27},\"end\":{\"line\":1,\"character\":9,\"byte\":34}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement expected an expression of type `int`\",\"file\":\"./test_code/Return_ShouldReturnButDoesnt_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_ShouldntReturnButDoes_2 = null;
  private String get_ShouldntReturnButDoes_2() {
    if (cached_ShouldntReturnButDoes_2 != null) {
      return cached_ShouldntReturnButDoes_2;
    }
    cached_ShouldntReturnButDoes_2 = generateTestOutput(false, "ShouldntReturnButDoes_2", "./test_code/Return_ShouldntReturnButDoes_failure.a");
    return cached_ShouldntReturnButDoes_2;
  }

  @Test
  public void testShouldntReturnButDoesFailure() {
    assertLiveFail(get_ShouldntReturnButDoes_2());
  }

  @Test
  public void testShouldntReturnButDoesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_ShouldntReturnButDoes_2());
  }

  @Test
  public void testShouldntReturnButDoesExceptionFree() {
    assertExceptionFree(get_ShouldntReturnButDoes_2());
  }

  @Test
  public void testShouldntReturnButDoesTODOFree() {
    assertTODOFree(get_ShouldntReturnButDoes_2());
  }

  @Test
  public void stable_ShouldntReturnButDoes_2() {
    String live = get_ShouldntReturnButDoes_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Return_ShouldntReturnButDoes_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":2,\"byte\":20},\"end\":{\"line\":1,\"character\":11,\"byte\":29}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement expects no expression\",\"file\":\"./test_code/Return_ShouldntReturnButDoes_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
