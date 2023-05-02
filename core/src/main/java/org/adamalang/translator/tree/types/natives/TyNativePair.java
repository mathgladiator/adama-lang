/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaSetter;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;

import java.util.function.Consumer;

/** the type of an Adama pair which is tightly coupled with map types */
public class TyNativePair extends TyType implements //
    AssignmentViaSetter, //
    DetailHasDeltaType, //
    DetailNativeDeclarationIsNotStandard {
  public final TyType domainType;
  public final TyType rangeType;
  private final Token readonlyToken;
  private final Token pairToken;
  private final Token beginToken;
  private final Token commaToken;
  private final Token endToken;

  public TyNativePair(final TypeBehavior behavior, final Token readonlyToken, final Token pairToken, final Token beginToken, final TyType domainType, final Token commaToken, final TyType rangeType, final Token endToken) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.pairToken = pairToken;
    this.beginToken = beginToken;
    this.domainType = domainType;
    this.commaToken = commaToken;
    this.rangeType = rangeType;
    this.endToken = endToken;
    ingest(pairToken);
    ingest(beginToken);
    ingest(endToken);
    ingest(domainType);
    ingest(rangeType);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(pairToken);
    yielder.accept(beginToken);
    domainType.emitInternal(yielder);
    yielder.accept(commaToken);
    rangeType.emitInternal(yielder);
    yielder.accept(endToken);
  }

  @Override
  public String getAdamaType() {
    return "pair<" + domainType.getAdamaType() + "," + rangeType.getAdamaType() + ">";
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    return getJavaConcreteType(environment);
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    TyType resolvedDomain = environment.rules.Resolve(domainType, false);
    TyType resolvedRange = environment.rules.Resolve(rangeType, false);
    return "NtPair<" + resolvedDomain.getJavaBoxType(environment) + "," + resolvedRange.getJavaBoxType(environment) + ">";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    return new TyNativePair(newBehavior, readonlyToken, pairToken, beginToken, domainType, commaToken, rangeType, endToken).withPosition(position);
  }

  @Override
  public void typing(Environment environment) {
    domainType.typing(environment);
    rangeType.typing(environment);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_pair");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("domain");
    domainType.writeTypeReflectionJson(writer);
    writer.writeObjectFieldIntro("range");
    rangeType.writeTypeReflectionJson(writer);
    writer.endObject();
  }

  public TyType getDomainType(final Environment environment) {
    return environment.rules.Resolve(domainType, false);
  }

  public TyType getRangeType(final Environment environment) {
    return environment.rules.Resolve(rangeType, false);
  }

  @Override
  public String getDeltaType(Environment environment) {
    return "DPair<" + ((DetailHasDeltaType) domainType).getDeltaType(environment) + "," + ((DetailHasDeltaType) rangeType).getDeltaType(environment) + ">";
  }

  @Override
  public String getPatternWhenValueProvided(Environment environment) {
    return "new NtPair(%s)";
  }

  @Override
  public String getStringWhenValueNotProvided(Environment environment) {
    return "new NtPair(" + domainType.getJavaDefaultValue(environment, this) + "," + rangeType.getJavaDefaultValue(environment, this) + ")";
  }
}
