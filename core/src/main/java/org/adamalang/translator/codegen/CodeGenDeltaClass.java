/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.codegen;

import java.util.ArrayList;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.privacy.PrivatePolicy;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.TyNativeMaybe;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;

/** generates the serialization code to emit deltas */
public class CodeGenDeltaClass {
  private static boolean canFieldBeSeenAtSomePoint(final FieldDefinition fd) {
    if (fd.policy == null || fd.policy instanceof PrivatePolicy) { return false; }
    return true;
  }

  private static void writeCommonConstructor(final ArrayList<FieldDefinition> fds, final ArrayList<FieldDefinition> bubbles, final StringBuilderWithTabs sb, final Environment environment, final String className) {
    sb.append("private boolean __emitted;").writeNewline();
    sb.append("private Delta").append(className).append("() {").tabUp().writeNewline();
    for (final FieldDefinition fd : fds) {
      if (fd.type instanceof TyReactiveLazy) {
        sb.append("__g").append(fd.name).append(" = -1;").writeNewline();
      }
      var fieldType = environment.rules.Resolve(fd.type, false);
      if (fieldType instanceof DetailComputeRequiresGet) {
        fieldType = ((DetailComputeRequiresGet) fieldType).typeAfterGet(environment);
      }
      final var deltaType = ((DetailHasDeltaType) fieldType).getDeltaType(environment);
      sb.append("__d").append(fd.name).append(" = new ").append(deltaType).append("();");
      sb.writeNewline();
    }
    for (final FieldDefinition fd : bubbles) {
      sb.append("__g").append(fd.name).append(" = -1;").writeNewline();
      var fieldType = environment.rules.Resolve(fd.type, false);
      if (fieldType instanceof DetailComputeRequiresGet) {
        fieldType = ((DetailComputeRequiresGet) fieldType).typeAfterGet(environment);
      }
      final var deltaType = ((DetailHasDeltaType) fieldType).getDeltaType(environment);
      sb.append("__d").append(fd.name).append(" = new ").append(deltaType).append("();");
      sb.writeNewline();
    }
    sb.append("__emitted = false;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  private static void writeCommonTrailer(final StringBuilderWithTabs sb) {
    sb.append("if (__obj.end()) {").tabUp().writeNewline();
    sb.append("__emitted = true;").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("public void hide(PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    sb.append("if (__emitted) {").tabUp().writeNewline();
    sb.append("__emitted = false;").writeNewline();
    sb.append("__writer.writeNull();").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeMessageDeltaClass(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final String className) {
    sb.append("private class Delta").append(className).append(" {").tabUp().writeNewline();
    for (final FieldDefinition fd : storage.fieldsByOrder) {
      final var fieldType = environment.rules.Resolve(fd.type, false);
      final var deltaType = ((DetailHasDeltaType) fieldType).getDeltaType(environment);
      sb.append("private ").append(deltaType).append(" __d").append(fd.name).append(";").writeNewline();
    }
    writeCommonConstructor(storage.fieldsByOrder, new ArrayList<>(), sb, environment, className);
    sb.append("public void show(").append(className).append(" __item, PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    final var cost = storage.fieldsByOrder.size();
    if (cost > 0 && !environment.state.hasNoCost()) {
      sb.append("__code_cost += ").append("" + cost).append(";").writeNewline();
    }
    sb.append("PrivateLazyDeltaWriter __obj = __writer.planObject();").writeNewline();
    for (final FieldDefinition fd : storage.fieldsByOrder) {
      final var fieldType = environment.rules.Resolve(fd.type, false);
      writeShowData(sb, "__d" + fd.name, "__item." + fd.name, fieldType, "__obj.planField(\"" + fd.name + "\")", environment, false);
    }
    writeCommonTrailer(sb);
  }

  public static void writeRecordDeltaClass(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final String className, final boolean forceManifest) {
    final var fds = new ArrayList<FieldDefinition>();
    sb.append("private class Delta").append(className).append(" {").tabUp().writeNewline();
    final var bubbles = new ArrayList<FieldDefinition>();
    for (final FieldDefinition fd : storage.fieldsByOrder) {
      var fieldType = environment.rules.Resolve(fd.type, false);
      if (canFieldBeSeenAtSomePoint(fd)) {
        if (fd.type instanceof TyReactiveLazy) {
          sb.append("private int __g").append(fd.name).append(";").writeNewline();
        }
        if (fieldType instanceof DetailComputeRequiresGet) {
          fieldType = ((DetailComputeRequiresGet) fieldType).typeAfterGet(environment);
        }
        sb.append("private ").append(((DetailHasDeltaType) fieldType).getDeltaType(environment)).append(" __d").append(fd.name).append(";").writeNewline();
        fds.add(fd);
      }
    }
    for (final BubbleDefinition bd : storage.bubbles.values()) {
      final var bubbleType = environment.rules.Resolve(bd.expressionType, false);
      sb.append("private long __g").append(bd.nameToken.text).append(";").writeNewline();
      sb.append("private ").append(((DetailHasDeltaType) bubbleType).getDeltaType(environment)).append(" __d").append(bd.nameToken.text).append(";").writeNewline();
      bubbles.add(FieldDefinition.invent(bd.expressionType, bd.nameToken.text));
    }
    writeCommonConstructor(fds, bubbles, sb, environment, className);
    sb.append("public void show(").append(className).append(" __item, PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    final var cost = fds.size() + storage.bubbles.size();
    if (cost > 0 && !environment.state.hasNoCost()) {
      sb.append("__code_cost += ").append("" + cost).append(";").writeNewline();
    }
    sb.append("PrivateLazyDeltaWriter __obj = __writer.planObject();").writeNewline();
    if (forceManifest) {
      sb.append("__obj.manifest();").writeNewline();
    }
    for (final FieldDefinition fd : fds) {
      final var isLazy = fd.type instanceof TyReactiveLazy;
      var fieldType = environment.rules.Resolve(fd.type, false);
      var addGet = false;
      if (fieldType instanceof DetailComputeRequiresGet) {
        addGet = true;
        fieldType = ((DetailComputeRequiresGet) fieldType).typeAfterGet(environment);
      }
      if (fieldType != null) {
        final var closeItUp = fd.policy.writePrivacyCheckGuard(sb, fd, environment);
        if (isLazy) {
          sb.append("if (__g").append(fd.name).append(" != __item.").append(fd.name).append(".getGeneration()) {").tabUp().writeNewline();
        }
        writeShowData(sb, "__d" + fd.name, "__item." + fd.name + (addGet ? ".get()" : ""), fieldType, "__obj.planField(\"" + fd.name + "\")", environment, false);
        if (isLazy) {
          sb.append("__g").append(fd.name).append(" = __item.").append(fd.name).append(".getGeneration();").tabDown().writeNewline();
          sb.append("}").writeNewline();
        }
        if (closeItUp) {
          sb.append("/* privacy check close up */").tabDown().writeNewline();
          sb.append("} else {").tabUp().writeNewline();
          if (isLazy) {
            sb.append("__g").append(fd.name).append(" = -1;").writeNewline();
          }
          sb.append("__d").append(fd.name).append(".hide(__obj.planField(\"").append(fd.name).append("\"));").tabDown().writeNewline();
          sb.append("}").writeNewline();
        }
      }
    }
    if (storage.bubbles.size() > 0) {
      sb.append("RTx__ViewerType __VIEWER = (RTx__ViewerType) __writer.viewerState;").writeNewline();
      sb.append("long __CHECK = 0;").writeNewline();
      for (final BubbleDefinition bd : storage.bubbles.values()) {
        sb.append("__CHECK = __item.___").append(bd.nameToken.text).append(".getGeneration() * 1662803L + __VIEWER.__DATA_GENERATION;").writeNewline();
        sb.append("if (__g").append(bd.nameToken.text).append(" != __CHECK)  {").tabUp().writeNewline();
        final var bubbleType = environment.rules.Resolve(bd.expressionType, false);
        sb.append(bubbleType.getJavaBoxType(environment)).append(" __local_").append(bd.nameToken.text).append(" = __item.__COMPUTE_").append(bd.nameToken.text).append("(__writer.who, __VIEWER);").writeNewline();
        writeShowData(sb, "__d" + bd.nameToken.text, "__local_" + bd.nameToken.text, bubbleType, "__obj.planField(\"" + bd.nameToken.text + "\")", environment, false);
        sb.append("__g").append(bd.nameToken.text).append(" = __CHECK;").tabDown().writeNewline();
        sb.append("}").writeNewline();
      }
    }
    writeCommonTrailer(sb);
  }

  private static void writeShowData(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType sourceType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    if (sourceType instanceof TySimpleNative || sourceType instanceof TyReactiveRecord || sourceType instanceof TyNativeMessage) {
      sb.append(deltaObject).append(".show(").append(sourceData).append(", ").append(targetObjectWriter).append(");");
      if (tabDown) {
        sb.tabDown();
      }
      sb.writeNewline();
    } else if (sourceType instanceof TyNativeList || sourceType instanceof TyNativeArray) {
      final var elementType = ((DetailContainsAnEmbeddedType) sourceType).getEmbeddedType(environment);
      if (elementType instanceof TyReactiveRecord) {
        writeShowDListRecords(sb, deltaObject, sourceData, elementType, targetObjectWriter, environment, tabDown);
      } else {
        writeShowDListNonRecord(sb, deltaObject, sourceData, elementType, targetObjectWriter, environment, tabDown);
      }
    } else if (sourceType instanceof TyNativeMaybe) {
      writeShowDMaybe(sb, deltaObject, sourceData, ((TyNativeMaybe) sourceType).getEmbeddedType(environment), targetObjectWriter, environment, tabDown);
    } else if (sourceType instanceof TyNativeMap) {
      writeShowDMap(sb, deltaObject, sourceData, ((TyNativeMap) sourceType).domainType, environment.rules.Resolve(((TyNativeMap) sourceType).rangeType, false), targetObjectWriter, environment, tabDown);
    } else if (sourceType instanceof TyReactiveMap) {
      boolean addGet = false;
      var walkRangeType = ((TyReactiveMap) sourceType).getRangeType(environment);
      var rangeType = walkRangeType;
      if (walkRangeType instanceof DetailComputeRequiresGet) {
        addGet = true;
        rangeType = ((DetailComputeRequiresGet) walkRangeType).typeAfterGet(environment);
      }
      writeShowDMapRx(sb, deltaObject, sourceData, ((TyReactiveMap) sourceType).getDomainType(environment), walkRangeType, rangeType, addGet, targetObjectWriter, environment, tabDown);
    }
  }

  private static void writeShowDListNonRecord(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment,
      final boolean tabDown) {
    final var elementDeltaType = ((DetailHasDeltaType) elementType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__list" + environment.autoVariable();
    final var dListCache = "__deltaList" + environment.autoVariable();
    final var childElementType = elementType.getJavaBoxType(environment);
    final var childElementVar = "__listElement" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    final var indexVar = "__index" + environment.autoVariable();
    sb.append("DList<").append(elementDeltaType).append("> ").append(dListCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("int ").append(indexVar).append(" = 0;").writeNewline();
    sb.append("for (").append(childElementType).append(" ").append(childElementVar).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(elementDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dListCache).append(".getPrior(").append(indexVar).append(", () -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowData(sb, childDeltaVar, childElementVar, elementType, listWriterVar + ".planField(" + indexVar + ")", environment, false);
    sb.append(indexVar).append("++;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append(dListCache).append(".rectify(").append(indexVar).append(", ").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDListRecords(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment,
      final boolean tabDown) {
    final var elementDeltaType = ((DetailHasDeltaType) elementType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__list" + environment.autoVariable();
    final var dListCache = "__deltaList" + environment.autoVariable();
    final var dListWalker = "__deltaListWalker" + environment.autoVariable();
    final var childElementType = elementType.getJavaBoxType(environment);
    final var childElementVar = "__listElement" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DRecordList<").append(elementDeltaType).append("> ").append(dListCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DRecordList<").append(elementDeltaType).append(">.Walk ").append(dListWalker).append(" = ").append(dListCache).append(".begin();").writeNewline();
    sb.append("for (").append(childElementType).append(" ").append(childElementVar).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(elementDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dListCache).append(".getPrior(").append(childElementVar).append(".__id(), () -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowData(sb, childDeltaVar, childElementVar, elementType, listWriterVar + ".planField(" + childElementVar + ".__id())", environment, false);
    sb.append(dListWalker).append(".next(").append(childElementVar).append(".__id());").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append(dListWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDMap(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType domainType, final TyType rangeType, final String targetObjectWriter, final Environment environment,
                                    final boolean tabDown) {
    final var domainBoxType = domainType.getJavaBoxType(environment);
    final var rangeDeltaType = ((DetailHasDeltaType) rangeType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__map" + environment.autoVariable();
    final var dMapCache = "__deltaMap" + environment.autoVariable();
    final var dMapCacheWalker = "__deltaMapWalker" + environment.autoVariable();
    final var entryType = "Map.Entry<" + domainType.getJavaBoxType(environment) + "," + rangeType.getJavaBoxType(environment) + ">";
    final var mapEntry = "__mapEntry" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaType).append("> ").append(dMapCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaType).append(">.Walk ").append(dMapCacheWalker).append(" = ").append(dMapCache).append(".begin();").writeNewline();
    sb.append("for (").append(entryType).append(" ").append(mapEntry).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(rangeDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dMapCacheWalker).append(".next(").append(mapEntry).append(".getKey(), () -> new ").append(rangeDeltaType).append("()").append(");").writeNewline();
    writeShowData(sb, childDeltaVar, mapEntry + ".getValue()", rangeType, listWriterVar + ".planField(\"\" + " + mapEntry + ".getKey())", environment, true);
    sb.append("}").writeNewline();
    sb.append(dMapCacheWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDMapRx(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType domainType, final TyType walkRangeType, final TyType getRangeType, boolean addGet, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var domainBoxType = domainType.getJavaBoxType(environment);
    final var rangeDeltaTypeGet = ((DetailHasDeltaType) getRangeType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__map" + environment.autoVariable();
    final var dMapCache = "__deltaMap" + environment.autoVariable();
    final var dMapCacheWalker = "__deltaMapWalker" + environment.autoVariable();
    final var entryTypeWalk = "Map.Entry<" + domainType.getJavaBoxType(environment) + "," + walkRangeType.getJavaBoxType(environment) + ">";
    final var mapEntryWalk = "__mapEntry" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaTypeGet).append("> ").append(dMapCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaTypeGet).append(">.Walk ").append(dMapCacheWalker).append(" = ").append(dMapCache).append(".begin();").writeNewline();
    sb.append("for (").append(entryTypeWalk).append(" ").append(mapEntryWalk).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(rangeDeltaTypeGet).append(" ").append(childDeltaVar).append(" = ").append(dMapCacheWalker).append(".next(").append(mapEntryWalk).append(".getKey(), () -> new ").append(rangeDeltaTypeGet).append("()").append(");").writeNewline();
    writeShowData(sb, childDeltaVar, mapEntryWalk + ".getValue()" + (addGet ? ".get()" : ""), getRangeType, listWriterVar + ".planField(\"\" + " + mapEntryWalk + ".getKey())", environment, true);
    sb.append("}").writeNewline();
    sb.append(dMapCacheWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDMaybe(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var elementDeltaType = ((DetailHasDeltaType) elementType).getDeltaType(environment);
    final var childElementType = elementType.getJavaBoxType(environment);
    final var childElementVar = "__maybeElement" + environment.autoVariable();
    final var deltaElementVar = "__maybeDeltaElement" + environment.autoVariable();
    sb.append("if (").append(sourceData).append(".has()) {").tabUp().writeNewline();
    sb.append(childElementType).append(" ").append(childElementVar).append(" = (").append(childElementType).append(")(").append(sourceData).append(".get());").writeNewline();
    sb.append(elementDeltaType).append(" ").append(deltaElementVar).append(" = ").append(deltaObject).append(".get(() -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowData(sb, deltaElementVar, childElementVar, elementType, targetObjectWriter, environment, true);
    sb.append("} else {").tabUp().writeNewline();
    sb.append(deltaObject).append(".hide(").append(targetObjectWriter).append(");").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }
}
