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
package org.adamalang.runtime.sys.web;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DelayParent;
import org.adamalang.runtime.remote.RxCache;

public class EphemeralWebGet {
  public final RxCache cache;
  public final WebGet get;
  public final Callback<WebResponse> callback;
  public final DelayParent delay;

  public EphemeralWebGet(RxCache cache, WebGet get, Callback<WebResponse> callback, DelayParent delay) {
    this.cache = cache;
    this.get = get;
    this.callback = callback;
    this.delay = delay;
  }
}
