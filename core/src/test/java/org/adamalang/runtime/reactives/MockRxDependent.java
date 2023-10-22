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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

public class MockRxDependent extends RxDependent {
  protected MockRxDependent(RxParent __parent) {
    super(__parent);
  }

  public boolean invalidRaised;

  @Override
  public boolean __raiseInvalid() {
    invalidRaised = true;
    return false;
  }

  public boolean getAndResetInvalid() {
    boolean prior = invalidRaised;
    invalidRaised = false;
    return prior;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {

  }

  @Override
  public void __dump(JsonStreamWriter writer) {

  }

  @Override
  public void __insert(JsonStreamReader reader) {

  }

  @Override
  public void __patch(JsonStreamReader reader) {

  }

  @Override
  public void __revert() {

  }

  @Override
  public boolean alive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }
}
