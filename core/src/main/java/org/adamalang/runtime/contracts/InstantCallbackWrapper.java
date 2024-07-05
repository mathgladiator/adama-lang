/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.remote.RemoteResult;


/** experimental callback wrapper to instantly detect a return value */
public class InstantCallbackWrapper implements Callback<String> {
  private String value;
  private ErrorCodeException exception;

  public InstantCallbackWrapper() {
    this.value = null;
    this.exception = null;
  }

  @Override
  public void success(String value) {
    this.value = value;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.exception = ex;
  }

  public RemoteResult convert() {
    if (value != null) {
      return new RemoteResult(value, null, null);
    } else if (exception != null) {
      return new RemoteResult(null, "" + exception.getMessage(), exception.code);
    } else {
      return null;
    }
  }
}
