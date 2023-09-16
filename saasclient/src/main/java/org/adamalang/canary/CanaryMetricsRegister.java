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
package org.adamalang.canary;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CanaryMetricsRegister {
  public final AtomicInteger success_connects;
  public final AtomicInteger failure_connects;
  public final AtomicLong bandwidth;

  public CanaryMetricsRegister() {
    this.success_connects = new AtomicInteger(0);
    this.failure_connects = new AtomicInteger(0);
    this.bandwidth = new AtomicLong(0);
  }

  public void poll() throws InterruptedException {
    System.out.println("time,success_connects,failure_connects,bandwidth");
    while (true) {
      System.out.println(System.currentTimeMillis() + "," + success_connects.get() + "," + failure_connects.get() + "," + bandwidth.get());
      Thread.sleep(1000);
    }
  }
}
