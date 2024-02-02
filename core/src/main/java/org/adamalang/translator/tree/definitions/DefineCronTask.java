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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;
import org.adamalang.translator.tree.types.reactive.TyReactiveInteger;
import org.adamalang.translator.tree.types.reactive.TyReactiveTime;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;

import java.util.function.Consumer;

public class DefineCronTask extends Definition {
  public final Token cron;
  public final Token name;
  public final Token[] schedule;
  public final Block code;

  public DefineCronTask(Token cron, Token name, Token[] schedule, Block code) {
    this.cron = cron;
    this.name = name;
    this.schedule = schedule;
    this.code = code;
    ingest(cron);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(cron);
    yielder.accept(name);
    for (int k = 0; k < schedule.length; k++) {
      yielder.accept(schedule[k]);
    }
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    code.format(formatter);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (environment) -> {
      if (schedule[1].isIdentifier()) {
        TyType typeFound = environment.rules.Resolve(environment.document.root.storage.fields.get(schedule[1].text).type, false);
        if (typeFound == null) {
          environment.document.createError(this, "cron job '" + name.text + "' failed to find variable '" + schedule[1].text + "' in root document");
        } else {
          switch (schedule[0].text) {
            case "daily":
              if (!(typeFound instanceof TyReactiveTime)) {
                environment.document.createError(this, "cron job '" + name.text + "' found variable '" + schedule[1].text + "' to have type '" + typeFound.getAdamaType() + "' instead of time");
              }
              break;
            case "hourly":
            case "monthly":
              if (!(typeFound instanceof TyReactiveInteger)) {
                environment.document.createError(this, "cron job '" + name.text + "' found variable '" + schedule[1].text + "' to have type '" + typeFound.getAdamaType() + "' instead of int");
              }
              break;
          }
        }
      } else {
        switch (schedule[0].text) {
          case "daily": {
            int hr = Integer.parseInt(schedule[1].text);
            int min = Integer.parseInt(schedule[3].text);
            if (hr < 0 || hr > 23) {
              environment.document.createError(this, "The hour '" + hr + "' is not between 0 and 23 inclusive");
            }
            if (min < 0 || min > 59) {
              environment.document.createError(this, "The minute '" + min + "' is not between 0 and 59 inclusive");
            }
            break;
          }
          case "hourly": {
            int min = Integer.parseInt(schedule[1].text);
            if (min < 0 || min > 31) {
              environment.document.createError(this, "The minute '" + min + "' is not between 0 and 59 inclusive");
            }
            break;
          }
          case "monthly": {
            int day = Integer.parseInt(schedule[1].text);
            if (day < 0 || day > 31) {
              environment.document.createError(this, "The day of month '" + day + "' is not between 0 and 31 inclusive");
            }
            break;
          }
        }
      }
      final var next = environment.scope();
      code.typing(next);
    });
  }
}
