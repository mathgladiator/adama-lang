/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.privacy;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

/** defines a policy for field records */
public abstract class Policy extends DocumentPosition {
  public abstract void emit(Consumer<Token> yielder);
  public abstract void typing(Environment environment, StructureStorage owningStructureStorage);
  public abstract boolean writePrivacyCheckGuard(StringBuilderWithTabs sb, FieldDefinition field, Environment environment);
}
