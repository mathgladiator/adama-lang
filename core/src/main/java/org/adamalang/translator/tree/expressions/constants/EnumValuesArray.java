/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions.constants;

import java.util.function.Consumer;
import org.adamalang.translator.codegen.CodeGenEnums;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.shared.EnumStorage;

public class EnumValuesArray extends Expression implements LatentCodeSnippet {
  public final Token colonsToken;
  public final String enumTypeName;
  public final Token enumTypeNameToken;
  private int prefixCachedID;
  public final Token prefixToken;
  public final Token starToken;
  private EnumStorage storage;

  /** The enumeration value
   *
   * @param enumTypeNameToken the token for the type */
  public EnumValuesArray(final Token enumTypeNameToken, final Token colonsToken, final Token prefixToken, final Token starToken) {
    this.enumTypeNameToken = enumTypeNameToken;
    this.colonsToken = colonsToken;
    this.prefixToken = prefixToken;
    this.starToken = starToken;
    enumTypeName = enumTypeNameToken.text;
    ingest(enumTypeNameToken);
    ingest(starToken);
    prefixCachedID = 0;
    storage = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(enumTypeNameToken);
    yielder.accept(colonsToken);
    if (prefixToken != null) {
      yielder.accept(prefixToken);
    }
    yielder.accept(starToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var isEnum = environment.rules.FindEnumType(enumTypeName, this, false);
    if (isEnum != null) {
      if (prefixToken != null) {
        prefixCachedID = environment.autoVariable();
        storage = isEnum.storage();
        environment.document.add(this);
      }
      return new TyNativeArray((TyType) isEnum, null).withPosition(this);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.mustBeComputeContext(this);
    if (prefixToken == null) {
      sb.append("__ALL_VALUES_").append(enumTypeName);
    } else {
      sb.append("__").append(prefixToken.text).append(prefixCachedID).append("_").append(enumTypeName);
    }
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    CodeGenEnums.writeEnumArray(sb, enumTypeName, prefixToken.text + prefixCachedID, prefixToken.text, storage);
  }
}
