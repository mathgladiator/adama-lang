/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.common;

/** Represents a code snippet which is integrated during a second phase of
 * building Java code. */
public interface LatentCodeSnippet {
  public void writeLatentJava(StringBuilderWithTabs sb);
}
