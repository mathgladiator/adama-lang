/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.natives.TyNativeStateMachineRef;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;

public class CodeGenMessage {
  public static void generateJsonReaders(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var localVar = new AtomicInteger();
    { // READ FROM STREAM
      sb.append("private RTx" + name + "(JsonStreamReader __reader) {").tabUp().writeNewline(); // UP
      if (storage.fields.size() == 0) {
        sb.append("__reader.skipValue();").tabDown().writeNewline();
      } else {
        sb.append("if (__reader.startObject()) {").tabUp().writeNewline(); // UP
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
      sb.append(name).append(" = ").append("__reader.read").append(type.getJavaBoxType(environment)).append("();").writeNewline();
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
      sb.append("if (__reader.startObject()) {").tabUp().writeNewline(); // UP
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
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else if (type instanceof TyNativeArray || type instanceof TyNativeList) {
      final var elementType = ((DetailContainsAnEmbeddedType) type).getEmbeddedType(environment);
      final var localArrayBuilder = "__localArray_" + localVar.getAndIncrement();
      final var localItem = "__localItem_" + localVar.getAndIncrement();
      final var localIndex = "__localIndex_" + localVar.getAndIncrement();
      sb.append("ArrayList<").append(elementType.getJavaBoxType(environment)).append("> ").append(localArrayBuilder).append(" = new ArrayList<>();").writeNewline();
      sb.append(elementType.getJavaConcreteType(environment)).append(" ").append(localItem).append(";").writeNewline();
      sb.append("__reader.startArray();").writeNewline();
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
      sb.append(name).append(".__writeOut(__writer);").writeNewline();
    } else if (type instanceof TyNativeMap) {
      final var domainType = ((TyNativeMap) type).getDomainType(environment);
      final var rangeType = ((TyNativeMap) type).getRangeType(environment);
      sb.append("__writer.beginObject();").writeNewline();
      final var localItem = "__localEntry_" + localVar.getAndIncrement();
      sb.append("for (Map.Entry<").append(domainType.getJavaBoxType(environment)).append(", ").append(rangeType.getJavaBoxType(environment)).append("> ").append(localItem).append(" : ").append(name).append(") {").tabUp().writeNewline();
      sb.append("__writer.writeObjectFieldIntro(").append(localItem).append(".getKey());").writeNewline();
      writeValueWriter(localItem + ".getValue()", rangeType, sb, environment, localVar, true);
      sb.append("}").writeNewline();
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
