/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.function.Consumer;

/** imports a document into the given document */
public class ImportDocument extends DocumentPosition {
  public final String filename;
  public final Token filenameToken;
  public final Token importToken;
  public final Token semicolonToken;

  public ImportDocument(
      final Token importToken, final Token filenameToken, final Token semicolonToken) {
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
