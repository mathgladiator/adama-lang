/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.function.Consumer;

/** provides a common framework for all Definitions */
public abstract class Definition extends DocumentPosition {
  public abstract void emit(Consumer<Token> yielder);
}
