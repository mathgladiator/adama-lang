/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
