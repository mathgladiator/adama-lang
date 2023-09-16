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
package org.adamalang.net.server;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class HandlerTests {
  @Test
  public void trivial() {
    Handler handler = new Handler(new ServerNexus(null, null, null, new ServerMetrics(new NoOpMetricsFactory()), null, null, null, null, null, 1, 2), null);
    handler.request(-1);
    handler.create(-1);
  }
}
