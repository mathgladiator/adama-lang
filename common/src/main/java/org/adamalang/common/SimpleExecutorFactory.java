/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

public interface SimpleExecutorFactory {
  SimpleExecutorFactory DEFAULT = new SimpleExecutorFactory() {
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

  SimpleExecutor makeSingle(String name);

  SimpleExecutor[] makeMany(String name, int nThreads);
}
