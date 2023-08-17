/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LinterTests {
  public static String reflect(String code) {
    try {
      CompilerOptions.Builder opts = CompilerOptions.start().enableCodeCoverage();
      opts.packageName = "P";
      final var options = opts.noCost().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("MeCode");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      parser.document().accept(document);
      if (!document.check(state)) {
        throw new Exception("Failed to check:" + document.errorsJson());
      }
      JsonStreamWriter reflection = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflection);
      return reflection.toString();
    } catch (Exception ex) {
      Assert.fail("failed to parse:" + ex.getMessage());
      return "";
    }
  }

  @Test
  public void enumLabelDataChangeWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("enum EEE { A, B, C }"), reflect("enum EEE { A, D, B, C } "));
    Assert.assertEquals(2, diagnostics.size());
    Assert.assertEquals("enumeration 'EEE' has a label change for 'B from 1 to 2.", diagnostics.get(0));
    Assert.assertEquals("enumeration 'EEE' has a label change for 'C from 2 to 3.", diagnostics.get(1));
  }

  @Test
  public void rootLongToIntWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public long x;"), reflect("public int x;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void rootLostData() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public long x;"), reflect(""));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' was removed", diagnostics.get(0));
  }

  @Test
  public void rootLongToDoubleWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public long x;"), reflect("public double x;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is being compacted from long to double and may result in data precision.", diagnostics.get(0));
  }

  @Test
  public void recordLongToIntWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public long x; }"), reflect("record R { public int x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void recordLongToDoubleWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public long x; }"), reflect("record R { public double x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to double and may result in data precision.", diagnostics.get(0));
  }

  @Test
  public void recordLongToComplexWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public long x; }"), reflect("record R { public complex x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to complex and may result in data precision.", diagnostics.get(0));
  }

  @Test
  public void recordIntToDoubleFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public int x; }"), reflect("record R { public double x; }"));
    Assert.assertEquals(0, diagnostics.size());
  }

  @Test
  public void recordIntToLongFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public int x; }"), reflect("record R { public long x; }"));
    Assert.assertEquals(0, diagnostics.size());
  }

  @Test
  public void recordIntToComplexeFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public int x; }"), reflect("record R { public complex x; }"));
    Assert.assertEquals(0, diagnostics.size());
  }

  @Test
  public void recordDoubleToComplexeFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public complex x; }"), reflect("record R { public complex x; }"));
    Assert.assertEquals(0, diagnostics.size());
  }

  @Test
  public void rootStringToDoubleWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public string x;"), reflect("public double x;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is change from a string to a double which may lose data.", diagnostics.get(0));
  }

  @Test
  public void recordStringToIntWarns() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public string x; }"), reflect("record R { public int x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is change from a string to a int which may lose data.", diagnostics.get(0));
  }

  @Test
  public void recordLongToMaybeInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public long x; }"), reflect("record R { public maybe<int> x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void recordMaybeLongToInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public maybe<long> x; }"), reflect("record R { public int x; }"));
    Assert.assertEquals(2, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to int and may result in data loss.", diagnostics.get(0));
    Assert.assertEquals("field 'x' in record 'R' is dropping the maybe and this may result in data invention of default data.", diagnostics.get(1));
  }

  @Test
  public void rootLongToMaybeInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public long x;"), reflect("public maybe<int> x;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void rootMaybeLongToInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public maybe<long> x;"), reflect("public int x;"));
    Assert.assertEquals(2, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is being compacted from long to int and may result in data loss.", diagnostics.get(0));
    Assert.assertEquals("field 'x' in root document is dropping the maybe and this may result in data invention of default data.", diagnostics.get(1));
  }

  @Test
  public void rootMaybeLongToMaybeInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("public maybe<long> x;"), reflect("public maybe<int> x;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in root document is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void recordMaybeLongToMaybeInt() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public maybe<long> x; }"), reflect("record R { public maybe<int> x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void tableDropRoot() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R1 { public long x; } table<R1> t;"), reflect("public int t;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 't' in root document is a table dropping to another type.", diagnostics.get(0));
  }

  @Test
  public void tableDropRecord() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R1 { public long x; } record R2 { table<R1> t; }"), reflect("record R2 { public int t; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 't' in record 'R2' is a table dropping to another type.", diagnostics.get(0));
  }

  @Test
  public void tableWithIssues() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R1 { public long x; } table<R1> t;"), reflect("record R2 { public int x; } table<R2> t;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in table at field 't' in root document is being compacted from long to int and may result in data loss.", diagnostics.get(0));
  }

  @Test
  public void assetToAnythingElse() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public asset x; }"), reflect("record R { public int x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is change from a asset to a int which will lose data.", diagnostics.get(0));
  }

  @Test
  public void principalToAnythingElse() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R { public principal x; }"), reflect("record R { public int x; }"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record 'R' is change from a principal to a int which will lose data.", diagnostics.get(0));
  }

  @Test
  public void recordToRecordFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R1 { public principal x; } R1 r;"), reflect("record R2 { public principal x; } R2 r;"));
    Assert.assertEquals(0, diagnostics.size());
  }

  @Test
  public void recordToRecordNotFine() {
    ArrayList<String> diagnostics = Linter.compare(reflect("record R1 { public principal x; } R1 r;"), reflect("record R2 { public asset x; } R2 r;"));
    Assert.assertEquals(1, diagnostics.size());
    Assert.assertEquals("field 'x' in record at field 'r' in root document is change from a principal to a asset which will lose data.", diagnostics.get(0));
  }
}
