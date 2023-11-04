/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.codegen.CodeGenDeltaClass;
import org.adamalang.translator.codegen.CodeGenIndexing;
import org.adamalang.translator.codegen.CodeGenMessage;
import org.adamalang.translator.codegen.CodeGenRecords;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.structures.DefineMethod;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNativeOnlyForSet;
import org.adamalang.translator.tree.types.traits.details.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class TyNativeMessage extends TyType implements //
    IsStructure, //
    DetailTypeProducesRootLevelCode, //
    DetailHasDeltaType, //
    DetailInventDefaultValueExpression, //
    CanBeNativeArray, //
    DetailTypeHasMethods, //
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

  public boolean hasUniqueId() {
    FieldDefinition fd = storage.fields.get("id");
    if (fd != null) {
      return fd.uniqueToken != null;
    }
    return false;
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    // a COPY of fields to preserver order
    final var fields = new ArrayList<FieldDefinition>();
    for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
      fields.add(e.getValue());
    }
    sb.append("private static class RTx" + name + " extends NtMessageBase {").tabUp().writeNewline();
    sb.append("private final RTx" + name + " __this;").writeNewline();
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
    CodeGenMessage.generateHashers(name, storage, sb, environment);
    CodeGenIndexing.writeIndexConstant(name, storage, sb, environment);
    CodeGenIndexing.writeIndices(name, storage, sb, environment);
    CodeGenMessage.generateJsonReaders(name, storage, sb, environment);
    for (final DefineMethod dm : storage.methods) {
      dm.writeFunctionJava(sb, environment.scopeStatic());
    }
    { // CLOSE UP THE MESSAGE WITH A CONSTRUCTOR FOR ANONYMOUS OBJECTS
      sb.append("private RTx" + name + "() { __this = this; }");
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
        sb.append("this.__this = this;").writeNewline();
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
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(messageToken);
    yielder.accept(nameToken);
    storage.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    storage.format(formatter);
  }

  @Override
  public String getAdamaType() {
    return name;
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
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeMessage(newBehavior, messageToken, nameToken, storage).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
  }

  @Override
  public void typing(TypeCheckerRoot checker) {
    storage.typing(name, checker);

    checker.register(Collections.emptySet(), (env) -> {
      for (FieldDefinition fd : storage.fieldsByOrder) {
        TyType resolved = env.rules.Resolve(fd.type, false);
        if (resolved instanceof IsReactiveValue || resolved instanceof TyReactiveRecord || resolved instanceof TyReactiveTable || resolved instanceof TyNativeTable) {
          env.document.createError(TyNativeMessage.this, String.format("Messages can't have a field type of '%s'", resolved.getAdamaType()));
        }
      }
    });
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    if (source == ReflectionSource.Root) {
      writer.beginObject();
      writer.writeObjectFieldIntro("nature");
      writer.writeString("native_message");
      writeAnnotations(writer);
      writer.writeObjectFieldIntro("name");
      writer.writeString(name);
      writer.writeObjectFieldIntro("anonymous");
      writer.writeBoolean(storage.anonymous);
      writer.writeObjectFieldIntro("fields");
      storage.writeTypeReflectionJson(writer);
      writer.endObject();
    } else {
      writer.beginObject();
      writer.writeObjectFieldIntro("nature");
      writer.writeString("native_ref");
      writeAnnotations(writer);
      writer.writeObjectFieldIntro("ref");
      writer.writeString(name);
      writer.endObject();
    }
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DeltaRTx" + name;
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
  public String name() {
    return name;
  }

  @Override
  public StructureStorage storage() {
    return storage;
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    if ("to_dynamic".equals(name)) {
      return new TyNativeFunctional("to_dynamic", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("to_dynamic", new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null), new ArrayList<>(), FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    if ("ingest_dynamic".equals(name)) {
      ArrayList<TyType> args = new ArrayList<>();
      args.add(new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null));
      return new TyNativeFunctional("ingest_dynamic", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("ingest_dynamic", new TyNativeVoid(), args, FunctionPaint.READONLY_NORMAL)), FunctionStyleJava.ExpressionThenArgs);
    }
    return storage.methodTypes.get(name);
  }
}
