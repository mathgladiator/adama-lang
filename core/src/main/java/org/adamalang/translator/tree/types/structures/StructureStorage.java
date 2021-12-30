/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.privacy.PrivatePolicy;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.reactive.TyReactiveClient;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.reactive.TyReactiveInteger;

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
  public final Token openBraceToken;
  public final TreeMap<String, DefineCustomPolicy> policies;
  public final ArrayList<String> policiesForVisibility;
  public final StorageSpecialization specialization;
  public final ArrayList<Consumer<Environment>> typeCheckOrder;
  public final HashSet<String> fieldsWithDefaults;
  public Token closeBraceToken;
  private boolean typedAlready;

  public StructureStorage(
      final StorageSpecialization specialization,
      final boolean anonymous,
      final Token openBraceToken) {
    this.specialization = specialization;
    this.anonymous = anonymous;
    this.openBraceToken = openBraceToken;
    closeBraceToken = null;
    typeCheckOrder = new ArrayList<>();
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
    fieldsWithDefaults = new HashSet<>();
    ingest(openBraceToken);
    typedAlready = false;
  }

  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.beginObject();
    for (FieldDefinition fd : fieldsByOrder) {
      if (specialization == StorageSpecialization.Record
          && (fd.policy == null || fd.policy instanceof PrivatePolicy)) {
        continue;
      }

      writer.writeObjectFieldIntro(fd.name);
      writer.beginObject();
      if (fd.type != null) {
        writer.writeObjectFieldIntro("type");
        fd.type.writeTypeReflectionJson(writer);
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
      bd.getValue().expressionType.writeTypeReflectionJson(writer);
      writer.writeObjectFieldIntro("privacy");
      writer.writeString("bubble");
      writer.endObject();
    }

    writer.endObject();
  }

  public void add(final BubbleDefinition bd) {
    add(bd, typeCheckOrder);
  }

  public void add(final BubbleDefinition bd, final ArrayList<Consumer<Environment>> order) {
    emissions.add(emit -> bd.emit(emit));
    ingest(bd);
    order.add(
        env -> {
          bd.typing(
              env.watch(
                  name -> {
                    if (!env.document.functionTypes.containsKey(name)) {
                      bd.variablesToWatch.add(name);
                    }
                  }));
        });
    if (has(bd.nameToken.text)) {
      order.add(
          env -> {
            env.document.createError(
                bd,
                String.format("Bubble '%s' was already defined", bd.nameToken.text),
                "StructureDefine");
          });
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
    typeCheckOrder.add(
        env -> {
          final var foi = dm.typing(env);
          var functional = methodTypes.get(dm.name);
          if (functional == null) {
            functional =
                new TyNativeFunctional(
                    dm.name, new ArrayList<>(), FunctionStyleJava.ExpressionThenNameWithArgs);
            methodTypes.put(dm.name, functional);
          }
          functional.overloads.add(foi);
        });
  }

  /** add the given field to the record */
  public void add(final FieldDefinition fd) {
    this.add(fd, typeCheckOrder);
  }

  /** add the given field to the record such that type checking is done in the given order */
  public void add(final FieldDefinition fd, final ArrayList<Consumer<Environment>> order) {
    emissions.add(emit -> fd.emit(emit));
    ingest(fd);
    if (has(fd.nameToken.text)) {
      order.add(
          env -> {
            env.document.createError(
                fd,
                String.format("Field '%s' was already defined", fd.nameToken.text),
                "StructureDefine");
          });
      return;
    }
    if (fd.defaultValueOverride != null) {
      fieldsWithDefaults.add(fd.name);
    }
    order.add(
        env -> {
          fd.typing(
              env.watch(
                  name -> {
                    if (!env.document.functionTypes.containsKey(name)) {
                      fd.variablesToWatch.add(name);
                    }
                  }),
              this);
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
      typeCheckOrder.add(
          env -> {
            final var fd = fields.get(indexDefn.nameToken.text);
            if (fd == null) {
              env.document.createError(
                  indexDefn,
                  String.format("Index could not find field '%s'", indexDefn.nameToken.text),
                  "StructureDefine");
            } else {
              final var canBeIndex =
                  fd.type instanceof TyReactiveInteger
                      || fd.type instanceof TyReactiveEnum
                      || fd.type instanceof TyReactiveClient;
              if (!canBeIndex) {
                env.document.createError(
                    indexDefn,
                    String.format(
                        "Index for field '%s' is not possible due to type",
                        indexDefn.nameToken.text, fd.type.getAdamaType()),
                    "StructureDefine");
              }
            }
          });
    } else {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                indexDefn,
                String.format("Index was already defined: '%s'", indexDefn.nameToken.text),
                "StructureDefine");
          });
    }
  }

  /** add the policy to the record */
  public void addPolicy(final DefineCustomPolicy policy) {
    emissions.add(emit -> policy.emit(emit));
    if (policies.containsKey(policy.name.text)) {
      typeCheckOrder.add(
          env -> {
            env.document.createError(
                policy,
                String.format("Policy '%s' was already defined", policy.name.text),
                "RecordMethodDefine");
          });
      return;
    }
    policies.put(policy.name.text, policy);
    typeCheckOrder.add(
        env -> {
          policy.typeCheck(env);
        });
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
      final var fakeId =
          FieldDefinition.invent(new TyReactiveInteger(null).withPosition(this), "id");
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

  public void markPolicyForVisibility(
      final Token requireToken, final Token policyToCheckToken, final Token semicolon) {
    emissions.add(
        yielder -> {
          yielder.accept(requireToken);
          yielder.accept(policyToCheckToken);
          yielder.accept(semicolon);
        });
    final var policyToCheck = policyToCheckToken.text;

    policiesForVisibility.add(policyToCheck);
    typeCheckOrder.add(
        env -> {
          if (!policies.containsKey(policyToCheck)) {
            final var dp = new DocumentPosition();
            dp.ingest(requireToken);
            dp.ingest(semicolon);
            env.document.createError(
                dp, String.format("Policy '%s' was not found", policyToCheck), "CustomPolicy");
          }
        });
  }

  /** is the other record type the same as this type. Must be exact. */
  public boolean match(final StructureStorage other, final Environment environment) {
    if (specialization != other.specialization) { return false; }
    if (fields.size() != other.fields.size()) { return false; }
    if (fieldsWithDefaults.size() > 0 || other.fieldsWithDefaults.size() > 0) { return false; }
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

  public void typing(Environment environment) {
    if (typedAlready) {
      return;
    }
    for (final Consumer<Environment> type : typeCheckOrder) {
      type.accept(environment);
    }
    for (final TyNativeFunctional functional : methodTypes.values()) {
      functional.typing(environment);
    }
    typedAlready = true;
  }
}
