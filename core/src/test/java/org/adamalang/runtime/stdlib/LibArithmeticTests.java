/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibArithmeticTests {

  @Test
  public void coverage() {
    new LibArithmetic();
    new LibArithmetic.Divide();
    new LibArithmetic.Multiply();
    new LibArithmetic.Subtract();
    new LibArithmetic.Add();
    new LibArithmetic.Mod();
  }

  @Test
  public void doubleDivision() {
    Assert.assertEquals(0.5, LibArithmetic.Divide.DD(1, 2.0).get(), 0.01);
    Assert.assertEquals(0.5, LibArithmetic.Divide.mDD(new NtMaybe<>(1.0), 2).get(), 0.01);
    Assert.assertEquals(0.5, LibArithmetic.Divide.DmD(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(
        0.5, LibArithmetic.Divide.mDmD(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibArithmetic.Divide.mDD(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibArithmetic.Divide.DmD(1.0, new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(), new NtMaybe<>(1.0)).has());
    Assert.assertFalse(LibArithmetic.Divide.DD(1, 0.0).has());
  }

  @Test
  public void intDivision() {
    Assert.assertEquals(0.5, LibArithmetic.Divide.II(1, 2).get(), 0.01);
    Assert.assertFalse(LibArithmetic.Divide.II(1, 0).has());
  }


  private static String codeWord(String ty) {
    switch (ty) {
      case "tyInt": return "I";
      case "tyLong": return "L";
      case "tyDouble": return "D";
      case "tyMaybeDouble": return "mD";
      case "tyComplex": return "C";
      case "tyMaybeComplex": return "mC";
    }
    throw new NullPointerException();
  }

  private static String jType(String ty) {
    switch (ty) {
      case "tyInt": return "int";
      case "tyLong": return "long";
      case "tyDouble": return "double";
      case "tyMaybeDouble": return "NtMaybe<Double>";
      case "tyComplex": return "NtComplex";
      case "tyMaybeComplex": return "NtMaybe<NtComplex>";
      case "tyBoolean": return "boolean";
      case "tyString": return "String";
      case "tyMaybeString": return "NtMaybe<String>";
    }
    throw new NullPointerException();
  }

  private static String aType(String ty) {
    switch (ty) {
      case "tyInt": return "int";
      case "tyLong": return "long";
      case "tyDouble": return "double";
      case "tyMaybeDouble": return "maybe<double>";
      case "tyComplex": return "complex";
      case "tyMaybeComplex": return "maybe<complex>";
      case "tyBoolean": return "bool";
      case "tyString": return "string";
      case "tyMaybeString": return "maybe<string>";
    }
    throw new NullPointerException();
  }

  private static String invent(String ty) {
    switch (ty) {
      case "tyInt": return "1";
      case "tyLong": return "2L";
      case "tyDouble": return "3.5";
      case "tyMaybeDouble": return "@maybe(4.5)";
      case "tyComplex": return "1 + 2 * @i";
      case "tyMaybeComplex": return "@maybe(7 + 2 * @i)";
      case "tyBoolean": return "true";
      case "tyString": return "\"s\"";
      case "tyMaybeString": return "@maybe(\"maybe?\")";
    }
    throw new NullPointerException();
  }

  @Test
  public void generateTable() {
    String[] x = new String[] { "tyInt", "tyLong", "tyDouble", "tyComplex" };
    for (int k = 0; k < x.length; k++) {
      System.out.println("public " + aType(x[k]) + " r" + k + ";");
    }
    System.out.println("@construct {");
    for (int k = 0; k < x.length; k++) {
      System.out.println("  " + aType(x[k]) + " l" + k + ";");
      for (int j = 0; j <= k; j++) {
        System.out.println("  r" + k + " -= " + invent(x[j]) + ";");
        System.out.println("  l" + k + " -= " + invent(x[j]) + ";");
        System.out.println("  r" + k + " -= l" + k + ";");
      }
    }
    System.out.println("}");


    /*
    String[] ops = new String[] { "<", "<=", "==", "!=",">=", ">"};
    for (String op : ops) {
      for (String a : x) {
        for (String b : x) {
          if (("==".equals(op) || "!=".equals(op)) && (a.contains("Double") || b.contains("Double"))) {
            if ("==".equals(op)) {
              System.out.println("insert(" + a + ", \"" + op + "\", " + b + ", tyBoolean, \"LibMath.near(%s, %s)\", false);");
            } else {
              System.out.println("insert(" + a + ", \"" + op + "\", " + b + ", tyBoolean, \"!LibMath.near(%s, %s)\", false);");
            }
          } else {
            System.out.println("insert("+a+", \""+op+"\", "+b+", tyBoolean, \"%s "+op+" %s\", false);");
          }
        }
        if (op.equals("==")) {
          System.out.println("insert(tyComplex, \"==\", "+a+", tyBoolean, \"LibMath.near(%s, %s)\", false);");
          System.out.println("insert("+a+", \"==\", tyComplex, tyBoolean, \"LibMath.near(%s, %s)\", true);");
        }
      }
    }
    System.out.println("insert(tyString, \"<\", tyString, tyBoolean, \"(%s).compareTo(%s) < 0\", false);");
    System.out.println("insert(tyString, \"<=\", tyString, tyBoolean, \"(%s).compareTo(%s) <= 0\", false);");
    System.out.println("insert(tyString, \"==\", tyString, tyBoolean, \"(%s).equals(%s)\", false);");
    System.out.println("insert(tyString, \"!=\", tyString, tyBoolean, \"!((%s).equals(%s))\", false);");
    System.out.println("insert(tyString, \">=\", tyString, tyBoolean, \"(%s).compareTo(%s) >= 0\", false);");
    System.out.println("insert(tyString, \">\", tyString, tyBoolean, \"(%s).compareTo(%s) > 0\", false);");

    for (String z : new String[] { "tyLabel", "tyAsset", "tyClient" }) {
      System.out.println("insert("+z+", \"==\", "+z+", tyBoolean, \"(%s).equals(%s)\", false);");
      System.out.println("insert("+z+", \"!=\", "+z+", tyBoolean, \"!((%s).equals(%s))\", false);");
    }

    System.out.println("insert(tyBoolean \"==\", tyBoolean, tyBoolean, \"%s == %s\", false);");
    System.out.println("insert(tyBoolean \"!=\", tyBoolean, tyBoolean, \"%s != %s\", false);");
    */

    /*
    String[] x = new String[] { "tyInt", "tyLong", "tyDouble", "tyMaybeDouble", "tyComplex", "tyMaybeComplex", "tyBoolean", "tyString", "tyMaybeString"};
    for (String a : x) {
      System.out.println("// " + a);
      System.out.println("insert(" + a + ", \"+\", tyString, tyString, \"%s + %s\", false);");
      System.out.println("insert(tyString, \"+\", " + a + ", tyString, \"%s + %s\", false);");
      System.out.println("insert(" + a + ", \"+\", tyMaybeString, tyString, \"%s + (%s).toString()\", false);");
      System.out.println("insert(tyMaybeString, \"+\", " + a + ", tyString, \"(%s).toString() + %s\", false);");
    }
    */

    /*
    String[] x = new String[] { "tyInt", "tyLong", "tyDouble", "tyMaybeDouble", "tyComplex", "tyMaybeComplex"};
    for (String a : x) {
      for (String b : x) {
        String c = a+b;
        if (c.contains("Maybe") || c.contains("Complex")) {
          // System.out.println("insert(" + a + ", \"+\", " + b + ", ty, \"LibArithmetic.Add."+codeWord(a) + codeWord(b)+"(%s, %s)\", false);");
          String result = c.contains("Complex") ? "NtComplex" : "Double";
          if (c.contains("Maybe")) {
            result = "NtMaybe<" + result + ">";
          }
          System.out.println("    public static " + result + " " + codeWord(a) + codeWord(b) + "(" + jType(a) + " x, " + jType(b) + " y) {");
          System.out.println("    }");
        } else {
          // System.out.println("insert(" + a + ", \"+\", " + b + ", ty, \"%s + %s\", false);");
        }
      }
    }
    */
  }

  @Test
  public void generateTestCase() {
    // String[] x = new String[] { "1", "1L", "0.5", "(1 / 2)", "(1 / 0)", "@i", "(1 / @i)", "(@i / 0)", "0", "0L", "0.0", "(@i * 0)", "(@i / 0.0)", "\"x\"", "@maybe(\"x\")", "@maybe<string>"};
    String[] x = new String[] { "true", "false"};
    int k = 0;
    for (String a : x) {
      for (String b : x) {
        System.out.println("public formula f" + k + " = " + a + " || " + b + ";");
        k++;
        System.out.println("public formula f" + k + " = " + a + " && " + b + ";");
        k++;
        System.out.println("public formula f" + k + " = " + a + " ^^ " + b + ";");
        k++;
      }
    }
  }
}
