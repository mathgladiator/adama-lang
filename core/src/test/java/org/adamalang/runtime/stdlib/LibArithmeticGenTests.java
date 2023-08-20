/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Test;

import java.lang.reflect.Method;

/** generated tests */
public class LibArithmeticGenTests {
  /** BEGIN */
  @Test
  public void battery_Xor() {
    LibArithmetic.Xor.mBB(new NtMaybe<Boolean>(true), true);
    LibArithmetic.Xor.mBB(new NtMaybe<Boolean>(true), false);
    LibArithmetic.Xor.mBB(new NtMaybe<Boolean>(), true);
    LibArithmetic.Xor.mBB(new NtMaybe<Boolean>(), false);
    LibArithmetic.Xor.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>(true));
    LibArithmetic.Xor.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>());
    LibArithmetic.Xor.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>(true));
    LibArithmetic.Xor.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>());
    LibArithmetic.Xor.BmB(true, new NtMaybe<Boolean>(true));
    LibArithmetic.Xor.BmB(true, new NtMaybe<Boolean>());
    LibArithmetic.Xor.BmB(false, new NtMaybe<Boolean>(true));
    LibArithmetic.Xor.BmB(false, new NtMaybe<Boolean>());
  }


  @Test
  public void battery_Or() {
    LibArithmetic.Or.mBB(new NtMaybe<Boolean>(true), true);
    LibArithmetic.Or.mBB(new NtMaybe<Boolean>(true), false);
    LibArithmetic.Or.mBB(new NtMaybe<Boolean>(), true);
    LibArithmetic.Or.mBB(new NtMaybe<Boolean>(), false);
    LibArithmetic.Or.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>(true));
    LibArithmetic.Or.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>());
    LibArithmetic.Or.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>(true));
    LibArithmetic.Or.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>());
    LibArithmetic.Or.BmB(true, new NtMaybe<Boolean>(true));
    LibArithmetic.Or.BmB(true, new NtMaybe<Boolean>());
    LibArithmetic.Or.BmB(false, new NtMaybe<Boolean>(true));
    LibArithmetic.Or.BmB(false, new NtMaybe<Boolean>());
  }


  @Test
  public void battery_And() {
    LibArithmetic.And.mBB(new NtMaybe<Boolean>(true), true);
    LibArithmetic.And.mBB(new NtMaybe<Boolean>(true), false);
    LibArithmetic.And.mBB(new NtMaybe<Boolean>(), true);
    LibArithmetic.And.mBB(new NtMaybe<Boolean>(), false);
    LibArithmetic.And.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>(true));
    LibArithmetic.And.mBmB(new NtMaybe<Boolean>(true), new NtMaybe<Boolean>());
    LibArithmetic.And.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>(true));
    LibArithmetic.And.mBmB(new NtMaybe<Boolean>(), new NtMaybe<Boolean>());
    LibArithmetic.And.BmB(true, new NtMaybe<Boolean>(true));
    LibArithmetic.And.BmB(true, new NtMaybe<Boolean>());
    LibArithmetic.And.BmB(false, new NtMaybe<Boolean>(true));
    LibArithmetic.And.BmB(false, new NtMaybe<Boolean>());
  }


  @Test
  public void battery_Mod() {
    LibArithmetic.Mod.LL(1000L, 1000L);
    LibArithmetic.Mod.LL(1000L, 512);
    LibArithmetic.Mod.LL(512, 1000L);
    LibArithmetic.Mod.LL(512, 512);
    LibArithmetic.Mod.II(1, 1);
    LibArithmetic.Mod.II(1, 7);
    LibArithmetic.Mod.II(7, 1);
    LibArithmetic.Mod.II(7, 7);
    LibArithmetic.Mod.LI(1000L, 1);
    LibArithmetic.Mod.LI(1000L, 7);
    LibArithmetic.Mod.LI(512, 1);
    LibArithmetic.Mod.LI(512, 7);
  }


  @Test
  public void battery_Add() {
    LibArithmetic.Add.CI(new NtComplex(1,0), 1);
    LibArithmetic.Add.CI(new NtComplex(1,0), 7);
    LibArithmetic.Add.CI(new NtComplex(0,1), 1);
    LibArithmetic.Add.CI(new NtComplex(0,1), 7);
    LibArithmetic.Add.CL(new NtComplex(1,0), 1000L);
    LibArithmetic.Add.CL(new NtComplex(1,0), 512);
    LibArithmetic.Add.CL(new NtComplex(0,1), 1000L);
    LibArithmetic.Add.CL(new NtComplex(0,1), 512);
    LibArithmetic.Add.CD(new NtComplex(1,0), 3.14);
    LibArithmetic.Add.CD(new NtComplex(1,0), 2.71);
    LibArithmetic.Add.CD(new NtComplex(0,1), 3.14);
    LibArithmetic.Add.CD(new NtComplex(0,1), 2.71);
    LibArithmetic.Add.CC(new NtComplex(1,0), new NtComplex(1,0));
    LibArithmetic.Add.CC(new NtComplex(1,0), new NtComplex(0,1));
    LibArithmetic.Add.CC(new NtComplex(0,1), new NtComplex(1,0));
    LibArithmetic.Add.CC(new NtComplex(0,1), new NtComplex(0,1));
    LibArithmetic.Add.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(1,0));
    LibArithmetic.Add.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(0,1));
    LibArithmetic.Add.mCC(new NtMaybe<NtComplex>(), new NtComplex(1,0));
    LibArithmetic.Add.mCC(new NtMaybe<NtComplex>(), new NtComplex(0,1));
    LibArithmetic.Add.CmD(new NtComplex(1,0), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.CmD(new NtComplex(1,0), new NtMaybe<Double>());
    LibArithmetic.Add.CmD(new NtComplex(0,1), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.CmD(new NtComplex(0,1), new NtMaybe<Double>());
    LibArithmetic.Add.mDI(new NtMaybe<Double>(1.5), 1);
    LibArithmetic.Add.mDI(new NtMaybe<Double>(1.5), 7);
    LibArithmetic.Add.mDI(new NtMaybe<Double>(), 1);
    LibArithmetic.Add.mDI(new NtMaybe<Double>(), 7);
    LibArithmetic.Add.mDL(new NtMaybe<Double>(1.5), 1000L);
    LibArithmetic.Add.mDL(new NtMaybe<Double>(1.5), 512);
    LibArithmetic.Add.mDL(new NtMaybe<Double>(), 1000L);
    LibArithmetic.Add.mDL(new NtMaybe<Double>(), 512);
    LibArithmetic.Add.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>());
    LibArithmetic.Add.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>());
    LibArithmetic.Add.mDD(new NtMaybe<Double>(1.5), 3.14);
    LibArithmetic.Add.mDD(new NtMaybe<Double>(1.5), 2.71);
    LibArithmetic.Add.mDD(new NtMaybe<Double>(), 3.14);
    LibArithmetic.Add.mDD(new NtMaybe<Double>(), 2.71);
    LibArithmetic.Add.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1000L);
    LibArithmetic.Add.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 512);
    LibArithmetic.Add.mCL(new NtMaybe<NtComplex>(), 1000L);
    LibArithmetic.Add.mCL(new NtMaybe<NtComplex>(), 512);
    LibArithmetic.Add.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 3.14);
    LibArithmetic.Add.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 2.71);
    LibArithmetic.Add.mCD(new NtMaybe<NtComplex>(), 3.14);
    LibArithmetic.Add.mCD(new NtMaybe<NtComplex>(), 2.71);
    LibArithmetic.Add.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Add.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>());
    LibArithmetic.Add.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Add.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>());
    LibArithmetic.Add.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1);
    LibArithmetic.Add.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 7);
    LibArithmetic.Add.mCI(new NtMaybe<NtComplex>(), 1);
    LibArithmetic.Add.mCI(new NtMaybe<NtComplex>(), 7);
    LibArithmetic.Add.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>());
    LibArithmetic.Add.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Add.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>());
  }


  @Test
  public void battery_Subtract() {
    LibArithmetic.Subtract.LC(1000L, new NtComplex(1,0));
    LibArithmetic.Subtract.LC(1000L, new NtComplex(0,1));
    LibArithmetic.Subtract.LC(512, new NtComplex(1,0));
    LibArithmetic.Subtract.LC(512, new NtComplex(0,1));
    LibArithmetic.Subtract.CI(new NtComplex(1,0), 1);
    LibArithmetic.Subtract.CI(new NtComplex(1,0), 7);
    LibArithmetic.Subtract.CI(new NtComplex(0,1), 1);
    LibArithmetic.Subtract.CI(new NtComplex(0,1), 7);
    LibArithmetic.Subtract.DC(3.14, new NtComplex(1,0));
    LibArithmetic.Subtract.DC(3.14, new NtComplex(0,1));
    LibArithmetic.Subtract.DC(2.71, new NtComplex(1,0));
    LibArithmetic.Subtract.DC(2.71, new NtComplex(0,1));
    LibArithmetic.Subtract.IC(1, new NtComplex(1,0));
    LibArithmetic.Subtract.IC(1, new NtComplex(0,1));
    LibArithmetic.Subtract.IC(7, new NtComplex(1,0));
    LibArithmetic.Subtract.IC(7, new NtComplex(0,1));
    LibArithmetic.Subtract.CL(new NtComplex(1,0), 1000L);
    LibArithmetic.Subtract.CL(new NtComplex(1,0), 512);
    LibArithmetic.Subtract.CL(new NtComplex(0,1), 1000L);
    LibArithmetic.Subtract.CL(new NtComplex(0,1), 512);
    LibArithmetic.Subtract.CD(new NtComplex(1,0), 3.14);
    LibArithmetic.Subtract.CD(new NtComplex(1,0), 2.71);
    LibArithmetic.Subtract.CD(new NtComplex(0,1), 3.14);
    LibArithmetic.Subtract.CD(new NtComplex(0,1), 2.71);
    LibArithmetic.Subtract.CC(new NtComplex(1,0), new NtComplex(1,0));
    LibArithmetic.Subtract.CC(new NtComplex(1,0), new NtComplex(0,1));
    LibArithmetic.Subtract.CC(new NtComplex(0,1), new NtComplex(1,0));
    LibArithmetic.Subtract.CC(new NtComplex(0,1), new NtComplex(0,1));
    LibArithmetic.Subtract.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(1,0));
    LibArithmetic.Subtract.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(0,1));
    LibArithmetic.Subtract.mCC(new NtMaybe<NtComplex>(), new NtComplex(1,0));
    LibArithmetic.Subtract.mCC(new NtMaybe<NtComplex>(), new NtComplex(0,1));
    LibArithmetic.Subtract.CmD(new NtComplex(1,0), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.CmD(new NtComplex(1,0), new NtMaybe<Double>());
    LibArithmetic.Subtract.CmD(new NtComplex(0,1), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.CmD(new NtComplex(0,1), new NtMaybe<Double>());
    LibArithmetic.Subtract.mDI(new NtMaybe<Double>(1.5), 1);
    LibArithmetic.Subtract.mDI(new NtMaybe<Double>(1.5), 7);
    LibArithmetic.Subtract.mDI(new NtMaybe<Double>(), 1);
    LibArithmetic.Subtract.mDI(new NtMaybe<Double>(), 7);
    LibArithmetic.Subtract.mDL(new NtMaybe<Double>(1.5), 1000L);
    LibArithmetic.Subtract.mDL(new NtMaybe<Double>(1.5), 512);
    LibArithmetic.Subtract.mDL(new NtMaybe<Double>(), 1000L);
    LibArithmetic.Subtract.mDL(new NtMaybe<Double>(), 512);
    LibArithmetic.Subtract.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>());
    LibArithmetic.Subtract.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>());
    LibArithmetic.Subtract.mDD(new NtMaybe<Double>(1.5), 3.14);
    LibArithmetic.Subtract.mDD(new NtMaybe<Double>(1.5), 2.71);
    LibArithmetic.Subtract.mDD(new NtMaybe<Double>(), 3.14);
    LibArithmetic.Subtract.mDD(new NtMaybe<Double>(), 2.71);
    LibArithmetic.Subtract.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1000L);
    LibArithmetic.Subtract.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 512);
    LibArithmetic.Subtract.mCL(new NtMaybe<NtComplex>(), 1000L);
    LibArithmetic.Subtract.mCL(new NtMaybe<NtComplex>(), 512);
    LibArithmetic.Subtract.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 3.14);
    LibArithmetic.Subtract.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 2.71);
    LibArithmetic.Subtract.mCD(new NtMaybe<NtComplex>(), 3.14);
    LibArithmetic.Subtract.mCD(new NtMaybe<NtComplex>(), 2.71);
    LibArithmetic.Subtract.mDC(new NtMaybe<Double>(1.5), new NtComplex(1,0));
    LibArithmetic.Subtract.mDC(new NtMaybe<Double>(1.5), new NtComplex(0,1));
    LibArithmetic.Subtract.mDC(new NtMaybe<Double>(), new NtComplex(1,0));
    LibArithmetic.Subtract.mDC(new NtMaybe<Double>(), new NtComplex(0,1));
    LibArithmetic.Subtract.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.ImC(1, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.ImC(1, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.ImC(7, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.ImC(7, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.ImD(1, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.ImD(1, new NtMaybe<Double>());
    LibArithmetic.Subtract.ImD(7, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.ImD(7, new NtMaybe<Double>());
    LibArithmetic.Subtract.DmD(3.14, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.DmD(3.14, new NtMaybe<Double>());
    LibArithmetic.Subtract.DmD(2.71, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.DmD(2.71, new NtMaybe<Double>());
    LibArithmetic.Subtract.mDmC(new NtMaybe<Double>(1.5), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.mDmC(new NtMaybe<Double>(1.5), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.mDmC(new NtMaybe<Double>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.mDmC(new NtMaybe<Double>(), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.LmC(1000L, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.LmC(1000L, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.LmC(512, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.LmC(512, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.LmD(1000L, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.LmD(1000L, new NtMaybe<Double>());
    LibArithmetic.Subtract.LmD(512, new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.LmD(512, new NtMaybe<Double>());
    LibArithmetic.Subtract.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1);
    LibArithmetic.Subtract.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 7);
    LibArithmetic.Subtract.mCI(new NtMaybe<NtComplex>(), 1);
    LibArithmetic.Subtract.mCI(new NtMaybe<NtComplex>(), 7);
    LibArithmetic.Subtract.DmC(3.14, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.DmC(3.14, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.DmC(2.71, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Subtract.DmC(2.71, new NtMaybe<NtComplex>());
    LibArithmetic.Subtract.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>());
    LibArithmetic.Subtract.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Subtract.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>());
  }


  @Test
  public void battery_Multiply() {
    LibArithmetic.Multiply.CI(new NtComplex(1,0), 1);
    LibArithmetic.Multiply.CI(new NtComplex(1,0), 7);
    LibArithmetic.Multiply.CI(new NtComplex(0,1), 1);
    LibArithmetic.Multiply.CI(new NtComplex(0,1), 7);
    LibArithmetic.Multiply.CL(new NtComplex(1,0), 1000L);
    LibArithmetic.Multiply.CL(new NtComplex(1,0), 512);
    LibArithmetic.Multiply.CL(new NtComplex(0,1), 1000L);
    LibArithmetic.Multiply.CL(new NtComplex(0,1), 512);
    LibArithmetic.Multiply.CD(new NtComplex(1,0), 3.14);
    LibArithmetic.Multiply.CD(new NtComplex(1,0), 2.71);
    LibArithmetic.Multiply.CD(new NtComplex(0,1), 3.14);
    LibArithmetic.Multiply.CD(new NtComplex(0,1), 2.71);
    LibArithmetic.Multiply.CC(new NtComplex(1,0), new NtComplex(1,0));
    LibArithmetic.Multiply.CC(new NtComplex(1,0), new NtComplex(0,1));
    LibArithmetic.Multiply.CC(new NtComplex(0,1), new NtComplex(1,0));
    LibArithmetic.Multiply.CC(new NtComplex(0,1), new NtComplex(0,1));
    LibArithmetic.Multiply.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(1,0));
    LibArithmetic.Multiply.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(0,1));
    LibArithmetic.Multiply.mCC(new NtMaybe<NtComplex>(), new NtComplex(1,0));
    LibArithmetic.Multiply.mCC(new NtMaybe<NtComplex>(), new NtComplex(0,1));
    LibArithmetic.Multiply.CmD(new NtComplex(1,0), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.CmD(new NtComplex(1,0), new NtMaybe<Double>());
    LibArithmetic.Multiply.CmD(new NtComplex(0,1), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.CmD(new NtComplex(0,1), new NtMaybe<Double>());
    LibArithmetic.Multiply.mDI(new NtMaybe<Double>(1.5), 1);
    LibArithmetic.Multiply.mDI(new NtMaybe<Double>(1.5), 7);
    LibArithmetic.Multiply.mDI(new NtMaybe<Double>(), 1);
    LibArithmetic.Multiply.mDI(new NtMaybe<Double>(), 7);
    LibArithmetic.Multiply.mDL(new NtMaybe<Double>(1.5), 1000L);
    LibArithmetic.Multiply.mDL(new NtMaybe<Double>(1.5), 512);
    LibArithmetic.Multiply.mDL(new NtMaybe<Double>(), 1000L);
    LibArithmetic.Multiply.mDL(new NtMaybe<Double>(), 512);
    LibArithmetic.Multiply.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>());
    LibArithmetic.Multiply.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>());
    LibArithmetic.Multiply.mDD(new NtMaybe<Double>(1.5), 3.14);
    LibArithmetic.Multiply.mDD(new NtMaybe<Double>(1.5), 2.71);
    LibArithmetic.Multiply.mDD(new NtMaybe<Double>(), 3.14);
    LibArithmetic.Multiply.mDD(new NtMaybe<Double>(), 2.71);
    LibArithmetic.Multiply.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1000L);
    LibArithmetic.Multiply.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 512);
    LibArithmetic.Multiply.mCL(new NtMaybe<NtComplex>(), 1000L);
    LibArithmetic.Multiply.mCL(new NtMaybe<NtComplex>(), 512);
    LibArithmetic.Multiply.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 3.14);
    LibArithmetic.Multiply.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 2.71);
    LibArithmetic.Multiply.mCD(new NtMaybe<NtComplex>(), 3.14);
    LibArithmetic.Multiply.mCD(new NtMaybe<NtComplex>(), 2.71);
    LibArithmetic.Multiply.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Multiply.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>());
    LibArithmetic.Multiply.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Multiply.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>());
    LibArithmetic.Multiply.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Multiply.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>());
    LibArithmetic.Multiply.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Multiply.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>());
    LibArithmetic.Multiply.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1);
    LibArithmetic.Multiply.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 7);
    LibArithmetic.Multiply.mCI(new NtMaybe<NtComplex>(), 1);
    LibArithmetic.Multiply.mCI(new NtMaybe<NtComplex>(), 7);
    LibArithmetic.Multiply.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>());
    LibArithmetic.Multiply.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Multiply.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>());
  }


  @Test
  public void battery_Divide() {
    LibArithmetic.Divide.LL(1000L, 1000L);
    LibArithmetic.Divide.LL(1000L, 512);
    LibArithmetic.Divide.LL(512, 1000L);
    LibArithmetic.Divide.LL(512, 512);
    LibArithmetic.Divide.LC(1000L, new NtComplex(1,0));
    LibArithmetic.Divide.LC(1000L, new NtComplex(0,1));
    LibArithmetic.Divide.LC(512, new NtComplex(1,0));
    LibArithmetic.Divide.LC(512, new NtComplex(0,1));
    LibArithmetic.Divide.LD(1000L, 3.14);
    LibArithmetic.Divide.LD(1000L, 2.71);
    LibArithmetic.Divide.LD(512, 3.14);
    LibArithmetic.Divide.LD(512, 2.71);
    LibArithmetic.Divide.CI(new NtComplex(1,0), 1);
    LibArithmetic.Divide.CI(new NtComplex(1,0), 7);
    LibArithmetic.Divide.CI(new NtComplex(0,1), 1);
    LibArithmetic.Divide.CI(new NtComplex(0,1), 7);
    LibArithmetic.Divide.ID(1, 3.14);
    LibArithmetic.Divide.ID(1, 2.71);
    LibArithmetic.Divide.ID(7, 3.14);
    LibArithmetic.Divide.ID(7, 2.71);
    LibArithmetic.Divide.DL(3.14, 1000L);
    LibArithmetic.Divide.DL(3.14, 512);
    LibArithmetic.Divide.DL(2.71, 1000L);
    LibArithmetic.Divide.DL(2.71, 512);
    LibArithmetic.Divide.DC(3.14, new NtComplex(1,0));
    LibArithmetic.Divide.DC(3.14, new NtComplex(0,1));
    LibArithmetic.Divide.DC(2.71, new NtComplex(1,0));
    LibArithmetic.Divide.DC(2.71, new NtComplex(0,1));
    LibArithmetic.Divide.IC(1, new NtComplex(1,0));
    LibArithmetic.Divide.IC(1, new NtComplex(0,1));
    LibArithmetic.Divide.IC(7, new NtComplex(1,0));
    LibArithmetic.Divide.IC(7, new NtComplex(0,1));
    LibArithmetic.Divide.DI(3.14, 1);
    LibArithmetic.Divide.DI(3.14, 7);
    LibArithmetic.Divide.DI(2.71, 1);
    LibArithmetic.Divide.DI(2.71, 7);
    LibArithmetic.Divide.DD(3.14, 3.14);
    LibArithmetic.Divide.DD(3.14, 2.71);
    LibArithmetic.Divide.DD(2.71, 3.14);
    LibArithmetic.Divide.DD(2.71, 2.71);
    LibArithmetic.Divide.IL(1, 1000L);
    LibArithmetic.Divide.IL(1, 512);
    LibArithmetic.Divide.IL(7, 1000L);
    LibArithmetic.Divide.IL(7, 512);
    LibArithmetic.Divide.II(1, 1);
    LibArithmetic.Divide.II(1, 7);
    LibArithmetic.Divide.II(7, 1);
    LibArithmetic.Divide.II(7, 7);
    LibArithmetic.Divide.LI(1000L, 1);
    LibArithmetic.Divide.LI(1000L, 7);
    LibArithmetic.Divide.LI(512, 1);
    LibArithmetic.Divide.LI(512, 7);
    LibArithmetic.Divide.CL(new NtComplex(1,0), 1000L);
    LibArithmetic.Divide.CL(new NtComplex(1,0), 512);
    LibArithmetic.Divide.CL(new NtComplex(0,1), 1000L);
    LibArithmetic.Divide.CL(new NtComplex(0,1), 512);
    LibArithmetic.Divide.CD(new NtComplex(1,0), 3.14);
    LibArithmetic.Divide.CD(new NtComplex(1,0), 2.71);
    LibArithmetic.Divide.CD(new NtComplex(0,1), 3.14);
    LibArithmetic.Divide.CD(new NtComplex(0,1), 2.71);
    LibArithmetic.Divide.CC(new NtComplex(1,0), new NtComplex(1,0));
    LibArithmetic.Divide.CC(new NtComplex(1,0), new NtComplex(0,1));
    LibArithmetic.Divide.CC(new NtComplex(0,1), new NtComplex(1,0));
    LibArithmetic.Divide.CC(new NtComplex(0,1), new NtComplex(0,1));
    LibArithmetic.Divide.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(1,0));
    LibArithmetic.Divide.mCC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtComplex(0,1));
    LibArithmetic.Divide.mCC(new NtMaybe<NtComplex>(), new NtComplex(1,0));
    LibArithmetic.Divide.mCC(new NtMaybe<NtComplex>(), new NtComplex(0,1));
    LibArithmetic.Divide.CmD(new NtComplex(1,0), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.CmD(new NtComplex(1,0), new NtMaybe<Double>());
    LibArithmetic.Divide.CmD(new NtComplex(0,1), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.CmD(new NtComplex(0,1), new NtMaybe<Double>());
    LibArithmetic.Divide.mDI(new NtMaybe<Double>(1.5), 1);
    LibArithmetic.Divide.mDI(new NtMaybe<Double>(1.5), 7);
    LibArithmetic.Divide.mDI(new NtMaybe<Double>(), 1);
    LibArithmetic.Divide.mDI(new NtMaybe<Double>(), 7);
    LibArithmetic.Divide.mDL(new NtMaybe<Double>(1.5), 1000L);
    LibArithmetic.Divide.mDL(new NtMaybe<Double>(1.5), 512);
    LibArithmetic.Divide.mDL(new NtMaybe<Double>(), 1000L);
    LibArithmetic.Divide.mDL(new NtMaybe<Double>(), 512);
    LibArithmetic.Divide.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.mDmD(new NtMaybe<Double>(1.5), new NtMaybe<Double>());
    LibArithmetic.Divide.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.mDmD(new NtMaybe<Double>(), new NtMaybe<Double>());
    LibArithmetic.Divide.mDD(new NtMaybe<Double>(1.5), 3.14);
    LibArithmetic.Divide.mDD(new NtMaybe<Double>(1.5), 2.71);
    LibArithmetic.Divide.mDD(new NtMaybe<Double>(), 3.14);
    LibArithmetic.Divide.mDD(new NtMaybe<Double>(), 2.71);
    LibArithmetic.Divide.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1000L);
    LibArithmetic.Divide.mCL(new NtMaybe<NtComplex>(new NtComplex(1,1)), 512);
    LibArithmetic.Divide.mCL(new NtMaybe<NtComplex>(), 1000L);
    LibArithmetic.Divide.mCL(new NtMaybe<NtComplex>(), 512);
    LibArithmetic.Divide.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 3.14);
    LibArithmetic.Divide.mCD(new NtMaybe<NtComplex>(new NtComplex(1,1)), 2.71);
    LibArithmetic.Divide.mCD(new NtMaybe<NtComplex>(), 3.14);
    LibArithmetic.Divide.mCD(new NtMaybe<NtComplex>(), 2.71);
    LibArithmetic.Divide.mDC(new NtMaybe<Double>(1.5), new NtComplex(1,0));
    LibArithmetic.Divide.mDC(new NtMaybe<Double>(1.5), new NtComplex(0,1));
    LibArithmetic.Divide.mDC(new NtMaybe<Double>(), new NtComplex(1,0));
    LibArithmetic.Divide.mDC(new NtMaybe<Double>(), new NtComplex(0,1));
    LibArithmetic.Divide.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.mCmC(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.mCmC(new NtMaybe<NtComplex>(), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.ImC(1, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.ImC(1, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.ImC(7, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.ImC(7, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.ImD(1, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.ImD(1, new NtMaybe<Double>());
    LibArithmetic.Divide.ImD(7, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.ImD(7, new NtMaybe<Double>());
    LibArithmetic.Divide.DmD(3.14, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.DmD(3.14, new NtMaybe<Double>());
    LibArithmetic.Divide.DmD(2.71, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.DmD(2.71, new NtMaybe<Double>());
    LibArithmetic.Divide.mDmC(new NtMaybe<Double>(1.5), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.mDmC(new NtMaybe<Double>(1.5), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.mDmC(new NtMaybe<Double>(), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.mDmC(new NtMaybe<Double>(), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.LmC(1000L, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.LmC(1000L, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.LmC(512, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.LmC(512, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.CmC(new NtComplex(1,0), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.CmC(new NtComplex(0,1), new NtMaybe<NtComplex>());
    LibArithmetic.Divide.LmD(1000L, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.LmD(1000L, new NtMaybe<Double>());
    LibArithmetic.Divide.LmD(512, new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.LmD(512, new NtMaybe<Double>());
    LibArithmetic.Divide.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 1);
    LibArithmetic.Divide.mCI(new NtMaybe<NtComplex>(new NtComplex(1,1)), 7);
    LibArithmetic.Divide.mCI(new NtMaybe<NtComplex>(), 1);
    LibArithmetic.Divide.mCI(new NtMaybe<NtComplex>(), 7);
    LibArithmetic.Divide.DmC(3.14, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.DmC(3.14, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.DmC(2.71, new NtMaybe<NtComplex>(new NtComplex(1,1)));
    LibArithmetic.Divide.DmC(2.71, new NtMaybe<NtComplex>());
    LibArithmetic.Divide.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.mCmD(new NtMaybe<NtComplex>(new NtComplex(1,1)), new NtMaybe<Double>());
    LibArithmetic.Divide.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>(1.5));
    LibArithmetic.Divide.mCmD(new NtMaybe<NtComplex>(), new NtMaybe<Double>());
  }

  /** END */

  private static enum MathTypes {
    Bol("B", "true", "false"),
    Int("I", "1", "7"),
    Lng("L", "1000L", "512"),
    Dbl("D", "3.14", "2.71"),
    Cpx("C", "new NtComplex(1,0)", "new NtComplex(0,1)"),
    MBol("mB", "new NtMaybe<Boolean>(true)", "new NtMaybe<Boolean>()"),
    MInt("mI", "new NtMaybe<Integer>(42)", "new NtMaybe<Integer>()"),
    MLng("mL", "new NtMaybe<Long>(12345L)", "new NtMaybe<Long>()"),
    MDbl("mD", "new NtMaybe<Double>(1.5)", "new NtMaybe<Double>()"),
    MCpx("mC", "new NtMaybe<NtComplex>(new NtComplex(1,1))", "new NtMaybe<NtComplex>()");

    public final String code;

    public final String valA;
    public final String valB;

    private MathTypes(String code, String valA, String valB) {
      this.code = code;
      this.valA = valA;
      this.valB = valB;
    }
  }

  public static void main(String[] raw) throws Exception {
    Class<?>[] clazzes = LibArithmetic.class.getClasses();
    for (Class<?> clazz : clazzes) {
      Method[] methods = clazz.getMethods();
      if (clazz.getName().endsWith("ListMath")) {
        continue;
      }
      StringBuilder testMethod = new StringBuilder();
      testMethod.append("  @Test\n");
      testMethod.append("  public void battery_"+clazz.getSimpleName()+"() {\n");
      for (Method method : methods) {
        switch (method.getName()) {
          case "wait":
            continue;
          default:
        }
        Class<?>[] inputs = method.getParameterTypes();
        if (inputs.length != 2) {
          continue;
        }
        MathTypes leftType = null;
        MathTypes rightType = null;
        for (MathTypes mt : MathTypes.values()) {
          if (method.getName().startsWith(mt.code)) {
            leftType = mt;
          }
        }
        for (MathTypes mt : MathTypes.values()) {
          if (method.getName().endsWith(mt.code)) {
            rightType = mt;
          }
        }
        if (leftType == null || rightType == null) {
          throw new Exception("failed:" + method.getName());
        }
        String[] args = new String[]{leftType.valA + ", " + rightType.valA, leftType.valA + ", " + rightType.valB, leftType.valB + ", " + rightType.valA, leftType.valB + ", " + rightType.valB};
        for (String arg : args) {
          testMethod.append("    LibArithmetic.").append(clazz.getSimpleName()).append(".").append(method.getName()).append("(").append(arg).append(");\n");
        }
      }
      testMethod.append("  }\n\n");
      System.err.println(testMethod);
    }
  }
}
