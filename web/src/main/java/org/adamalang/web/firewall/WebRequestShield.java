/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
    if (x.startsWith("/mgmt/")) return true;
    if (x.startsWith("/wp-admin/")) return true;
    return false;
  }
}
