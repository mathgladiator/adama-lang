/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.env2;

import org.adamalang.translator.tree.definitions.DocumentEvent;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;

/** Scope is a place for variables to be define */
public class Scope {
  private final Scope parent;

  private Scope(Scope parent) {
    this.parent = parent;
  }

  public static Scope makeRootDocument() {
    return new Scope(null);
  }

  public Scope makeWebHandler(String verb) {
    return new Scope(this);
  }

  public Scope makeStateMachineTransition() {
    return new Scope(this);
  }

  public Scope makeDocumentEvent(DocumentEvent event) {
    return new Scope(this);
  }

  public Scope makeAuthorize() {
    return new Scope(this);
  }

  public Scope makePassword() {
    return new Scope(this);
  }

  public Scope makeTest() {
    return new Scope(this);
  }

  public Scope makeProcedure(FunctionPaint fp) {
    return new Scope(this);
  }

  public Scope makeMethod(FunctionPaint fp) {
    return new Scope(this);
  }

  public Scope makeFunction(FunctionPaint fp) {
    return new Scope(this);
  }

  public Scope makePolicy() {
    return new Scope(this);
  }

  public Scope makeBubble() {
    return new Scope(this);
  }

  public Scope makeLambdaScope() {
    return new Scope(this);
  }

  public Scope makeReplication() {
    return new Scope(this);
  }

  public Scope makeStaticScope() {
    return new Scope(this);
  }

  public Scope makeLinkScope() {
    return new Scope(this);
  }

  public Scope makeServiceScope() {
    return new Scope(this);
  }

  public Scope makeConstructor() {
    return new Scope(this);
  }

  public Scope makeDispatch() {
    return new Scope(this);
  }

  public Scope makeConstant() {
    return new Scope(this);
  }

  public Scope makeMessageHandler() {
    return new Scope(this);
  }

  public Scope makeBranchScope() {
    return new Scope(this);
  }

  public Scope makeRecordType() {
    return new Scope(this);
  }

  public Scope makeMessageType() {
    return new Scope(this);
  }
}
