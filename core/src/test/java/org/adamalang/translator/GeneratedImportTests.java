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
  public void testEmptyStringBadNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_EmptyStringBad_1());
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
    gold.append("Path:Import_EmptyStringBad_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0},\"end\":{\"line\":0,\"character\":10}},\"severity\":1,\"source\":\"error\",\"message\":\"File '' failed to import due (ImportIssue)\"},{\"range\":{\"start\":{\"line\":0,\"character\":0},\"end\":{\"line\":0,\"character\":10}},\"severity\":1,\"source\":\"error\",\"message\":\"Import failed (Unknown) (ImportIssue)\"}]\"--JAVA---------------------------------------------");
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
  public void testNotFoundFileNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_NotFoundFile_2());
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
    gold.append("Path:Import_NotFoundFile_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0},\"end\":{\"line\":0,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"File 'WHATNOTFOUND.a' was not found (ImportIssue)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
