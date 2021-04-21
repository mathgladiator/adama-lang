/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.common;

/** Represents a code snippet which is integrated during a second phase of
 * building Java code. */
public interface LatentCodeSnippet {
  public void writeLatentJava(StringBuilderWithTabs sb);
}
