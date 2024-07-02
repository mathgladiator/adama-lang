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
package org.adamalang.common.metrics;

/** record success/failure rates, latency, failure codes */
public interface RequestResponseMonitor {

  /** start an operation */
  RequestResponseMonitorInstance start();

  /** the operation must success or fail. We also capture common bugs */
  interface RequestResponseMonitorInstance {
    void success();

    void extra();

    void failure(int code);
  }

  public static RequestResponseMonitorInstance UNMONITORED = new RequestResponseMonitorInstance() {
    @Override
    public void success() {
    }

    @Override
    public void extra() {
    }

    @Override
    public void failure(int code) {
    }
  };
}
