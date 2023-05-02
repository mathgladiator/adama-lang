/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.codegen.CodeGenDeltaClass;
import org.adamalang.translator.codegen.CodeGenRecords;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeFunctionInternalFieldReplacement;
import org.adamalang.translator.tree.types.structures.DefineMethod;
import org.adamalang.translator.tree.types.structures.IndexDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsKillable;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;
import org.adamalang.translator.tree.types.traits.details.DetailTypeProducesRootLevelCode;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyReactiveRecord extends TyType implements //
    IsStructure, //
    DetailHasDeltaType, //
    IsKillable, //
    DetailTypeProducesRootLevelCode, //
    DetailTypeHasMethods {
  public String name;
  public Token nameToken;
  public Token recordToken;
  public StructureStorage storage;
  private boolean typedAlready;

  public TyReactiveRecord(final Token recordToken, final Token nameToken, final StructureStorage storage) {
    super(TypeBehavior.ReadWriteWithSetGet);
    this.recordToken = recordToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.storage = storage;
    ingest(recordToken);
    ingest(nameToken);
    ingest(storage);
    typedAlready = false;
  }

  @Override
  public void compile(final StringBuilderWithTabs sb, final Environment environment) {
    final var classFields = new StringBuilderWithTabs().tabUp().tabUp();
    final var classConstructor = new StringBuilderWithTabs().tabUp().tabUp().tabUp();
    CodeGenRecords.writeCommonBetweenRecordAndRoot(storage, classConstructor, classFields, environment.scope(), true);
    classConstructor.append("if (__owner instanceof RxTable) {").tabUp().writeNewline();
    var colNum = 0;
    for (final IndexDefinition idefn : storage.indices) {
      classFields.append("private final ReactiveIndexInvalidator __INDEX_").append(idefn.nameToken.text).append(";").writeNewline();
      classConstructor.append("__INDEX_").append(idefn.nameToken.text).append(" = new ReactiveIndexInvalidator(((RxTable<RTx").append(name).append(">)(__owner)).getIndex((short)").append("" + colNum).append("), this) {").tabUp().tabUp().writeNewline();
      classConstructor.append("@Override").writeNewline();
      classConstructor.append("public int pullValue() {").tabUp().writeNewline();
      classConstructor.append("  return ").append(idefn.nameToken.text).append(".get()");
      final var fd = storage.fields.get(idefn.nameToken.text);
      if (fd != null) {
        if (fd.type instanceof TyReactivePrincipal) {
          classConstructor.append(".hashCode()");
        }
        if (fd.type instanceof TyReactiveDate || fd.type instanceof TyReactiveTime) {
          classConstructor.append(".toInt()");
        }
      }
      classConstructor.append(";").tabDown().writeNewline();
      classConstructor.append("}").tabDown().writeNewline();
      classConstructor.append("};").tabDown().writeNewline();
      classConstructor.append(idefn.nameToken.text).append(".__subscribe(__INDEX_").append(idefn.nameToken.text).append(");").writeNewline();
      colNum++;
    }
    classConstructor.append("/* ok */").tabDown().writeNewline();
    classConstructor.append("} else {").tabUp().writeNewline();
    for (final IndexDefinition idefn : storage.indices) {
      classConstructor.append("__INDEX_").append(idefn.nameToken.text).append(" = null;").writeNewline();
    }
    classConstructor.append("/* ok */").tabDown().writeNewline();
    classConstructor.append("}").writeNewline();
    CodeGenRecords.writeIndexConstant(name, storage, sb, environment);
    sb.append("private class RTx" + name + " extends RxRecordBase<RTx").append(name).append("> {").tabUp().writeNewline();
    sb.append(classFields.toString());
    sb.append("private RTx" + name + "(RxParent __owner) {");
    final var classConstructorStripped = classConstructor.toString().stripTrailing();
    sb.tabUp().writeNewline();
    sb.append(classConstructorStripped);
    sb.append("").tabDown().writeNewline().append("}").writeNewline();
    for (final DefineMethod dm : storage.methods) {
      dm.writeFunctionJava(sb, environment);
    }
    CodeGenRecords.writePrivacyCommonBetweenRecordAndRoot(storage, sb, environment);
    CodeGenRecords.writeIndices(name, storage, sb, environment);
    CodeGenRecords.writeCommitAndRevert(storage, sb, environment, false);
    sb.append("@Override").writeNewline();
    sb.append("public String __name() {").tabUp().writeNewline();
    sb.append("return \"").append(name).append("\";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void __deindex() {").tabUp().writeNewline();
    for (final IndexDefinition idefn : storage.indices) {
      sb.append("__INDEX_").append(idefn.nameToken.text).append(".deindex();").writeNewline();
    }
    sb.append("/* ok */").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("public void __reindex() {").tabUp().writeNewline();
    for (final IndexDefinition idefn : storage.indices) {
      sb.append("__INDEX_").append(idefn.nameToken.text).append(".reindex();").writeNewline();
    }
    sb.append("/* ok */").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public int __id() {").tabUp().writeNewline();
    sb.append("return id.get();").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void __setId(int __id, boolean __force) {").tabUp().writeNewline();
    sb.append("if (__force) {").tabUp().writeNewline();
    sb.append("id.forceSet(__id);").tabDown().writeNewline();
    sb.append("} else {").tabUp().writeNewline();
    sb.append("id.set(__id);").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    CodeGenDeltaClass.writeRecordDeltaClass(storage, sb, environment, "RTx" + name, false);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    yielder.accept(recordToken);
    yielder.accept(nameToken);
    storage.emit(yielder);
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
    return new TyReactiveRecord(recordToken, nameToken, storage).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    if (typedAlready) {
      return;
    }
    final var fdId = storage.fields.get("id");
    if (fdId == null || !(fdId.type instanceof TyReactiveInteger)) {
      environment.document.createError(this, "id must be type int", "Record");
    }
    typedAlready = true;
    storage.typing(environment.scope());
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("reactive_record");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("name");
    writer.writeString(name);
    writer.writeObjectFieldIntro("fields");
    storage.writeTypeReflectionJson(writer);
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DeltaRTx" + name;
  }

  @Override
  public TyNativeFunctional lookupMethod(final String name, final Environment environment) {
    if (!environment.state.isPure()) {
      if ("delete".equals(name) && storage.specialization == StorageSpecialization.Record) {
        return new TyNativeFunctionInternalFieldReplacement("__delete", FunctionOverloadInstance.WRAP(new FunctionOverloadInstance("__delete", null, new ArrayList<>(), false, false, false)), FunctionStyleJava.ExpressionThenArgs);
      }
      return storage.methodTypes.get(name);
    }
    return null;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public StructureStorage storage() {
    return storage;
  }
}
