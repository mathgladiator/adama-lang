/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions.constants;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;

/** an enumeration constant */
public class EnumConstant extends Expression {
  public final Token colonsToken;
  public final String enumTypeName;
  public final Token enumTypeNameToken;
  private int foundValue;
  public final String value;
  public final Token valueToken;

  public EnumConstant(final Token enumTypeNameToken, final Token colonsToken, final Token valueToken) {
    this.enumTypeNameToken = enumTypeNameToken;
    this.colonsToken = colonsToken;
    this.valueToken = valueToken;
    enumTypeName = enumTypeNameToken.text;
    value = valueToken.text;
    ingest(enumTypeNameToken);
    ingest(valueToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(enumTypeNameToken);
    yielder.accept(colonsToken);
    yielder.accept(valueToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    final var isEnum = environment.rules.FindEnumType(enumTypeName, this, false);
    if (isEnum != null) {
      final var valueFound = isEnum.storage().options.get(value);
      if (valueFound == null) {
        environment.document.createError(this, String.format("Type lookup failure: unable to find value '%s' within the enumeration '%s'", value, isEnum.name()), "Enumerations");
      } else {
        foundValue = valueFound;
      }
      return ((TyType) isEnum).makeCopyWithNewPosition(this, TypeBehavior.ReadOnlyNativeValue);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append(foundValue);
  }
}
