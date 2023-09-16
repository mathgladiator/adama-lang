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
package org.adamalang.runtime.exceptions;

import org.adamalang.runtime.natives.NtPrincipal;

/** the compute was blocked, and we must wait for data from outside */
public class ComputeBlockedException extends RuntimeException {
  public final String channel;
  public final NtPrincipal client;

  public ComputeBlockedException(final NtPrincipal client, final String channel) {
    this.client = client;
    this.channel = channel;
  }

  public ComputeBlockedException() {
    this.client = null;
    this.channel = null;
  }
}
