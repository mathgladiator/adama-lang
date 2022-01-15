/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
    if (os != null) {
      return os.getProcessCpuLoad();
    }
    return -1;
  }

  public static double memory() {
    if (os != null) {
      // TODO: find out if allocating the entire JVM messes this number up... it probably does, but move on for now
      double free = Math.ceil(os.getFreeMemorySize() / (1024 * 1024.0));
      double total = Math.floor(os.getTotalMemorySize() / (1024 * 1024.0));
      return (total - free) / total;
    }
    return -1;
  }
}
