/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedImportTests extends GeneratedBase {
  private String cached_EmptyStringBad_1 = null;
  private String get_EmptyStringBad_1() {
    if (cached_EmptyStringBad_1 != null) {
      return cached_EmptyStringBad_1;
    }
    cached_EmptyStringBad_1 = generateTestOutput(false, "EmptyStringBad_1", "./test_code/Import_EmptyStringBad_failure.a");
    return cached_EmptyStringBad_1;
  }

  @Test
  public void testEmptyStringBadFailure() {
    assertLiveFail(get_EmptyStringBad_1());
  }

  @Test
  public void testEmptyStringBadExceptionFree() {
    assertExceptionFree(get_EmptyStringBad_1());
  }

  @Test
  public void testEmptyStringBadTODOFree() {
    assertTODOFree(get_EmptyStringBad_1());
  }

  @Test
  public void stable_EmptyStringBad_1() {
    String live = get_EmptyStringBad_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Import_EmptyStringBad_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"File '' failed to import due(ImportIssue)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Import failed (Unknown)(ImportIssue)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_NotFoundFile_2 = null;
  private String get_NotFoundFile_2() {
    if (cached_NotFoundFile_2 != null) {
      return cached_NotFoundFile_2;
    }
    cached_NotFoundFile_2 = generateTestOutput(false, "NotFoundFile_2", "./test_code/Import_NotFoundFile_failure.a");
    return cached_NotFoundFile_2;
  }

  @Test
  public void testNotFoundFileFailure() {
    assertLiveFail(get_NotFoundFile_2());
  }

  @Test
  public void testNotFoundFileExceptionFree() {
    assertExceptionFree(get_NotFoundFile_2());
  }

  @Test
  public void testNotFoundFileTODOFree() {
    assertTODOFree(get_NotFoundFile_2());
  }

  @Test
  public void stable_NotFoundFile_2() {
    String live = get_NotFoundFile_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Import_NotFoundFile_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 24");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"File 'WHATNOTFOUND.a' was not found(ImportIssue)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
