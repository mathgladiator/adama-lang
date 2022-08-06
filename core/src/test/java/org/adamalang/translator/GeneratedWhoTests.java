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

public class GeneratedWhoTests extends GeneratedBase {
  private String cached_CantPromoteOrdinaryClient_1 = null;
  private String get_CantPromoteOrdinaryClient_1() {
    if (cached_CantPromoteOrdinaryClient_1 != null) {
      return cached_CantPromoteOrdinaryClient_1;
    }
    cached_CantPromoteOrdinaryClient_1 = generateTestOutput(false, "CantPromoteOrdinaryClient_1", "./test_code/Who_CantPromoteOrdinaryClient_failure.a");
    return cached_CantPromoteOrdinaryClient_1;
  }

  @Test
  public void testCantPromoteOrdinaryClientFailure() {
    assertLiveFail(get_CantPromoteOrdinaryClient_1());
  }

  @Test
  public void testCantPromoteOrdinaryClientNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantPromoteOrdinaryClient_1());
  }

  @Test
  public void testCantPromoteOrdinaryClientExceptionFree() {
    assertExceptionFree(get_CantPromoteOrdinaryClient_1());
  }

  @Test
  public void testCantPromoteOrdinaryClientTODOFree() {
    assertTODOFree(get_CantPromoteOrdinaryClient_1());
  }

  @Test
  public void stable_CantPromoteOrdinaryClient_1() {
    String live = get_CantPromoteOrdinaryClient_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Who_CantPromoteOrdinaryClient_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":93},\"end\":{\"line\":11,\"character\":1,\"byte\":189}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'secure<principal>' is unable to store type 'principal'. (TypeCheckReferences)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CantStateTransition_2 = null;
  private String get_CantStateTransition_2() {
    if (cached_CantStateTransition_2 != null) {
      return cached_CantStateTransition_2;
    }
    cached_CantStateTransition_2 = generateTestOutput(false, "CantStateTransition_2", "./test_code/Who_CantStateTransition_failure.a");
    return cached_CantStateTransition_2;
  }

  @Test
  public void testCantStateTransitionFailure() {
    assertLiveFail(get_CantStateTransition_2());
  }

  @Test
  public void testCantStateTransitionNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantStateTransition_2());
  }

  @Test
  public void testCantStateTransitionExceptionFree() {
    assertExceptionFree(get_CantStateTransition_2());
  }

  @Test
  public void testCantStateTransitionTODOFree() {
    assertTODOFree(get_CantStateTransition_2());
  }

  @Test
  public void stable_CantStateTransition_2() {
    String live = get_CantStateTransition_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Who_CantStateTransition_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":10,\"byte\":16},\"end\":{\"line\":1,\"character\":14,\"byte\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"@who is only available from static policies, document policies, privacy policies, bubbles, web paths, and message handlers (WHO)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CopySecureClient_3 = null;
  private String get_CopySecureClient_3() {
    if (cached_CopySecureClient_3 != null) {
      return cached_CopySecureClient_3;
    }
    cached_CopySecureClient_3 = generateTestOutput(false, "CopySecureClient_3", "./test_code/Who_CopySecureClient_failure.a");
    return cached_CopySecureClient_3;
  }

  @Test
  public void testCopySecureClientFailure() {
    assertLiveFail(get_CopySecureClient_3());
  }

  @Test
  public void testCopySecureClientNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CopySecureClient_3());
  }

  @Test
  public void testCopySecureClientExceptionFree() {
    assertExceptionFree(get_CopySecureClient_3());
  }

  @Test
  public void testCopySecureClientTODOFree() {
    assertTODOFree(get_CopySecureClient_3());
  }

  @Test
  public void stable_CopySecureClient_3() {
    String live = get_CopySecureClient_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Who_CopySecureClient_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":6,\"character\":2,\"byte\":93},\"end\":{\"line\":6,\"character\":19,\"byte\":110}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'secure<principal>' is unable to store type 'principal'. (TypeCheckReferences)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
