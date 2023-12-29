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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.SymbolIndex;

public class LibAdama {
  public static NtDynamic validate(String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("Validate");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, new SymbolIndex(), Scope.makeRootDocument());
      parser.document().accept(document);
      if (!document.check(state)) {
        return new NtDynamic(document.errorsJson());
      } else {
        return new NtDynamic("[]");
      }
    } catch (AdamaLangException ale) {
      return new NtDynamic("[{\"error\":true}]");
    }
  }

  public static NtDynamic reflect(String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("Validate");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, new SymbolIndex(), Scope.makeRootDocument());
      parser.document().accept(document);
      if (document.check(state)) {
        JsonStreamWriter writer = new JsonStreamWriter();
        document.writeTypeReflectionJson(writer);
        return new NtDynamic(writer.toString());
      }
    } catch (AdamaLangException ale) {
    }
    return new NtDynamic("null");
  }
}
