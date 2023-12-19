/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

  public Scope makeCronTask() {
    return new Scope(this);
  }
}
