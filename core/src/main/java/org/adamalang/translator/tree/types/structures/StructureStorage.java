/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.env.topo.TopologicalSort;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.FunctionArg;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.Watcher;
import org.adamalang.translator.tree.types.topo.TypeChecker;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.topo.TypeCheckerStructure;

import java.util.*;
import java.util.function.Consumer;

public class StructureStorage extends DocumentPosition {
  public final boolean anonymous;
  public final TreeMap<String, BubbleDefinition> bubbles;
  public final ArrayList<Consumer<Consumer<Token>>> emissions;
  public final TreeMap<String, FieldDefinition> fields;
  public final ArrayList<FieldDefinition> fieldsByOrder;
  public final HashSet<String> indexSet;
  public final ArrayList<IndexDefinition> indices;
  public final ArrayList<DefineMethod> methods;
  public final HashMap<String, TyNativeFunctional> methodTypes;
  public final HashMap<String, TyNativeFunctional> internalMethods;
  public final Token openBraceToken;
  public final TreeMap<String, DefineCustomPolicy> policies;
  public final ArrayList<String> policiesForVisibility;
  public final StorageSpecialization specialization;
  public TypeCheckerStructure checker;
  public final HashSet<String> fieldsWithDefaults;
  public Token closeBraceToken;

  public StructureStorage(final StorageSpecialization specialization, final boolean anonymous, final Token openBraceToken) {
    this.specialization = specialization;
    this.anonymous = anonymous;
    this.openBraceToken = openBraceToken;
    this.checker = new TypeCheckerStructure();
    closeBraceToken = null;
    fields = new TreeMap<>();
    bubbles = new TreeMap<>();
    methods = new ArrayList<>();
    fieldsByOrder = new ArrayList<>();
    policies = new TreeMap<>();
    policiesForVisibility = new ArrayList<>();
    emissions = new ArrayList<>();
    indices = new ArrayList<>();
    indexSet = new HashSet<>();
    methodTypes = new HashMap<>();
    internalMethods = new HashMap<>();
    fieldsWithDefaults = new HashSet<>();
    ingest(openBraceToken);
  }

  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    for (FieldDefinition fd : fieldsByOrder) {
      writer.writeObjectFieldIntro(fd.name);
      writer.beginObject();
      if (fd.type != null) {
        writer.writeObjectFieldIntro("type");
        fd.type.writeTypeReflectionJson(writer, ReflectionSource.Structure);
      }
      if (fd.policy != null) {
        writer.writeObjectFieldIntro("privacy");
        fd.policy.writeTypeReflectionJson(writer);
      }
      writer.endObject();
    }

    for (Map.Entry<String, BubbleDefinition> bd : bubbles.entrySet()) {
      writer.writeObjectFieldIntro(bd.getKey());
      writer.beginObject();
      writer.writeObjectFieldIntro("type");
      bd.getValue().expressionType.writeTypeReflectionJson(writer, ReflectionSource.Structure);
      writer.writeObjectFieldIntro("privacy");
      writer.writeString("bubble");
      writer.endObject();
    }

    writer.endObject();
  }



  public void add(final BubbleDefinition bd) {
    addCommon(bd, FreeEnvironment.root(), checker);
  }

  public void addFromRoot(final BubbleDefinition bd, TypeCheckerRoot checker) {
    addCommon(bd, FreeEnvironment.root(), checker);
  }

  public void addCommon(final BubbleDefinition bd, FreeEnvironment fe, TypeChecker inChecker) {
    emissions.add(emit -> bd.emit(emit));
    ingest(bd);
    bd.expression.free(fe);
    inChecker.register(fe.free, env -> bd.typing(env.watch(Watcher.make(env, bd.variablesToWatch, bd.servicesToWatch))));
    if (has(bd.nameToken.text)) {
      inChecker.issueError(bd, String.format("Bubble '%s' was already defined", bd.nameToken.text), "StructureDefine");
      return;
    }
    bubbles.put(bd.nameToken.text, bd);
  }

  /** does this record contain this field */
  public boolean has(final String name) {
    return fields.containsKey(name) || bubbles.containsKey(name);
  }

  public void add(final DefineMethod dm) {
    emissions.add(emit -> dm.emit(emit));
    ingest(dm);
    methods.add(dm);
    FreeEnvironment fe = FreeEnvironment.root();
    for (FunctionArg arg : dm.args) {
      fe.define(arg.argName);
    }
    dm.code.free(fe);

    checker.define(dm.nameToken, fe.free, env -> {
      HashSet<String> local = new HashSet<>();
      for (String field : fields.keySet()) {
        local.add(field);
      }
      for (DefineMethod method : methods) {
        local.add(method.name);
      }
      final var foi = dm.typing(env, local);
      var functional = methodTypes.get(dm.name);
      if (functional == null) {
        functional = new TyNativeFunctional(dm.name, new ArrayList<>(), FunctionStyleJava.ExpressionThenNameWithArgs);
        methodTypes.put(dm.name, functional);
      }
      functional.overloads.add(foi);
      var interFunctional = internalMethods.get(dm.name);
      if (interFunctional == null) {
        interFunctional = new TyNativeFunctional(dm.name, new ArrayList<>(), FunctionStyleJava.InjectNameThenArgs);
        internalMethods.put(dm.name, interFunctional);
        env.define(dm.name, interFunctional, false, dm);
      }
      interFunctional.overloads.add(foi);
    });
  }

  /** add the given field to the record */
  public void add(final FieldDefinition fd) {
    addCommon(fd, FreeEnvironment.root(), checker);
  }

  public void addFromRoot(final FieldDefinition fd, TypeCheckerRoot checker) {
    addCommon(fd, FreeEnvironment.root(), checker);
  }

  public void addCommon(final FieldDefinition fd, FreeEnvironment fe, TypeChecker insertChecker) {
    emissions.add(emit -> fd.emit(emit));
    ingest(fd);

    if (has(fd.nameToken.text)) {
      insertChecker.issueError(fd, String.format("Field '%s' was already defined", fd.nameToken.text), "StructureDefine");
      return;
    }
    if (fd.defaultValueOverride != null) {
      fieldsWithDefaults.add(fd.name);
    }
    if (fd.computeExpression != null) {
      fd.computeExpression.free(fe);
    }
    insertChecker.define(fd.nameToken, fe.free, env -> {
      fd.typing(env.watch(Watcher.make(env, fd.variablesToWatch, fd.servicesToWatch)), this);
      env.define(fd.name, fd.type, false, fd);
    });
    fields.put(fd.name, fd);
    fieldsByOrder.add(fd);
  }

  public void add(final IndexDefinition indexDefn) {
    emissions.add(x -> indexDefn.emit(x));
    if (!indexSet.contains(indexDefn.nameToken.text)) {
      indices.add(indexDefn);
      indexSet.add(indexDefn.nameToken.text);
      checker.register(Collections.singleton(indexDefn.nameToken.text), env -> {
        final var fd = fields.get(indexDefn.nameToken.text);
        if (fd == null) {
          env.document.createError(indexDefn, String.format("Index could not find field '%s'", indexDefn.nameToken.text), "StructureDefine");
        } else {
          final var canBeIndex = fd.type instanceof TyReactiveInteger || fd.type instanceof TyReactiveEnum || fd.type instanceof TyReactivePrincipal || fd.type instanceof TyReactiveDate || fd.type instanceof TyReactiveTime;
          if (!canBeIndex) {
            env.document.createError(indexDefn, String.format("Index for field '%s' is not possible due to type", indexDefn.nameToken.text, fd.type.getAdamaType()), "StructureDefine");
          }
        }
      });
    } else {
      checker.issueError(indexDefn, String.format("Index was already defined: '%s'", indexDefn.nameToken.text), "StructureDefine");
    }
  }

  /** add the policy to the record */
  public void addPolicy(final DefineCustomPolicy policy) {
    emissions.add(emit -> policy.emit(emit));
    if (policies.containsKey(policy.name.text)) {
      checker.issueError(policy, String.format("Policy '%s' was already defined", policy.name.text), "RecordMethodDefine");
      return;
    }
    policies.put(policy.name.text, policy);
    policy.typing(checker);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(openBraceToken);
    for (final Consumer<Consumer<Token>> c : emissions) {
      c.accept(yielder);
    }
    yielder.accept(closeBraceToken);
  }

  public void end(final Token closeBraceToken) {
    this.closeBraceToken = closeBraceToken;
    ingest(closeBraceToken);
  }

  public void finalizeRecord() {
    if (!fields.containsKey("id")) {
      final var fakeId = FieldDefinition.invent(new TyReactiveInteger(null).withPosition(this), "id");
      fakeId.ingest(this);
      fields.put("id", fakeId);
      fieldsByOrder.add(fakeId);
    }
  }

  public StructureStorage makeAnonymousCopy() {
    final var storage = new StructureStorage(StorageSpecialization.Message, true, openBraceToken);
    storage.fieldsByOrder.addAll(fieldsByOrder);
    storage.fields.putAll(fields);
    return storage;
  }

  public void markPolicyForVisibility(final Token requireToken, final Token policyToCheckToken, final Token semicolon) {
    emissions.add(yielder -> {
      yielder.accept(requireToken);
      yielder.accept(policyToCheckToken);
      yielder.accept(semicolon);
    });
    final var policyToCheck = policyToCheckToken.text;

    policiesForVisibility.add(policyToCheck);
    checker.register(Collections.singleton("policy:" + policyToCheck), env -> {
      if (!policies.containsKey(policyToCheck) && !env.document.root.storage.policies.containsKey(policyToCheck)) {
        final var dp = new DocumentPosition();
        dp.ingest(requireToken);
        dp.ingest(semicolon);
        env.document.createError(dp, String.format("Policy '%s' was not found", policyToCheck), "CustomPolicy");
      }
    });
  }

  /** is the other record type the same as this type. Must be exact. */
  public boolean match(final StructureStorage other, final Environment environment) {
    if (specialization != other.specialization || fields.size() != other.fields.size() || fieldsWithDefaults.size() > 0 || other.fieldsWithDefaults.size() > 0) {
      return false;
    }
    final var thisIt = fields.values().iterator();
    final var thisOther = other.fields.values().iterator();
    while (thisIt.hasNext() && thisOther.hasNext()) {
      final var a = thisIt.next();
      final var b = thisOther.next();
      if (!a.equals(b)) {
        return false;
      }
    }
    return !thisIt.hasNext() && !thisOther.hasNext();
  }

  public void typing(Environment envParent) {
  }

  public void typing(String name, TypeCheckerRoot rootChecker) {
    checker.register(Collections.emptySet(), env -> {
      for (final TyNativeFunctional functional : methodTypes.values()) {
        functional.typing(env);
      }
    });
    reorder();
    checker.transferInto(name, specialization, rootChecker);
  }

  public void reorder() {
    // re-order the fields to be topologically sorted
    TopologicalSort<FieldDefinition> sorter = new TopologicalSort<>();
    for (FieldDefinition fd : fieldsByOrder) {
      FreeEnvironment fe = FreeEnvironment.root();
      if (fd.computeExpression != null) {
        fd.computeExpression.free(fe);
      }
      sorter.add(fd.name, fd, fe.free);
    }
    fieldsByOrder.clear();
    fieldsByOrder.addAll(sorter.sort());
  }
}
