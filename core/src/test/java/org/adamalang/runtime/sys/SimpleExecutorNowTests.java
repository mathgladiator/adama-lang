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
package org.adamalang.runtime.sys;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.junit.Test;

public class SimpleExecutorNowTests {
  @Test
  public void coverage() {
    SimpleExecutor.NOW.execute(new NamedRunnable("y") {
      @Override
      public void execute() throws Exception {

      }
    });
    SimpleExecutor.NOW.schedule(new NamedRunnable("x") {
      @Override
      public void execute() throws Exception {

      }
    }, 1000L);
    SimpleExecutor.NOW.shutdown();
  }
}
