/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.env;

import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineService;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.Rules;
import org.adamalang.translator.tree.types.natives.TyNativeDate;
import org.adamalang.translator.tree.types.natives.TyNativeLazyWrap;
import org.adamalang.translator.tree.types.natives.TyNativeService;
import org.adamalang.translator.tree.types.reactive.TyReactiveLong;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Defines the environment within */
public class Environment {
  public final CodeCoverageTracker codeCoverageTracker;
  public final Document document;
  public final Rules rules;
  public final EnvironmentState state;
  public final HashSet<String> interns;
  private final Environment parent;
  private final HashSet<String> readonly;
  private final HashMap<String, TyType> variables;
  private TyType returnType;
  private TyType caseType;
  private TyType selfType;
  private Function<String, TyType> trap = null;
  private BiConsumer<String, TyType> watch = null;
  private HashMap<String, TyType> specialConstants;
  private boolean hasDefaultCase;

  private Environment(final Document document, final EnvironmentState state, final Environment parent) {
    this.document = document;
    this.state = state;
    variables = new HashMap<>();
    readonly = new HashSet<>();
    this.parent = parent;
    returnType = null;
    caseType = null;
    selfType = null;
    if (parent != null) {
      rules = parent.rules;
      codeCoverageTracker = parent.codeCoverageTracker;
    } else {
      rules = new Rules(this);
      codeCoverageTracker = new CodeCoverageTracker();
    }
    if (parent != null) {
      this.interns = parent.interns;
    } else {
      this.interns = new HashSet<>();
      this.interns.add("\"?\"");
      this.interns.add("\"\"");
    }
    this.specialConstants = null;
  }

  /** construct an environment that is fresh */
  public static Environment fresh(final Document document, final EnvironmentState state) {
    return new Environment(document, state, null);
  }

  /** get the root environment for the document; this is for static methods like @static { create(who) { ... } } */
  public Environment staticPolicy() {
    return new Environment(document, state, null).scopeAsReadOnlyBoundary();
  }

  /** create a new environment which enforces a readonly barrier to all mutations prior it */
  public Environment scopeAsReadOnlyBoundary() {
    return new Environment(document, state.scopeReadonly(), this).scope();
  }

  /** create a new environment which allows abortion */
  public Environment scopeAsAbortable() {
    return new Environment(document, state.scopeAbortion(), this).scope();
  }

  /** create a new environment for @authorize */
  public Environment scopeAsAuthorize() {
    return new Environment(document, state.scopeAuthorize(), this);
  }

  /** need to capture special variables */
  public Environment captureSpecials() {
    Environment next = scope();
    next.specialConstants = new HashMap<>();
    return next;
  }

  /** return a map of the specials within the environment */
  public HashMap<String, TyType> specials() {
    return specialConstants;
  }

  /** use a special */
  public void useSpecial(TyType type, String name) {
    if (specialConstants != null) {
      specialConstants.put(name, type);
    }
    if (parent != null) {
      parent.useSpecial(type, name);
    }
  }

  public TyType getSelfType() {
    if (selfType != null) {
      return selfType;
    }
    if (parent != null) {
      return parent.getSelfType();
    }
    return null;
  }

  public void setSelfType(TyType selfType) {
    this.selfType = selfType;
  }

  /** when we need to create a variable, let's make it globally unique */
  public int autoVariable() {
    return state.autoId.getAndIncrement();
  }

  /** define a variable within the environment */
  public Environment define(final String name, final TyType type, final boolean isReadonly, final DocumentPosition position) {
    if (variables.containsKey(name)) {
      document.createError(position, String.format("Variable '%s' was already defined", name));
      return this;
    }
    if (type == null) {
      document.createError(position, String.format("Variable '%s' has no backing type", name));
    }
    variables.put(name, type);
    if (isReadonly) {
      readonly.add(name);
    }
    return this;
  }

  /** is the variable defined period */
  public boolean defined(String name) {
    if (variables.containsKey(name)) {
      return true;
    }
    if (state.isDefineBoundary()) {
      return false;
    }
    if (parent != null) {
      return parent.defined(name);
    }
    return false;
  }

  private TyType lookup_return(String name, TyType result) {
    if (watch != null) {
      watch.accept(name, result);
    }
    return result;
  }

  /** look up the type of the given variable; will throw issues */
  public TyType lookup(final String name, final boolean compute, final DocumentPosition position, final boolean silent) {
    // test the current environment
    var result = variables.get(name);
    if (result != null) {
      // is the current environment
      if (!compute && (readonly.contains(name) || result.behavior == TypeBehavior.ReadOnlyNativeValue) && !silent) {
        document.createError(position, String.format("The variable '%s' is readonly", name));
      }
      return lookup_return(name, result);
    }
    // let's try to invent the type if a trap has been set
    if (result == null && trap != null) {
      result = trap.apply(name);
    }
    // it is still not found
    if (result == null && parent != null) {
      result = parent.lookup(name, compute, position, silent);
      if (result != null && !compute && state.isReadonly() && !silent) {
        document.createError(position, String.format("The variable '%s' is readonly due to the environment", name));
      }
    }
    // then it must be a function
    if (result == null) {
      final var func = document.functionTypes.get(name);
      if (func != null) {
        return lookup_return(name, func);
      }
    }

    // Ok, maybe it is a global object
    final var globalObject = state.globals.get(name);
    if (globalObject != null) {
      return lookup_return(name, globalObject);
    }

    // Or, a service?
    DefineService ds = document.services.get(name);
    if (ds != null) {
      return lookup_return(name, new TyNativeService(ds).withPosition(position));
    }

    if ("__time".equals(name)) {
      return lookup_return(name, new TyReactiveLong(null));
    }

    if ("__today".equals(name)) {
      return lookup_return(name, new TyNativeLazyWrap(new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, null)));
    }

    return lookup_return(name, result);
  }

  /** test the immediate environment for a variable definition */
  public TyType lookupDirect(final String name) {
    return variables.get(name);
  }

  /** assert the environment must be a compute environment */
  public Environment mustBeComputeContext(final DocumentPosition position) {
    if (!state.isContextComputation()) {
      document.createError(position, String.format("Expression expected to be computed, rather than assigned to"));
    }
    return this;
  }

  /** create a new environment just for message handler */
  public Environment scopeAsMessageHandler() {
    return new Environment(document, state.scopeMessageHandler(), this);
  }

  /** create a new environment that has access to the viewer */
  public Environment scopeWithViewer() {
    return new Environment(document, state.scopeViewer(), this);
  }


  /** create a new environment that has access to the viewer */
  public Environment scopeDefine() {
    return new Environment(document, state.scopeDefineLimit(), this);
  }

  /** create a new environment just for constructor */
  public Environment scopeAsConstructor() {
    return new Environment(document, state.scopeConstructor(), this);
  }

  /** create a new environment just for document events */
  public Environment scopeAsDocumentEvent() {
    return new Environment(document, state.scopeDocumentEvent(), this);
  }

  /** create a new environment where code is free (Why?) */
  public Environment scopeAsNoCost() {
    return new Environment(document, state.scopeNoCost(), this);
  }

  /** create a new environment for the same document that is fresh for pure functions */
  public Environment scopeAsPureFunction() {
    return new Environment(document, state.scopePure(), null);
  }

  /** create a new environment which is for state machine transitions */
  public Environment scopeAsStateMachineTransition() {
    return new Environment(document, state.scopeStateMachineTransition(), this);
  }

  /** create a new environment which is for document policies */
  public Environment scopeAsPolicy() {
    return new Environment(document, state.scopePolicy(), this);
  }

  /** create a new environment which is a bubble */
  public Environment scopeAsBubble() {
    return new Environment(document, state.scopeBubble(), this);
  }

  /** create a new environment for web calls */
  public Environment scopeAsWeb(String method) {
    return new Environment(document, state.scopeWeb(method), this);
  }

  /** create a new environment which is for unit tests */
  public Environment scopeAsUnitTest() {
    return new Environment(document, state.scopeTesting(), this);
  }

  /** create a new environment for scoping a reactive expression */
  public Environment scopeReactiveExpression() {
    return new Environment(document, state.scopeReactiveExpression(), this);
  }

  /** create a new environment which is for the given computation context */
  public Environment scopeWithComputeContext(final ComputeContext context) {
    return new Environment(document, state.scopeWithComputeContext(context), this);
  }

  /** create a new environment which is for static methods (i.e. policy methods) */
  public Environment scopeStatic() {
    return new Environment(document, state.scopeStatic(), this);
  }

  /** create a new environment which is for static methods (i.e. policy methods) */
  public Environment scopeMessage() {
    return new Environment(document, state.scopeStatic().scopePure(), null);
  }

  /** create a new environment which is for leveraging the cache */
  public Environment scopeWithCache(String cacheObject) {
    return new Environment(document, state.scopeWithCache(cacheObject), this);
  }

  /** @return the most recent return type. */
  public TyType getMostRecentReturnType() {
    if (returnType != null) {
      return returnType;
    }
    if (parent != null) {
      return parent.getMostRecentReturnType();
    }
    return null;
  }

  /** set the return type of the given scope */
  public Environment setReturnType(final TyType returnType) {
    this.returnType = returnType;
    return this;
  }


  /** @return the case type of the current environment. */
  public TyType getCaseType() {
    return caseType;
  }

  public Environment setCaseType(final TyType caseType) {
    this.caseType = caseType;
    return this;
  }

  public boolean checkDefaultReturnTrueIfMultiple() {
    if (hasDefaultCase) {
      return true;
    }
    hasDefaultCase = true;
    return false;
  }

  /** create a new environment (scoped) that will trap the given variables */
  public Environment trap(final Function<String, TyType> trap) {
    final var next = scope();
    next.trap = trap;
    return next;
  }

  /** create a new environment which allows new variables to override previous variables */
  public Environment scope() {
    return new Environment(document, state.scope(), this);
  }

  /** create a new environment (scoped) that will watch for variables that escape */
  public Environment watch(final BiConsumer<String, TyType> watch) {
    final var next = scope();
    next.watch = watch;
    return next;
  }
}
