/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedBlockTests extends GeneratedBase {
  private String cached_DeadCode_1 = null;
  private String get_DeadCode_1() {
    if (cached_DeadCode_1 != null) {
      return cached_DeadCode_1;
    }
    cached_DeadCode_1 = generateTestOutput(false, "DeadCode_1", "./test_code/Block_DeadCode_failure.a");
    return cached_DeadCode_1;
  }

  @Test
  public void testDeadCodeFailure() {
    assertLiveFail(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeExceptionFree() {
    assertExceptionFree(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeTODOFree() {
    assertTODOFree(get_DeadCode_1());
  }

  @Test
  public void stable_DeadCode_1() {
    String live = get_DeadCode_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Block_DeadCode_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 9");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"This code is unreachable.(Block)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateVariable_2 = null;
  private String get_DuplicateVariable_2() {
    if (cached_DuplicateVariable_2 != null) {
      return cached_DuplicateVariable_2;
    }
    cached_DuplicateVariable_2 = generateTestOutput(false, "DuplicateVariable_2", "./test_code/Block_DuplicateVariable_failure.a");
    return cached_DuplicateVariable_2;
  }

  @Test
  public void testDuplicateVariableFailure() {
    assertLiveFail(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableExceptionFree() {
    assertExceptionFree(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableTODOFree() {
    assertTODOFree(get_DuplicateVariable_2());
  }

  @Test
  public void stable_DuplicateVariable_2() {
    String live = get_DuplicateVariable_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Block_DuplicateVariable_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 5");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Variable 'x' was already defined(EnvironmentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
