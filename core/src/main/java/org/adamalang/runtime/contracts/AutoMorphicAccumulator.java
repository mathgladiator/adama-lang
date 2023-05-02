/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

/** provides a mechanism to accumulate data into a final bundle */
public interface AutoMorphicAccumulator<T> {
  /** is the accumulator empty */
  boolean empty();

  /** provide a data element */
  void next(T data);

  /** finish the stream */
  T finish();
}
