/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.expressions;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.privacy.PrivatePolicy;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.SupportsTwoPhaseTyping;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;

/** an anonymous is an instance of an object that has no defined type.
 * <p>
 * For instance {x:1, y:0} is anonymous in that no type is defined, and yet if
 * <p>
 * message Point { int x; int y; } existed then the type Point should be
 * used. */
public class AnonymousObject extends Expression implements SupportsTwoPhaseTyping {
  public class RawField {
    public final Token colonToken;
    public final Token commaToken;
    public final Expression expression;
    public final Token fieldToken;

    public RawField(final Token commaToken, final Token fieldToken, final Token colonToken, final Expression expression) {
      this.commaToken = commaToken;
      this.fieldToken = fieldToken;
      this.colonToken = colonToken;
      this.expression = expression;
    }
  }

  public Token closeBraceToken;
  public final TreeMap<String, Expression> fields;
  public final Token openBraceToken;
  public final ArrayList<RawField> rawFields;

  public AnonymousObject(final Token openBraceToken) {
    this.openBraceToken = openBraceToken;
    fields = new TreeMap<>();
    rawFields = new ArrayList<>();
    ingest(openBraceToken);
  }

  /** add a field to this object */
  public void add(final Token commaToken, final Token fieldToken, final Token colonToken, final Expression expression) {
    rawFields.add(new RawField(commaToken, fieldToken, colonToken, expression));
    fields.put(fieldToken.text, expression);
    ingest(commaToken);
    ingest(expression);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(openBraceToken);
    for (final RawField rf : rawFields) {
      if (rf.commaToken != null) {
        yielder.accept(rf.commaToken);
      }
      yielder.accept(rf.fieldToken);
      if (rf.colonToken != null) {
        yielder.accept(rf.colonToken);
        rf.expression.emit(yielder);
      }
    }
    yielder.accept(closeBraceToken);
  }

  public void end(final Token closeBraceToken) {
    this.closeBraceToken = closeBraceToken;
    ingest(closeBraceToken);
  }

  @Override
  public TyType estimateType(final Environment environment) {
    environment.mustBeComputeContext(this);
    final var storage = new StructureStorage(StorageSpecialization.Message, true, null);
    for (final Map.Entry<String, Expression> entry : fields.entrySet()) {
      final var type = entry.getValue() instanceof SupportsTwoPhaseTyping ? ((SupportsTwoPhaseTyping) entry.getValue()).estimateType(environment) : entry.getValue().typing(environment, null);
      final var p = new PrivatePolicy(null);
      p.ingest(entry.getValue());
      final var fd = FieldDefinition.invent(type, entry.getKey());
      storage.add(fd);
    }
    return new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("_AnonObjConvert_" + environment.autoVariable()), storage).withPosition(this);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    if (suggestion != null) {
      var theType = environment.rules.GetMaxType(suggestion, estimateType(environment), false);
      theType = environment.rules.EnsureRegisteredAndDedupe(theType, false);
      if (theType != null) {
        upgradeType(environment, theType);
        return theType.makeCopyWithNewPosition(this, theType.behavior);
      }
    }
    var typeToUse = estimateType(environment);
    typeToUse = environment.rules.EnsureRegisteredAndDedupe(typeToUse, false);
    upgradeType(environment, typeToUse);
    return typeToUse;
  }

  @Override
  public void upgradeType(final Environment environment, final TyType newType) {
    cachedType = newType.withPosition(this);
    if (environment.rules.IsNativeMessage(newType, false)) {
      final var other = (TyNativeMessage) newType;
      for (final Map.Entry<String, FieldDefinition> otherField : other.storage().fields.entrySet()) {
        var myField = fields.get(otherField.getKey());
        final var otherFieldType = otherField.getValue().type;
        if (myField == null) {
          if (otherFieldType != null) {
            final var newValue = ((DetailInventDefaultValueExpression) otherFieldType).inventDefaultValueExpression(this);
            fields.put(otherField.getKey(), newValue);
          }
        } else {
          if (otherFieldType instanceof TyNativeMaybe && !(myField.typing(environment, null) instanceof TyNativeMaybe)) {
            if (myField instanceof SupportsTwoPhaseTyping) {
              ((SupportsTwoPhaseTyping) myField).upgradeType(environment, ((TyNativeMaybe) otherFieldType).tokenElementType.item);
            }
            myField = new MaybeLift(null, null, null, myField, null);
            myField.typing(environment, otherFieldType);
            fields.put(otherField.getKey(), myField);
          } else {
            if (myField instanceof SupportsTwoPhaseTyping) {
              ((SupportsTwoPhaseTyping) myField).upgradeType(environment, otherFieldType);
            }
          }
        }
      }
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var me = (TyNativeMessage) cachedType;
    if (me != null) {
      sb.append("new RTx" + me.name + "(");
      var first = true;
      for (final Expression expression : fields.values()) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        expression.writeJava(sb, environment);
      }
      sb.append(")");
    }
  }
}
