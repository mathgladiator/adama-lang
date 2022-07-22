package org.adamalang.web.firewall;

/** high level rules about dropping traffic before any logic */
public class WebRequestShield {
  public static boolean block(String x) {
    if (x.startsWith(".git/")) return true;
    if (x.startsWith("CSS/")) return true;
    if (x.startsWith("Portal/")) return true;
    if (x.startsWith("actuator/")) return true;
    if (x.startsWith("api/")) return true;
    if (x.startsWith("cgi-bin/")) return true;
    if (x.startsWith("docs/")) return true;
    if (x.startsWith("ecp/")) return true;
    if (x.startsWith("owa/")) return true;
    if (x.startsWith("scripts/")) return true;
    if (x.startsWith("vendor/")) return true;
    return false;
  }
}
