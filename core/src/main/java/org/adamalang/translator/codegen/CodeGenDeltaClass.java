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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.privacy.PrivatePolicy;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.reactive.TyReactiveText;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;

import java.util.ArrayList;

/** generates the serialization code to emit deltas */
public class CodeGenDeltaClass {
  private static boolean canFieldBeSeenAtSomePoint(final FieldDefinition fd) {
    return fd.policy != null && !(fd.policy instanceof PrivatePolicy);
  }

  private static void writeCommonConstructorAndCost(final ArrayList<FieldDefinition> fds, final ArrayList<FieldDefinition> bubbles, final StringBuilderWithTabs sb, final Environment environment, final String className) {
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
    sb.append("@Override").writeNewline();
    sb.append("public long __memory() {").tabUp().writeNewline();
    sb.append("long __sum = 40;").writeNewline();
    for (final FieldDefinition fd : fds) {
      sb.append("__sum += __d").append(fd.name).append(".__memory();").writeNewline();
    }
    for (final FieldDefinition fd : bubbles) {
      sb.append("__sum += __d").append(fd.name).append(".__memory();").writeNewline();
    }
    sb.append("return __sum;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeMessageDeltaClass(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final String className) {
    sb.append("private class Delta").append(className).append(" implements DeltaNode {").tabUp().writeNewline();
    for (final FieldDefinition fd : storage.fieldsByOrder) {
      final var fieldType = environment.rules.Resolve(fd.type, false);
      final var deltaType = ((DetailHasDeltaType) fieldType).getDeltaType(environment);
      sb.append("private ").append(deltaType).append(" __d").append(fd.name).append(";").writeNewline();
    }
    writeCommonConstructorAndCost(storage.fieldsByOrder, new ArrayList<>(), sb, environment, className);
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
    sb.append("if (__obj.end()) {").tabUp().writeNewline();
    sb.append("__emitted = true;").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void clear() {").tabUp().writeNewline();
    for (final FieldDefinition fd : storage.fieldsByOrder) {
      sb.append("__d").append(fd.name).append(".clear();").writeNewline();
    }
    sb.append("__code_cost += ").append("" + (storage.fieldsByOrder.size())).append(";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("public void hide(PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    sb.append("if (__emitted) {").tabUp().writeNewline();
    sb.append("clear();").writeNewline();
    sb.append("__emitted = false;").writeNewline();
    sb.append("__writer.writeNull();").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeRecordDeltaClass(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final String className, final boolean forceManifestBecauseRoot) {
    final var fds = new ArrayList<FieldDefinition>();
    sb.append("private class Delta").append(className).append(" implements DeltaNode {").tabUp().writeNewline();
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
      FieldDefinition fd = FieldDefinition.invent(bd.expressionType, bd.nameToken.text);
      fd.ingest(bd);
      bubbles.add(fd);
    }
    writeCommonConstructorAndCost(fds, bubbles, sb, environment, className);
    sb.append("public boolean show(").append(className).append(" __item, PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    if (forceManifestBecauseRoot) {
      sb.append("DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);").writeNewline();
      sb.append("__writer.setCacheObject(__policy_cache);").writeNewline();
    } else {
      sb.append("DeltaPrivacyCache __policy_cache = (DeltaPrivacyCache) __writer.getCacheObject();").writeNewline();
    }
    final var cost = fds.size() + storage.bubbles.size();
    if (cost > 0 && !environment.state.hasNoCost()) {
      sb.append("__code_cost += ").append("" + cost).append(";").writeNewline();
    }
    for (String policy : storage.policiesForVisibility) {
      if (storage.policies.containsKey(policy)) {
        sb.append("if (!__item.__POLICY_").append(policy).append("(__writer.who)) {").tabUp().writeNewline();
      } else {
        sb.append("if (!__policy_cache.").append(policy).append(") {").tabUp().writeNewline();
      }
      sb.append("hide(__writer);").writeNewline();
      sb.append("return false;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
    sb.append("PrivateLazyDeltaWriter __obj = __writer.planObject();").writeNewline();
    if (forceManifestBecauseRoot) {
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
        boolean closeItUp = false;
        if (bd.guard != null) {
          closeItUp = bd.writePrivacyCheckGuard(sb);
        }
        sb.append("__CHECK = __item.___").append(bd.nameToken.text).append(".getGeneration();").writeNewline();
        if (bd.viewerFields.size() != 0) {
          for (String vf : bd.viewerFields) {
            sb.append("__CHECK = __CHECK * 1662803L + __VIEWER.__GEN_").append(vf).append(";").writeNewline();
          }
        }
        sb.append("if (__g").append(bd.nameToken.text).append(" != __CHECK)  {").tabUp().writeNewline();
        if (environment.state.options.instrumentPerf) {
          sb.append("Runnable __PTb_").append(bd.nameToken.text).append(" = __perf.measure(\"").append(storage.name.text).append("_b_" + bd.nameToken.text).append("\");").writeNewline();
        }
        final var bubbleType = environment.rules.Resolve(bd.expressionType, false);
        sb.append(bubbleType.getJavaBoxType(environment)).append(" __local_").append(bd.nameToken.text).append(" = __item.__COMPUTE_").append(bd.nameToken.text).append("(__writer.who, __VIEWER);").writeNewline();
        writeShowData(sb, "__d" + bd.nameToken.text, "__local_" + bd.nameToken.text, bubbleType, "__obj.planField(\"" + bd.nameToken.text + "\")", environment, false);
        if (environment.state.options.instrumentPerf) {
          sb.append("__PTb_").append(bd.nameToken.text).append(".run();").writeNewline();
        }
        sb.append("__g").append(bd.nameToken.text).append(" = __CHECK;").tabDown().writeNewline();
        sb.append("}").writeNewline();
        if (closeItUp) {
          sb.append("/* privacy check close up */").tabDown().writeNewline();
          sb.append("} else {").tabUp().writeNewline();
          sb.append("__g").append(bd.nameToken.text).append(" = -1;").writeNewline();
          sb.append("__d").append(bd.nameToken.text).append(".hide(__obj.planField(\"").append(bd.nameToken.text).append("\"));").tabDown().writeNewline();
          sb.append("}").writeNewline();
        }
      }
    }
    sb.append("if (__obj.end()) {").tabUp().writeNewline();
    sb.append("__emitted = true;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("return true;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void clear() {").tabUp().writeNewline();
    for (final FieldDefinition fd : fds) {
      sb.append("__d").append(fd.name).append(".clear();").writeNewline();
    }
    for (final BubbleDefinition bd : storage.bubbles.values()) {
      sb.append("__d").append(bd.nameToken.text).append(".clear();").writeNewline();
    }
    sb.append("__code_cost += ").append("" + (fds.size() + storage.bubbles.size())).append(";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("public void hide(PrivateLazyDeltaWriter __writer) {").tabUp().writeNewline();
    sb.append("if (__emitted) {").tabUp().writeNewline();
    sb.append("clear();").writeNewline();
    sb.append("__emitted = false;").writeNewline();
    sb.append("__writer.writeNull();").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  private static void writeShowData(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType sourceType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    if (sourceType instanceof TySimpleNative || sourceType instanceof TyReactiveRecord || sourceType instanceof TyNativeMessage || sourceType instanceof TyReactiveText) {
      sb.append(deltaObject).append(".show(").append(sourceData).append(", ").append(targetObjectWriter).append(");");
      if (tabDown) {
        sb.tabDown();
      }
      sb.writeNewline();
    } else if (sourceType instanceof TyNativeList || sourceType instanceof TyNativeArray) {
      final var elementType = ((DetailContainsAnEmbeddedType) sourceType).getEmbeddedType(environment);
      if (elementType instanceof TyReactiveRecord) {
        writeShowDListRecords(sb, deltaObject, sourceData, elementType, targetObjectWriter, environment, tabDown);
      } else if (elementType instanceof TyNativeMessage && ((TyNativeMessage) elementType).hasUniqueId()) {
        writeShowDListMessagesWithUniqueID(sb, deltaObject, sourceData, elementType, targetObjectWriter, environment, tabDown);
      } else {
        writeShowDListNonRecord(sb, deltaObject, sourceData, elementType, targetObjectWriter, environment, tabDown);
      }
    } else if (sourceType instanceof TyNativeResult) {
      writeShowDResult(sb, deltaObject, sourceData, ((TyNativeResult) sourceType).getEmbeddedType(environment), targetObjectWriter, environment, tabDown);
    } else if (sourceType instanceof TyNativeMaybe) {
      writeShowDMaybe(sb, deltaObject, sourceData, ((TyNativeMaybe) sourceType).getEmbeddedType(environment), targetObjectWriter, environment, tabDown);
    } else if (sourceType instanceof TyNativeMap) {
      writeShowDMap(sb, deltaObject, sourceData, environment.rules.Resolve(((TyNativeMap) sourceType).domainType, false), environment.rules.Resolve(((TyNativeMap) sourceType).rangeType, false), targetObjectWriter, environment, tabDown);
    } else if (sourceType instanceof TyNativePair) {
      writeShowDPair(sb, deltaObject, sourceData, environment.rules.Resolve(((TyNativePair) sourceType).domainType, false), environment.rules.Resolve(((TyNativePair) sourceType).rangeType, false), targetObjectWriter, environment, tabDown);
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

  private static void writeShowRecord(final StringBuilderWithTabs sb, final String gateVar, final String deltaObject, final String sourceData, final TyType sourceType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    sb.append("boolean ").append(gateVar).append(" = ").append(deltaObject).append(".show(").append(sourceData).append(", ").append(targetObjectWriter).append(");");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowMessage(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType sourceType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    sb.append(deltaObject).append(".show(").append(sourceData).append(", ").append(targetObjectWriter).append(");");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDListNonRecord(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
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

  private static void writeShowDListRecords(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var elementDeltaType = ((DetailHasDeltaType) elementType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__list" + environment.autoVariable();
    final var dListCache = "__deltaList" + environment.autoVariable();
    final var dListWalker = "__deltaListWalker" + environment.autoVariable();
    final var childElementType = elementType.getJavaBoxType(environment);
    final var childElementVar = "__listElement" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    final var gateVar = "__gate" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DRecordList<").append(elementDeltaType).append("> ").append(dListCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DRecordList<").append(elementDeltaType).append(">.Walk ").append(dListWalker).append(" = ").append(dListCache).append(".begin();").writeNewline();
    sb.append("for (").append(childElementType).append(" ").append(childElementVar).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(elementDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dListCache).append(".getPrior(").append(childElementVar).append(".__id(), () -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowRecord(sb, gateVar, childDeltaVar, childElementVar, elementType, listWriterVar + ".planField(" + childElementVar + ".__id())", environment, false);
    sb.append("if (").append(gateVar).append(") {").tabUp().writeNewline();
    sb.append(dListWalker).append(".next(").append(childElementVar).append(".__id());").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append(dListWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDListMessagesWithUniqueID(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
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
    sb.append(elementDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dListCache).append(".getPrior(").append(childElementVar).append(".id, () -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowMessage(sb, childDeltaVar, childElementVar, elementType, listWriterVar + ".planField(" + childElementVar + ".id)", environment, false);
    sb.append(dListWalker).append(".next(").append(childElementVar).append(".id);").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append(dListWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDMap(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType domainType, final TyType rangeType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var domainBoxType = domainType.getJavaBoxType(environment);
    final var rangeDeltaType = ((DetailHasDeltaType) rangeType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var listWriterVar = "__map" + environment.autoVariable();
    final var dMapCache = "__deltaMap" + environment.autoVariable();
    final var dMapCacheWalker = "__deltaMapWalker" + environment.autoVariable();
    final var entryType = "NtPair<" + domainType.getJavaBoxType(environment) + "," + rangeType.getJavaBoxType(environment) + ">";
    final var mapEntry = "__mapEntry" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaType).append("> ").append(dMapCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaType).append(">.Walk ").append(dMapCacheWalker).append(" = ").append(dMapCache).append(".begin();").writeNewline();
    sb.append("for (").append(entryType).append(" ").append(mapEntry).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(rangeDeltaType).append(" ").append(childDeltaVar).append(" = ").append(dMapCacheWalker).append(".next(").append(mapEntry).append(".key, () -> new ").append(rangeDeltaType).append("()").append(");").writeNewline();
    writeShowData(sb, childDeltaVar, mapEntry + ".value", rangeType, listWriterVar + ".planField(\"\" + " + mapEntry + ".key)", environment, true);
    sb.append("}").writeNewline();
    sb.append(dMapCacheWalker).append(".end(").append(listWriterVar).append(");").writeNewline();
    sb.append(listWriterVar).append(".end();").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private static void writeShowDPair(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType domainType, final TyType rangeType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var domainBoxType = domainType.getJavaBoxType(environment);
    final var rangeBoxType = rangeType.getJavaBoxType(environment);
    final var domainDeltaType = ((DetailHasDeltaType) domainType).getDeltaType(environment);
    final var rangeDeltaType = ((DetailHasDeltaType) rangeType).getDeltaType(environment);
    sb.append("{").tabUp().writeNewline();
    final var pairWriterVar = "__pair" + environment.autoVariable();
    final var src = "__src" + environment.autoVariable();
    final var valKey = "__key" + environment.autoVariable();
    final var valValue = "__val" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(pairWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("NtPair<").append(domainBoxType).append(",").append(rangeBoxType).append("> ").append(src).append(" = ").append(sourceData).append(";").writeNewline();
    sb.append(domainDeltaType).append(" ").append(valKey).append(" = ").append(deltaObject).append(".key(() -> new ").append(rangeDeltaType).append("()").append(");").writeNewline();
    writeShowData(sb, valKey, src + ".key", domainType, pairWriterVar + ".planField(\"key\")", environment, false);
    sb.append(rangeDeltaType).append(" ").append(valValue).append(" = ").append(deltaObject).append(".value(() -> new ").append(rangeDeltaType).append("()").append(");").writeNewline();
    writeShowData(sb, valValue, src + ".value", domainType, pairWriterVar + ".planField(\"value\")", environment, false);
    sb.append(pairWriterVar).append(".end();").tabDown().writeNewline();
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
    final var entryTypeWalk = "NtPair<" + domainType.getJavaBoxType(environment) + "," + walkRangeType.getJavaBoxType(environment) + ">";
    final var mapEntryWalk = "__mapEntry" + environment.autoVariable();
    final var childDeltaVar = "__deltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(listWriterVar).append(" = ").append(targetObjectWriter).append(".planObject();").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaTypeGet).append("> ").append(dMapCache).append(" = ").append(deltaObject).append(";").writeNewline();
    sb.append("DMap<").append(domainBoxType).append(",").append(rangeDeltaTypeGet).append(">.Walk ").append(dMapCacheWalker).append(" = ").append(dMapCache).append(".begin();").writeNewline();
    sb.append("for (").append(entryTypeWalk).append(" ").append(mapEntryWalk).append(" : ").append(sourceData).append(") {").tabUp().writeNewline();
    sb.append(rangeDeltaTypeGet).append(" ").append(childDeltaVar).append(" = ").append(dMapCacheWalker).append(".next(").append(mapEntryWalk).append(".key, () -> new ").append(rangeDeltaTypeGet).append("()").append(");").writeNewline();
    writeShowData(sb, childDeltaVar, mapEntryWalk + ".value" + (addGet ? ".get()" : ""), getRangeType, listWriterVar + ".planField(\"\" + " + mapEntryWalk + ".key)", environment, true);
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

  private static void writeShowDResult(final StringBuilderWithTabs sb, final String deltaObject, final String sourceData, final TyType elementType, final String targetObjectWriter, final Environment environment, final boolean tabDown) {
    final var childWriter = "__resultWriter" + environment.autoVariable();
    final var elementDeltaType = ((DetailHasDeltaType) elementType).getDeltaType(environment);
    final var childElementType = elementType.getJavaBoxType(environment);
    final var childElementVar = "__resultChild" + environment.autoVariable();
    final var deltaElementVar = "__resultDeltaElement" + environment.autoVariable();
    sb.append("PrivateLazyDeltaWriter ").append(childWriter).append(" = ").append(deltaObject).append(".show(").append(sourceData).append(",").append(targetObjectWriter).append(");").writeNewline();
    sb.append("if (").append(sourceData).append(".has()) {").tabUp().writeNewline();
    sb.append(childElementType).append(" ").append(childElementVar).append(" = (").append(childElementType).append(")(").append(sourceData).append(".get());").writeNewline();
    sb.append(elementDeltaType).append(" ").append(deltaElementVar).append(" = ").append(deltaObject).append(".get(() -> new ").append(elementDeltaType).append("());").writeNewline();
    writeShowData(sb, deltaElementVar, childElementVar, elementType, childWriter, environment, true);
    sb.append("} else {").tabUp().writeNewline();
    sb.append(deltaObject).append(".hide(").append(childWriter).append(");").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append(childWriter).append(".end();");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }
}
