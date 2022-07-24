/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.firewall;

/** high level rules about dropping traffic before any logic */
public class WebRequestShield {
  public static boolean block(String x) {
    if (x.startsWith("/.")) return true;
    if (x.startsWith("/CSS/")) return true;
    if (x.startsWith("/Portal/")) return true;
    if (x.startsWith("/actuator/")) return true;
    if (x.startsWith("/api/")) return true;
    if (x.startsWith("/cgi-bin/")) return true;
    if (x.startsWith("/docs/")) return true;
    if (x.startsWith("/ecp/")) return true;
    if (x.startsWith("/owa/")) return true;
    if (x.startsWith("/scripts/")) return true;
    if (x.startsWith("/vendor/")) return true;
    if (x.startsWith("/remote/")) return true;
    if (x.startsWith("/portal/")) return true;
    if (x.startsWith("/d/")) return true;
    if (x.startsWith("/s/")) return true;
    if (x.startsWith("/telescope/")) return true;
    if (x.startsWith("/idx_config/")) return true;
    if (x.startsWith("/console/")) return true;
    return false;
  }
}
