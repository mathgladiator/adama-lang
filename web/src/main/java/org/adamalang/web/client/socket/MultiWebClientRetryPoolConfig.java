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
package org.adamalang.web.client.socket;

import org.adamalang.common.ConfigObject;

/** the configuration for the behavior of maintaining a connection to an endpoint */
public class MultiWebClientRetryPoolConfig {
  public final int connectionCount;
  public final int maxInflight;
  public final int findTimeout;
  public final int maxBackoff;

  public MultiWebClientRetryPoolConfig(ConfigObject config) {
    this.connectionCount = config.intOf("multi-connection-count", 2);
    this.maxInflight = config.intOf("multi-inflight-limit", 50);
    this.findTimeout = config.intOf("multi-timeout-find", 1500);
    this.maxBackoff = config.intOf("multi-max-backoff-milliseconds", 5000);
  }
}
