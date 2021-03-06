/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.definitions;

import java.util.function.Consumer;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

/** imports a document into the given document */
public class ImportDocument extends DocumentPosition {
  public final String filename;
  public final Token filenameToken;
  public final Token importToken;
  public final Token semicolonToken;

  public ImportDocument(final Token importToken, final Token filenameToken, final Token semicolonToken) {
    this.importToken = importToken;
    this.filenameToken = filenameToken;
    this.semicolonToken = semicolonToken;
    ingest(importToken);
    ingest(filenameToken);
    ingest(semicolonToken);
    final var rawFilename = filenameToken.text;
    if (rawFilename.length() <= 2) {
      filename = "";
    } else {
      filename = rawFilename.substring(1, rawFilename.length() - 1);
    }
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(importToken);
    yielder.accept(filenameToken);
    yielder.accept(semicolonToken);
  }
}
