/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.env2;

/** Scope is a place for variables to be define */
public class Scope {
  private Scope() {

  }

  public static Scope makeRootDocument() {
    return new Scope();
  }

  public static Scope makeMessageHandler() {
    return new Scope();
  }
}
