/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    gold.append("\n[{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":85},\"end\":{\"line\":11,\"character\":1,\"byte\":178}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'secure<principal>' is unable to store type 'principal'.\",\"file\":\"./test_code/Who_CantPromoteOrdinaryClient_failure.a\"}]\"--JAVA---------------------------------------------");
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
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":10,\"byte\":15},\"end\":{\"line\":1,\"character\":14,\"byte\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"@who is only available from static policies, document policies, privacy policies, bubbles, web paths, traffic hinting, and message handlers\",\"file\":\"./test_code/Who_CantStateTransition_failure.a\"}]\"--JAVA---------------------------------------------");
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
    gold.append("\n[{\"range\":{\"start\":{\"line\":6,\"character\":2,\"byte\":87},\"end\":{\"line\":6,\"character\":19,\"byte\":104}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'secure<principal>' is unable to store type 'principal'.\",\"file\":\"./test_code/Who_CopySecureClient_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
