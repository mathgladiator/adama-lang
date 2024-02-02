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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineCronTask;

import java.util.Map;

public class CodeGenCron {
  public static void writeCronExecution(final StringBuilderWithTabs sb, Environment env) {
    for (Map.Entry<String, DefineCronTask> entry : env.document.cronTasks.entrySet()) {
      sb.append("private void __cron_body_").append(entry.getKey()).append("()");
      entry.getValue().code.writeJava(sb, env);
      sb.writeNewline();
    }
    sb.append("@Override").writeNewline();
    int countdown = env.document.cronTasks.size();
    if (countdown == 0) {
      sb.append("public void __make_cron_progress() {}").writeNewline();
      return;
    }
    sb.append("public void __make_cron_progress() {").tabUp().writeNewline();
    sb.append("CronTask __current;").writeNewline();
    sb.append("long __now = __time.get();").writeNewline();
    sb.append("ZoneId __fromTZ = ZoneId.systemDefault();").writeNewline();
    sb.append("ZoneId __toTZ = __zoneId();").writeNewline();
    for (Map.Entry<String, DefineCronTask> entry : env.document.cronTasks.entrySet()) {
      DefineCronTask dct = entry.getValue();
      if (dct.schedule[1].isIdentifier()) {
        sb.append("__current = CronChecker.").append(dct.schedule[0].text).append("(__").append(dct.name.text).append(", __now, ").append(dct.schedule[1].text).append(", __fromTZ, __toTZ);").writeNewline();
      } else{
        switch (dct.schedule[0].text) {
          case "daily":
            sb.append("__current = CronChecker.daily(__").append(dct.name.text).append(", __now, ").append(dct.schedule[1].text).append(", ").append(dct.schedule[3].text).append(", __fromTZ, __toTZ);").writeNewline();
            break;
          case "hourly": // note: these share the same signature with different function names
          case "monthly":
            sb.append("__current = CronChecker.").append(dct.schedule[0].text).append("(__").append(dct.name.text).append(", __now, ").append(dct.schedule[1].text).append(", __fromTZ, __toTZ);").writeNewline();
            break;
          default:
            throw new RuntimeException("unknown schedule type:" + dct.schedule[0].text);
        }
      }
      sb.append("if (__current.fire) ").append("{").tabUp().writeNewline();
      sb.append("__cron_body_").append(entry.getKey()).append("();").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("__optimisticNextCronCheck = __current.integrate(__optimisticNextCronCheck);");
      countdown--;
      if (countdown == 0) {
        sb.tabDown();
      }
      sb.writeNewline();
    }
    sb.append("}").writeNewline();
  }
}
