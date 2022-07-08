/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.text.ot;

import java.util.ArrayList;

/** many operands combined into one via concatenation */
public class Join implements Operand {
  public final ArrayList<Operand> children;

  public Join() {
    this.children = new ArrayList<>();
  }

  @Override
  public String get() {
    StringBuilder sb = new StringBuilder();
    for (Operand op : children) {
      sb.append(op.get());
    }
    return sb.toString();
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    int curAt = 0;
    for (Operand child : children) {
      int curLen = child.length();
      int iA = Math.max(curAt, at);
      int iB = Math.min(curAt + curLen, at + length);
      if (iA < iB) {
        child.transposeRangeIntoJoin(iA - curAt, iB - iA, join);
      }
      curAt += curLen;
    }
  }

  @Override
  public int length() {
    int _length = 0;
    for (Operand op : children) {
      _length += op.length();
    }
    return _length;
  }
}
