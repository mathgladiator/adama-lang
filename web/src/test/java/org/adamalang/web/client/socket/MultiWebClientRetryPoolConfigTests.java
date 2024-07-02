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
package org.adamalang.web.client.socket;

import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class MultiWebClientRetryPoolConfigTests {

  @Test
  public void defaults() {
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{}")));
    Assert.assertEquals(2, config.connectionCount);
    Assert.assertEquals(50, config.maxInflight);
    Assert.assertEquals(1500, config.findTimeout);
    Assert.assertEquals(5000, config.maxBackoff);
  }

  @Test
  public void coverage() {
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{\"multi-connection-count\":5,\"multi-inflight-limit\":77,\"multi-timeout-find\":9998,\"multi-max-backoff-milliseconds\":1234}")));
    Assert.assertEquals(5, config.connectionCount);
    Assert.assertEquals(77, config.maxInflight);
    Assert.assertEquals(9998, config.findTimeout);
    Assert.assertEquals(1234, config.maxBackoff);
  }
}
