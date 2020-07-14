/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.privacy;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

/** a privacy policy that means it is invisible for all time */
public class PrivatePolicy extends Policy {
  public final Token privateToken;

  public PrivatePolicy(final Token privateToken) {
    this.privateToken = privateToken;
    ingest(privateToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    if (privateToken != null) {
      yielder.accept(privateToken);
    }
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
  }

  @Override
  public void writePrivacyCheckAndExtractJava(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
  }
}
