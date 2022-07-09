package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

public class LibAdama {
  public static NtDynamic validate(String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("Validate");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
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
}
