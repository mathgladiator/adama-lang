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
