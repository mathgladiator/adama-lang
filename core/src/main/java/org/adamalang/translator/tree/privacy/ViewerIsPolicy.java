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

/** a policy that enables the field to be visible if another field is the
 * viewer */
public class ViewerIsPolicy extends Policy {
  public final Token closeToken;
  public final Token fieldToken;
  public final Token openToken;
  public final Token viewerIsToken;

  public ViewerIsPolicy(final Token viewerIsToken, final Token openToken, final Token fieldToken, final Token closeToken) {
    this.viewerIsToken = viewerIsToken;
    this.openToken = openToken;
    this.fieldToken = fieldToken;
    this.closeToken = closeToken;
    ingest(viewerIsToken);
    ingest(closeToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(viewerIsToken);
    yielder.accept(openToken);
    yielder.accept(fieldToken);
    yielder.accept(closeToken);
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
    final var fd = owningStructureStorage.fields.get(fieldToken.text);
    if (fd == null) {
      environment.document.createError(this, String.format("Field '%s' was not defined within the record", fieldToken.text), "ViewerPolicy");
      return;
    }
    environment.rules.IsClient(fd.type, false);
  }

  @Override
  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
    sb.append("if (__writer.who.equals(__item.").append(fieldToken.text).append(".get())) {").tabUp().writeNewline();
    return true;
  }
}
