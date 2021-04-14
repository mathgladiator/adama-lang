/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.codegen.CodeGenDeltaClass;
import org.adamalang.translator.codegen.CodeGenMessage;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;
import org.adamalang.translator.tree.types.traits.details.DetailNativeDeclarationIsNotStandard;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

public class TyNativeMessage extends TyType implements IsStructure, //
    DetailTypeProducesRootLevelCode, //
    DetailHasDeltaType, //
    DetailInventDefaultValueExpression, //
    CanBeNativeArray, //
    DetailNativeDeclarationIsNotStandard, //
    AssignmentViaNativeOnlyForSet {
  public Token messageToken;
  public String name;
  public Token nameToken;
  public StructureStorage storage;

  public TyNativeMessage(final TypeBehavior behavior, final Token messageToken, final Token nameToken, final StructureStorage storage) {
    super(behavior);
    this.messageToken = messageToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.storage = storage;
    ingest(messageToken);
    ingest(storage);
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    // a COPY of fields to preserver order
    final var fields = new ArrayList<FieldDefinition>();
    for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
      fields.add(e.getValue());
    }
    sb.append("private static class RTx" + name + " implements NtMessageBase {").tabUp().writeNewline();
    for (final FieldDefinition fd : fields) {
      sb.append("private ").append(fd.type.getJavaConcreteType(environment)).append(" ").append(fd.name);
      final var fieldType = environment.rules.Resolve(fd.type, false);
      if (fieldType instanceof DetailNativeDeclarationIsNotStandard) {
        sb.append(" = ").append(((DetailNativeDeclarationIsNotStandard) fieldType).getStringWhenValueNotProvided(environment));
      } else {
        Expression defaultValueToUse = null;
        if (fieldType instanceof DetailInventDefaultValueExpression) {
          defaultValueToUse = ((DetailInventDefaultValueExpression) fieldType).inventDefaultValueExpression(this);
        }
        if (fd.defaultValueOverride != null) {
          defaultValueToUse = fd.defaultValueOverride;
        }
        if (defaultValueToUse != null) {
          sb.append(" = ");
          defaultValueToUse.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        }
      }
      sb.append(";").writeNewline();
    }
    CodeGenMessage.generateJsonReaders(name, storage, sb, environment);
    { // CLOSE UP THE MESSAGE WITH A CONSTRUCTOR FOR ANONYMOUS OBJECTS
      sb.append("private RTx" + name + "() {}");
      if (storage.fields.size() == 0) {
        sb.tabDown().writeNewline();
      } else {
        sb.writeNewline();
        sb.append("private RTx").append(name).append("(");
        var firstArg = true;
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          if (!firstArg) {
            sb.append(", ");
          }
          firstArg = false;
          sb.append(e.getValue().type.getJavaConcreteType(environment)).append(" ").append(e.getKey());
        }
        sb.append(") {").tabUp().writeNewline();
        var countDownUntilTab = storage.fields.size();
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          sb.append("this.").append(e.getKey()).append(" = ").append(e.getKey()).append(";");
          if (--countDownUntilTab <= 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").tabDown().writeNewline();
      }
      sb.append("}").writeNewline();
    }
    CodeGenDeltaClass.writeMessageDeltaClass(storage, sb, environment, "RTx" + name);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(messageToken);
    yielder.accept(nameToken);
    storage.emit(yielder);
  }

  @Override
  public String getAdamaType() {
    return name;
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DeltaRTx" + name;
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    return String.format("RTx%s", name);
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    return String.format("RTx%s", name);
  }

  @Override
  public String getPatternWhenValueProvided(final Environment environment) {
    return "%s";
  }

  @Override
  public String getStringWhenValueNotProvided(final Environment environment) {
    return "new RTx" + name + "()";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new InjectExpression(this) {
      @Override
      public void writeJava(final StringBuilder sb, final Environment environment) {
        sb.append("new RTx").append(name).append("()");
      }
    };
  }

  public TyNativeMessage makeAnonymousCopy() {
    return (TyNativeMessage) (new TyNativeMessage(behavior, messageToken, nameToken, storage.makeAnonymousCopy()).withPosition(this));
  }

  @Override
  public TyType makeCopyWithNewPosition(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeMessage(newBehavior, messageToken, nameToken, storage).withPosition(position);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public StructureStorage storage() {
    return storage;
  }

  @Override
  public void typing(final Environment environment) {
    storage.typing(environment.scope());
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_message");
    writer.writeObjectFieldIntro("name");
    writer.writeString(name);
    writer.writeObjectFieldIntro("anonymous");
    writer.writeBoolean(storage.anonymous);
    writer.writeObjectFieldIntro("fields");
    storage.writeTypeReflectionJson(writer);
    writer.endObject();
  }
}
