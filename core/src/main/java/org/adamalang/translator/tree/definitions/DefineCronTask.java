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

import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;

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
}
