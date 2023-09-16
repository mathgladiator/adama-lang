/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenMessage {
  public static void generateHashers(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    AtomicInteger localVar = new AtomicInteger(1);
    sb.append("public void __hash(HashBuilder __hash) {").tabUp().writeNewline();
    for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
      sb.append("__hash.hashString(\"").append(e.getKey()).append("\");").writeNewline();
      writeValueHasher("this." + e.getKey(), environment.rules.Resolve(e.getValue().type, false), sb, environment, localVar, false);
    }
    if (storage.anonymous) {
      sb.append("__hash.hashString(\"anonymous\");").tabDown().writeNewline();
    } else {
      sb.append("__hash.hashString(\"").append(name).append("\");").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();
  }

  public static void writeValueHasher(final String expression, final TyType type, final StringBuilderWithTabs sb, final Environment environment, final AtomicInteger localVar, boolean tabDown) {
    if (type instanceof TySimpleNative) {
      sb.append("__hash.hash").append(type.getJavaBoxType(environment)).append("(").append(expression).append(");");
    } else if (type instanceof TyNativeMaybe) {
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      sb.append("if (").append(expression).append(".has()) {").tabUp().writeNewline();
      writeValueHasher(expression + ".get()", environment.rules.Resolve(elementType, false), sb, environment, localVar, true);
      sb.append("}");
    } else if (type instanceof TyNativeMessage) {
      sb.append(expression).append(".__hash(__hash);");
    } else if (type instanceof TyNativeMap) {
      TyType domainType = environment.rules.Resolve(((TyNativeMap) type).domainType, true);
      TyType rangeType = environment.rules.Resolve(((TyNativeMap) type).rangeType, true);
      final var entryKey = "__entry" + localVar.getAndIncrement();
      sb.append("for (NtPair<").append(domainType.getJavaBoxType(environment)).append(",").append(rangeType.getJavaBoxType(environment)).append("> ").append(entryKey).append(" : ").append(expression).append(") {").tabUp().writeNewline();
      writeValueHasher(entryKey + ".key", domainType, sb, environment, localVar, false);
      writeValueHasher(entryKey + ".value", rangeType, sb, environment, localVar, true);
      sb.append("}");
    } else if (type instanceof TyNativePair) {
      TyType domainType = environment.rules.Resolve(((TyNativePair) type).domainType, true);
      TyType rangeType = environment.rules.Resolve(((TyNativePair) type).rangeType, true);
      final var localCpy = "__cpy" + localVar.getAndIncrement();
      sb.append("NtPair<").append(domainType.getJavaBoxType(environment)).append(",").append(rangeType.getJavaBoxType(environment)).append("> ").append(localCpy).append(" = ").append(expression).append(";").writeNewline();
      writeValueHasher(localCpy + ".key", domainType, sb, environment, localVar, false);
      writeValueHasher(localCpy + ".value", rangeType, sb, environment, localVar, tabDown);
      return;
    } else if (type instanceof TyNativeArray || type instanceof TyNativeList) {
      final var itemVar = "__item" + localVar.getAndIncrement();
      TyType itemType = environment.rules.Resolve(((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment), true);
      sb.append("for (").append(itemType.getJavaBoxType(environment)).append(" ").append(itemVar).append(" : ").append(expression).append(") {").tabUp().writeNewline();
      writeValueHasher(itemVar, itemType, sb, environment, localVar, true);
      sb.append("}");
    }
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  public static void generateJsonReaders(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var localVar = new AtomicInteger();
    { // READ FROM STREAM
      sb.append("private RTx" + name + "(JsonStreamReader __reader) {").tabUp().writeNewline(); // UP
      sb.append("__this = this;").writeNewline();
      sb.append("__ingest(__reader);").tabDown().writeNewline();
      sb.append("}").writeNewline();
      boolean isViewerType = name.equals("__ViewerType");
      if (isViewerType) {
        sb.append("public int __DATA_GENERATION = 1;").writeNewline();
      }
      sb.append("@Override").writeNewline();
      sb.append("public void __ingest(JsonStreamReader __reader) {").tabUp().writeNewline(); // UP
      if (storage.fields.size() == 0) {
        sb.append("__reader.mustSkipObject();").tabDown().writeNewline();
      } else {
        if (isViewerType) {
          sb.append("this.__DATA_GENERATION += 2;").writeNewline();
        }
        sb.append("__reader.mustStartObject();").writeNewline();
        sb.append("while (__reader.notEndOfObject()) {").tabUp().writeNewline(); // UP
        sb.append("String __fieldName = __reader.fieldName();").writeNewline();
        sb.append("switch (__fieldName) {").tabUp().writeNewline(); // UP
        for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
          sb.append("case \"").append(e.getKey()).append("\":").tabUp().writeNewline(); // UP
          writeValueReader("this." + e.getKey(), environment.rules.Resolve(e.getValue().type, false), sb, environment, localVar);
          sb.append("break;").tabDown().writeNewline(); // DOWN
        }
        sb.append("default:").tabUp().writeNewline(); // UP
        sb.append("__reader.skipValue();").tabDown().tabDown().writeNewline(); // DOWN
        sb.append("}").tabDown().writeNewline(); // DOWN
        sb.append("}").tabDown().writeNewline();
      }
      sb.append("}").writeNewline();
    }
    { // WRITE TO STREAM
      sb.append("@Override").writeNewline();
      sb.append("public void __writeOut(JsonStreamWriter __writer) {").tabUp().writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      for (final Map.Entry<String, FieldDefinition> e : storage.fields.entrySet()) {
        sb.append("__writer.writeObjectFieldIntro(\"").append(e.getKey()).append("\");").writeNewline();
        writeValueWriter(e.getKey(), e.getValue().type, sb, environment, localVar, false);
      }
      sb.append("__writer.endObject();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }

  public static void writeValueReader(final String name, final TyType type, final StringBuilderWithTabs sb, final Environment environment, final AtomicInteger localVar) {
    if (type instanceof TySimpleNative) {
      if (type instanceof TyNativeEnum) {
        String enumName = ((TyNativeEnum) type).name;
        sb.append(name).append(" = ").append("__EnumFix_").append(enumName).append("(__reader.read").append(type.getJavaBoxType(environment)).append("());").writeNewline();
      } else {
        sb.append(name).append(" = ").append("__reader.read").append(type.getJavaBoxType(environment)).append("();").writeNewline();
      }
    } else if (type instanceof TyNativeMaybe) {
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      final var localItem = "__localItem_" + localVar.getAndIncrement();
      sb.append("if (__reader.testLackOfNull()) {").tabUp().writeNewline();
      sb.append(elementType.getJavaConcreteType(environment)).append(" ").append(localItem).append(";").writeNewline();
      writeValueReader(localItem, elementType, sb, environment, localVar);
      sb.append(name).append(".set(").append(localItem).append(");").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else if (type instanceof TyNativeMessage) {
      sb.append(name).append(" = new RTx").append(((TyNativeMessage) type).nameToken.text).append("(__reader);").writeNewline();
    } else if (type instanceof TyNativeMap) {
      final var keyValueRaw = "__keyRaw" + localVar.getAndIncrement();
      final var localValue = "__localValue_" + localVar.getAndIncrement();
      final var domainType = ((TyNativeMap) type).getDomainType(environment);
      final var rangeType = ((TyNativeMap) type).getRangeType(environment);
      sb.append("__reader.mustStartObject();").writeNewline();
      sb.append("while (__reader.notEndOfObject()) {").tabUp().writeNewline(); // UP
      if (domainType instanceof TyNativeInteger) {
        sb.append("int ").append(keyValueRaw).append(" = Integer.parseInt(__reader.fieldName());").writeNewline();
      } else if (domainType instanceof TyNativeLong) {
        sb.append("long ").append(keyValueRaw).append(" = Long.parseLong(__reader.fieldName());").writeNewline();
      } else if (domainType instanceof TyNativeString) {
        sb.append("String ").append(keyValueRaw).append(" = __reader.fieldName();").writeNewline();
      }
      sb.append(rangeType.getJavaConcreteType(environment)).append(" ").append(localValue).append(";").writeNewline();
      writeValueReader(localValue, rangeType, sb, environment, localVar);
      sb.append(name).append(".put(").append(keyValueRaw).append(", ").append(localValue).append(");").tabDown().writeNewline();
      sb.append("}").writeNewline();

    } else if (type instanceof TyNativePair) {
      sb.append("{").tabUp().writeNewline();
      final var domainType = ((TyNativePair) type).getDomainType(environment);
      final var rangeType = ((TyNativePair) type).getRangeType(environment);
      final var keyValue = "__key" + localVar.getAndIncrement();
      final var valueValue = "__value" + localVar.getAndIncrement();
      sb.append(domainType.getJavaBoxType(environment)).append(" ").append(keyValue).append(" = ").append(domainType.getJavaDefaultValue(environment, type)).append(";").writeNewline();
      sb.append(rangeType.getJavaBoxType(environment)).append(" ").append(valueValue).append(" = ").append(rangeType.getJavaDefaultValue(environment, type)).append(";").writeNewline();
      sb.append("__reader.mustStartObject();").writeNewline();
      sb.append("while (__reader.notEndOfObject()) {").tabUp().writeNewline(); // UP
      sb.append("switch (__reader.fieldName()) {").tabUp().writeNewline(); // UP
      sb.append("case \"key\":").tabUp().writeNewline();
      writeValueReader(keyValue, domainType, sb, environment, localVar);
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"value\":").tabUp().writeNewline();
      writeValueReader(valueValue, rangeType, sb, environment, localVar);
      sb.append("break;").tabDown().writeNewline();
      sb.append("default:").tabUp().writeNewline();
      sb.append("__reader.skipValue();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline(); // DOWN
      sb.append("}").tabDown().writeNewline(); // DOWN
      sb.append(name).append(" = new NtPair<>(").append(keyValue).append(", ").append(valueValue).append(");").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else if (type instanceof TyNativeArray || type instanceof TyNativeList) {
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      final var localArrayBuilder = "__localArray_" + localVar.getAndIncrement();
      final var localItem = "__localItem_" + localVar.getAndIncrement();
      final var localIndex = "__localIndex_" + localVar.getAndIncrement();
      sb.append("ArrayList<").append(elementType.getJavaBoxType(environment)).append("> ").append(localArrayBuilder).append(" = new ArrayList<>();").writeNewline();
      sb.append(elementType.getJavaConcreteType(environment)).append(" ").append(localItem).append(";").writeNewline();
      sb.append("__reader.mustStartArray();").writeNewline();
      sb.append("while (__reader.notEndOfArray()) {").tabUp().writeNewline();
      writeValueReader(localItem, elementType, sb, environment, localVar);
      sb.append(localArrayBuilder).append(".add(").append(localItem).append(");").tabDown().writeNewline();
      sb.append("}").writeNewline();
      if (type instanceof TyNativeArray) {
        sb.append(name).append(" = new ").append(elementType.getJavaConcreteType(environment)).append("[").append(localArrayBuilder).append(".size()];").writeNewline();
        sb.append("for (int ").append(localIndex).append(" = 0; ").append(localIndex).append(" < ").append(name).append(".length; ").append(localIndex).append("++) {").tabUp().writeNewline();
        sb.append(name).append("[").append(localIndex).append("] = ").append(localArrayBuilder).append(".get(").append(localIndex).append(");").tabDown().writeNewline();
        sb.append("}").writeNewline();
      } else {
        sb.append(name).append(" = new ArrayNtList<").append(elementType.getJavaBoxType(environment)).append(">(").append(localArrayBuilder).append(");").writeNewline();
      }
    }
  }

  public static void writeValueWriter(final String name, final TyType type, final StringBuilderWithTabs sb, final Environment environment, final AtomicInteger localVar, final boolean tabDown) {
    if (type instanceof TySimpleNative) {
      var suffix = type.getJavaBoxType(environment);
      if (type instanceof TyNativeStateMachineRef) {
        suffix = "FastString";
      }
      sb.append("__writer.write").append(suffix).append("(").append(name).append(");");
    } else if (type instanceof TyNativeMaybe) {
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      sb.append("if (").append(name).append(".has()) {").tabUp().writeNewline();
      writeValueWriter(name + ".get()", elementType, sb, environment, localVar, true);
      sb.append("} else {").tabUp().writeNewline();
      sb.append("__writer.writeNull();").tabDown().writeNewline();
      sb.append("}");
    } else if (type instanceof TyNativeMessage) {
      sb.append(name).append(".__writeOut(__writer);");
    } else if (type instanceof TyNativeMap) {
      final var domainType = ((TyNativeMap) type).getDomainType(environment);
      final var rangeType = ((TyNativeMap) type).getRangeType(environment);
      sb.append("__writer.beginObject();").writeNewline();
      final var localItem = "__localEntry_" + localVar.getAndIncrement();
      sb.append("for (NtPair<").append(domainType.getJavaBoxType(environment)).append(",").append(rangeType.getJavaBoxType(environment)).append("> ").append(localItem).append(" : ").append(name).append(") {").tabUp().writeNewline();
      sb.append("__writer.writeObjectFieldIntro(").append(localItem).append(".key);").writeNewline();
      writeValueWriter(localItem + ".value", rangeType, sb, environment, localVar, true);
      sb.append("}").writeNewline();
      sb.append("__writer.endObject();");
    } else if (type instanceof TyNativePair) {
      final var domainType = ((TyNativePair) type).getDomainType(environment);
      final var rangeType = ((TyNativePair) type).getRangeType(environment);
      final var localItem = "__cpy" + localVar.getAndIncrement();
      sb.append("NtPair<").append(domainType.getJavaBoxType(environment)).append(",").append(rangeType.getJavaBoxType(environment)).append("> ").append(localItem).append(" = ").append(name).append(";").writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      sb.append("__writer.writeObjectFieldIntro(\"key\");").writeNewline();
      writeValueWriter(localItem + ".key", domainType, sb, environment, localVar, false);
      sb.append("__writer.writeObjectFieldIntro(\"value\");").writeNewline();
      writeValueWriter(localItem + ".value", rangeType, sb, environment, localVar, false);
      sb.append("__writer.endObject();");
    } else if (type instanceof TyNativeArray || type instanceof TyNativeList) {
      sb.append("__writer.beginArray();").writeNewline();
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      final var localItem = "__localItem_" + localVar.getAndIncrement();
      sb.append("for (").append(elementType.getJavaConcreteType(environment)).append(" ").append(localItem).append(" : ").append(name).append(") {").tabUp().writeNewline();
      writeValueWriter(localItem, elementType, sb, environment, localVar, true);
      sb.append("}");
      sb.append("__writer.endArray();");
    }
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }
}
