/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

public interface SimpleExecutorFactory {
  public SimpleExecutor makeSingle(String name);

  public SimpleExecutor[] makeMany(String name, int nThreads);

  public static final SimpleExecutorFactory DEFAULT = new SimpleExecutorFactory() {
    @Override
    public SimpleExecutor makeSingle(String name) {
      return SimpleExecutor.create(name);
    }

    @Override
    public SimpleExecutor[] makeMany(String prefix, int nThreads) {
      SimpleExecutor[] executors = new SimpleExecutor[nThreads];
      for (int k = 0; k < nThreads; k++) {
        executors[k] = SimpleExecutor.create(prefix + "-" + nThreads);
      }
      return executors;
    }
  };
}
