/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.privacy;

import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

/** a public policy that means it is visible for all time */
public class PublicPolicy extends Policy {
  public final Token publicToken;

  public PublicPolicy(final Token publicToken) {
    this.publicToken = publicToken;
    ingest(publicToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (publicToken != null) {
      yielder.accept(publicToken);
    }
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
  }

  @Override
  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
    return false;
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.writeString("public");
  }

}
