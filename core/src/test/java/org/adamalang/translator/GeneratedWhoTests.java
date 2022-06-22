/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedWhoTests extends GeneratedBase {
  private String cached_CantStateTransition_1 = null;
  private String get_CantStateTransition_1() {
    if (cached_CantStateTransition_1 != null) {
      return cached_CantStateTransition_1;
    }
    cached_CantStateTransition_1 = generateTestOutput(false, "CantStateTransition_1", "./test_code/Who_CantStateTransition_failure.a");
    return cached_CantStateTransition_1;
  }

  @Test
  public void testCantStateTransitionFailure() {
    assertLiveFail(get_CantStateTransition_1());
  }

  @Test
  public void testCantStateTransitionNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantStateTransition_1());
  }

  @Test
  public void testCantStateTransitionExceptionFree() {
    assertExceptionFree(get_CantStateTransition_1());
  }

  @Test
  public void testCantStateTransitionTODOFree() {
    assertTODOFree(get_CantStateTransition_1());
  }

  @Test
  public void stable_CantStateTransition_1() {
    String live = get_CantStateTransition_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Who_CantStateTransition_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":10},\"end\":{\"line\":1,\"character\":14}},\"severity\":1,\"source\":\"error\",\"message\":\"@who is only available from static policies, document policies, privacy policies, and message handlers (WHO)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
