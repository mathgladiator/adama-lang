/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.env;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.Rules;

/** Defines the environment within */
public class Environment {
  /** construct an environment that is fresh */
  public static Environment fresh(final Document document, final EnvironmentState state) {
    return new Environment(document, state, null);
  }

  public final CodeCoverageTracker codeCoverageTracker;
  public final Document document;
  private final Environment parent;
  private final HashSet<String> readonly;
  private TyType returnType;
  private boolean returnTypeSet;
  public final Rules rules;
  public final EnvironmentState state;
  private Function<String, TyType> trap = null;
  private final HashMap<String, TyType> variables;
  private Consumer<String> watch = null;
  public final HashSet<String> interns;

  private Environment(final Document document, final EnvironmentState state, final Environment parent) {
    this.document = document;
    this.state = state;
    variables = new HashMap<>();
    readonly = new HashSet<>();
    this.parent = parent;
    returnTypeSet = false;
    returnType = null;
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
  }

  /** when we need to create a variable, let's make it globally unique */
  public int autoVariable() {
    return state.autoId.getAndIncrement();
  }

  /** define a variable within the environment */
  public Environment define(final String name, final TyType type, final boolean isReadonly, final DocumentPosition position) {
    if (variables.containsKey(name)) {
      document.createError(position, String.format("Variable '%s' was already defined", name), "EnvironmentDefine");
      return this;
    }
    if (type == null) {
      document.createError(position, String.format("Variable '%s' has no backing type", name), "EnvironmentDefine");
    }
    variables.put(name, type);
    if (isReadonly) {
      readonly.add(name);
    }
    return this;
  }

  /** @return the most recent return type. */
  public TyType getMostRecentReturnType() {
    if (returnTypeSet) { return returnType; }
    if (parent != null) { return parent.getMostRecentReturnType(); }
    return null;
  }

  /** look up the type of the given variable; will throw issues */
  public TyType lookup(final String name, final boolean compute, final DocumentPosition position, final boolean silent) {
    // something is watching what flows pass this
    if (watch != null) {
      watch.accept(name);
    }
    // test the current environment
    var result = variables.get(name);
    if (result != null) {
      // is the current environment
      if (!compute && (readonly.contains(name) || result.behavior == TypeBehavior.ReadOnlyNativeValue) && !silent) {
        document.createError(position, String.format("The variable '%s' is readonly", name), "VariableLookup");
      }
      return result;
    }
    // let's try to invent the type if a trap has been set
    if (result == null && trap != null) {
      result = trap.apply(name);
    }
    // it is still not found
    if (result == null && parent != null) {
      result = parent.lookup(name, compute, position, silent);
      if (result != null && !compute && state.isReadonly() && !silent) {
        document.createError(position, String.format("The variable '%s' is readonly due to the environment", name), "VariableLookup");
      }
    }
    // then it must be a function
    if (result == null) {
      final var func = document.functionTypes.get(name);
      if (func != null) { return func; }
    }
    return result;
  }

  /** test the immediate environment for a variable definition */
  public TyType lookupDirect(final String name) {
    return variables.get(name);
  }

  /** assert the environment must be a compute environment */
  public Environment mustBeComputeContext(final DocumentPosition position) {
    if (!state.isContextComputation()) {
      document.createError(position, String.format("Expression expected to be computed, rather than assigned to"), "Environment");
    }
    return this;
  }

  /** create a new environment which allows new variables to override previous
   * variables */
  public Environment scope() {
    return new Environment(document, state, this);
  }

  /** create a new environment just for message handler */
  public Environment scopeAsMessageHandler() {
    return new Environment(document, state.scopeMessageHandler(), this);
  }

  /** create a new environment where code is free (Why?) */
  public Environment scopeAsNoCost() {
    return new Environment(document, state.scopeNoCost(), this);
  }

  /** create a new environment for the same document that is fresh for pure
   * functions */
  public Environment scopeAsPureFunction() {
    return new Environment(document, state.scopePure(), null);
  }

  /** create a new environment which enforces a readonly barrier to all mutations
   * prior it */
  public Environment scopeAsReadOnlyBoundary() {
    return new Environment(document, state, new Environment(document, state.scopeReadonly(), this));
  }

  /** create a new environment which is for state machine transitions */
  public Environment scopeAsStateMachineTransition() {
    return new Environment(document, state.scopeStateMachineTransition(), this);
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

  /** set the return type of the given scope */
  public Environment setReturnType(final TyType returnType) {
    returnTypeSet = true;
    this.returnType = returnType;
    return this;
  }

  /** create a new environment (scoped) that will trap the given variables */
  public Environment trap(final Function<String, TyType> trap) {
    final var next = scope();
    next.trap = trap;
    return next;
  }

  /** create a new environment (scoped) that will watch for variables that
   * escape */
  public Environment watch(final Consumer<String> watch) {
    final var next = scope();
    next.watch = watch;
    return next;
  }
}
