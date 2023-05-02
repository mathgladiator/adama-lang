/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import java.lang.reflect.Method;

public class Temp_CodeGenMath {
  public static void main(String[] args) throws Exception {
    String[] copy = new String[] {"min", "max", "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "atan2", "hypot", "exp", "log", "log10", "pow", "cbrt", "floorDiv", "floorMod", "IEEEremainder", "expm1", "log1p", "signum", "ulp", "fma", "copySign", "getExponent", "powerOfTwo"};
    for (String version : copy) {
      try {
        Method m = Math.class.getMethod(version, int.class, int.class);
        //Method m = Math.class.getMethod(version, double.class);
        if (m != null) {
          System.out.println("  /** " + version + " */");
          /*
          long sample = (long) m.invoke(null, 1L, 2L);
          System.out.println("  @Test\n  public void test_" + version + "_l() {");
          System.out.println("    Assert.assertEquals("+sample+"L, LibMath."+version+"(1L, 2L));\n"
              + "    Assert.assertEquals("+sample+"L, (long) LibMath."+version+"_l(1L, new NtMaybe<>(2L)).get());\n"
              + "    Assert.assertEquals("+sample+"L, (long) LibMath."+version+"_l(new NtMaybe<>(1L), 2L).get());\n"
              + "    Assert.assertEquals("+sample+"L, (long) LibMath."+version+"_l(new NtMaybe<>(1L), new NtMaybe<>(2L)).get());\n"
              + "    Assert.assertFalse(LibMath."+version+"_l(new NtMaybe<>(1L), new NtMaybe<>()).has());\n"
              + "    Assert.assertFalse(LibMath."+version+"_l(new NtMaybe<>(), 2L).has());\n"
              + "    Assert.assertFalse(LibMath."+version+"_l(1L, new NtMaybe<>()).has());\n"
             + "    Assert.assertFalse(LibMath."+version+"_l(new NtMaybe<>(), new NtMaybe<>(2L)).has());\n"
          + "    Assert.assertFalse(LibMath."+version+"_l(new NtMaybe<>(), new NtMaybe<>()).has());");
          System.out.println("  }\n");

          */



          /*
          System.out.println(
              "  @Extension\n" +
                  "  public static long "+version+"(final long x, final long y) {\n" +
                  "    return Math."+version+"(x, y);\n" +
                  "  }");
          System.out.println(
              "  @UseName(name=\""+version+"\")\n  @Extension\n" +
                  "  public static @HiddenType(clazz=Long.class) NtMaybe<Long> "+version+"_l(@HiddenType(clazz=Long.class) NtMaybe<Long> mx, long y) {\n" +
                  "    if (mx.has()) {\n" +
                  "      return new NtMaybe<>("+version+"(mx.get(), y));\n" +
                  "    }\n" +
                  "    return mx;\n" +
                  "  }");
          System.out.println(
              "  @UseName(name=\""+version+"\")\n  @Extension\n" +
                  "  public static @HiddenType(clazz=Long.class) NtMaybe<Long> "+version+"_l(long x, @HiddenType(clazz=Long.class) NtMaybe<Long> my) {\n" +
                  "    if (my.has()) {\n" +
                  "      return new NtMaybe<>("+version+"(x, my.get()));\n" +
                  "    }\n" +
                  "    return my;\n" +
                  "  }");
          System.out.println(
              "  @UseName(name=\""+version+"\")\n  @Extension\n" +
                  "  public static @HiddenType(clazz=Long.class) NtMaybe<Long> "+version+"_l(@HiddenType(clazz=Long.class) NtMaybe<Long> mx, @HiddenType(clazz=Long.class) NtMaybe<Long> my) {\n" +
                  "    if (mx.has()) {\n" +
                  "      if (my.has()) {\n" +
                  "        return new NtMaybe<>("+version+"(mx.get(), my.get()));\n" +
                  "      }\n" +
                  "      return my;\n" +
                  "    }\n" +
                  "    return mx;\n" +
                  "  }");
          */


          /*
          System.out.println(
              "  @Extension\n" +
                  "  public static double "+version+"(final double x, final double y) {\n" +
                  "    return Math."+version+"(x, y);\n" +
                  "  }");
          System.out.println(
              "  @Extension\n" +
                  "  public static @HiddenType(clazz=Double.class) NtMaybe<Double> "+version+"(@HiddenType(clazz=Double.class) NtMaybe<Double> mx, double y) {\n" +
                  "    if (mx.has()) {\n" +
                  "      return new NtMaybe<>("+version+"(mx.get(), y));\n" +
                  "    }\n" +
                  "    return mx;\n" +
                  "  }");
          System.out.println(
              "  @Extension\n" +
                  "  public static @HiddenType(clazz=Double.class) NtMaybe<Double> "+version+"(double x, @HiddenType(clazz=Double.class) NtMaybe<Double> my) {\n" +
                  "    if (my.has()) {\n" +
                  "      return new NtMaybe<>("+version+"(x, my.get()));\n" +
                  "    }\n" +
                  "    return my;\n" +
                  "  }");
          System.out.println(
              "  @Extension\n" +
                  "  public static @HiddenType(clazz=Double.class) NtMaybe<Double> "+version+"(@HiddenType(clazz=Double.class) NtMaybe<Double> mx, @HiddenType(clazz=Double.class) NtMaybe<Double> my) {\n" +
                  "    if (mx.has()) {\n" +
                  "      if (my.has()) {\n" +
                  "        return new NtMaybe<>("+version+"(mx.get(), my.get()));\n" +
                  "      }\n" +
                  "      return my;\n" +
                  "    }\n" +
                  "    return mx;\n" +
                  "  }");
          */

          // Method m = Math.class.getMethod(version, double.class);
          /*
          double sample = (double) m.invoke(null, 1.0);
          System.out.println("  @Test\n  public void test_" + version + "() {");
          System.out.println("    Assert.assertEquals("+sample+", LibMath."+version+"(1.0), 0.01);\n"
              + "    Assert.assertEquals("+sample+", LibMath."+version+"(new NtMaybe<>(1.0)).get(), 0.01);\n"
              + "    Assert.assertFalse(LibMath."+version+"(new NtMaybe<>()).has());");
          System.out.println("  }\n");
          */



          /*
          System.out.println(
              "  @Extension\n" +
              "  public static double "+version+"(final double x) {\n" +
              "    return Math."+version+"(x);\n" +
              "  }");
          System.out.println();
          System.out.println(
                  "  @Extension\n" +
                  "  public static @HiddenType(clazz=Double.class) NtMaybe<Double> "+version+"(@HiddenType(clazz=Double.class) NtMaybe<Double> mx) {\n" +
                  "    if (mx.has()) {\n" +
                      "      return new NtMaybe<>("+version+"(mx.get()));\n" +
                      "    }\n" +
                      "    return mx;\n" +
                      "  }");
          System.out.println();
          */
        }
      } catch (Exception ex) {}
    }
  }
}
