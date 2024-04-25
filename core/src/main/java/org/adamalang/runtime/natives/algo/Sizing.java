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
package org.adamalang.runtime.natives.algo;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtTemplate;

/** size of a native simple type with value -1 */
public class Sizing {
  public static long memoryOf(String x) {
    return 80 + x.length() * 2;
  }
  public static long memoryOf(NtAsset x) {
    return 120 + (x.id.length() + x.md5.length() + x.sha384.length()) * 2;
  }
  public static long memoryOf(NtDynamic x) {
    return 80 + (x.json.length() * 4);
  }
  public static long memoryOf(NtPrincipal x) {
    return 120 + (x.agent.length() + x.authority.length()) * 2;
  }
  public static long memoryOf(NtTemplate x) {
    // TODO
    return 1024;
  }
}
