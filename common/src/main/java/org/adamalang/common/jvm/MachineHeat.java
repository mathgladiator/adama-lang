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
package org.adamalang.common.jvm;

import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;
import java.lang.management.ManagementFactory;

public class MachineHeat {
  private static MBeanServerConnection mbsc = null;
  private static OperatingSystemMXBean os = null;

  public static void install() throws Exception {
    mbsc = ManagementFactory.getPlatformMBeanServer();
    os = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
  }

  public static double cpu() {
    double cpu = -1;
    if (os != null) {
      cpu = os.getProcessCpuLoad();
    }
    return cpu;
  }

  public static double memory() {
    double free = Math.ceil(Runtime.getRuntime().freeMemory() / (1024 * 1024.0));
    double total = Math.floor(Runtime.getRuntime().totalMemory() / (1024 * 1024.0));
    return (total - free) / total;
  }
}
