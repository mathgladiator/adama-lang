/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 3,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 3,");
    gold.append("\n      \"character\" : 14");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: the type 'String' was not found. (TypeCheckReferences)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
