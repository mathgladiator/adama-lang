/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.privacy;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

/** defines a policy for field records */
public abstract class Policy extends DocumentPosition {
  public abstract void emit(Consumer<Token> yielder);
  public abstract void typing(Environment environment, StructureStorage owningStructureStorage);

  protected void writeAllow(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment, final boolean tabDown) {
    var resolvedType = environment.rules.Resolve(field.type, false);
    var addGet = false;
    if (resolvedType instanceof DetailComputeRequiresGet && !(resolvedType instanceof TyReactiveMaybe)) {
      addGet = true;
      resolvedType = ((DetailComputeRequiresGet) resolvedType).typeAfterGet(environment);
    }
    if (resolvedType instanceof DetailHasBridge) {
      final var bridge = ((DetailHasBridge) resolvedType).getBridge(environment);
      if (!environment.state.hasNoCost()) {
        sb.append("__code_cost++;").writeNewline();
      }
      sb.append("__view.set(\"").append(field.name).append("\", ").append(bridge).append(".toPrivateJsonNode(__who, ").append(field.name);
      if (addGet) {
        sb.append(".get()");
      }
      sb.append("));");
    }
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  public abstract void writePrivacyCheckAndExtractJava(StringBuilderWithTabs sb, FieldDefinition field, Environment environment);
}
