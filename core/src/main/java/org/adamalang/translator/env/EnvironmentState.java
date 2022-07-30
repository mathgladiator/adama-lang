/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import java.util.concurrent.atomic.AtomicInteger;

/** properties about the environment */
public class EnvironmentState {
  public final AtomicInteger autoId;
  public final GlobalObjectPool globals;
  public final CompilerOptions options;
  private ComputeContext computationContext;
  private boolean isMessageHandler;
  private boolean isNoCost;
  private boolean isStateMachineTransition;
  private boolean isStatic;
  private boolean pure;
  private boolean reactiveExpression;
  private boolean readonly;
  private boolean testing;
  private boolean isPolicy;
  private boolean isBubble;
  private boolean isWeb;
  private boolean isConstructor;
  private String webMethod;
  private String cacheObject;
  private boolean readonlyEnv;

  private EnvironmentState(final EnvironmentState prior) {
    autoId = prior.autoId;
    globals = prior.globals;
    options = prior.options;
    pure = prior.pure;
    testing = prior.testing;
    isNoCost = prior.isNoCost;
    isMessageHandler = prior.isMessageHandler;
    isStateMachineTransition = prior.isStateMachineTransition;
    computationContext = prior.computationContext;
    reactiveExpression = prior.reactiveExpression;
    isStatic = prior.isStatic;
    isPolicy = prior.isPolicy;
    isBubble = prior.isBubble;
    isWeb = prior.isWeb;
    webMethod = prior.webMethod;
    cacheObject = prior.cacheObject;
    readonly = false;
    readonlyEnv = prior.readonlyEnv;
    isConstructor = prior.isConstructor;
  }

  public EnvironmentState(final GlobalObjectPool globals, final CompilerOptions options) {
    autoId = new AtomicInteger(0);
    this.globals = globals;
    this.options = options;
    isMessageHandler = false;
    isNoCost = false;
    pure = false;
    isStateMachineTransition = false;
    testing = false;
    readonly = false;
    reactiveExpression = false;
    computationContext = ComputeContext.Unknown;
    isStatic = false;
    isPolicy = false;
    isBubble = false;
    isWeb = false;
    webMethod = null;
    cacheObject = null;
    isConstructor = false;
  }

  public boolean hasNoCost() {
    return isNoCost;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public boolean isPolicy() {
    return isPolicy;
  }

  public boolean isBubble() {
    return isBubble;
  }

  public boolean isWeb() {
    return isWeb;
  }

  public String getWebMethod() {
    return webMethod;
  }

  public String getCacheObject() {
    return cacheObject;
  }

  public boolean isContextComputation() {
    return computationContext == ComputeContext.Computation;
  }

  public boolean isContextAssignment() {
    return computationContext == ComputeContext.Assignment;
  }

  /** is the current environment operating inside a message handler */
  public boolean isMessageHandler() {
    return isMessageHandler;
  }

  /** is the current environment operating in a purity model */
  public boolean isPure() {
    return pure;
  }

  /** is the current environment a reactive expression */
  public boolean isReactiveExpression() {
    return reactiveExpression;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public boolean isReadonlyEnvironment() {
    return readonlyEnv;
  }

  public boolean isConstructor() {
    return isConstructor;
  }

  /** is the current environment for state machine transition code */
  public boolean isStateMachineTransition() {
    return isStateMachineTransition;
  }

  /** is the current environment for test code */
  public boolean isTesting() {
    return testing;
  }

  public EnvironmentState scopeMessageHandler() {
    final var next = new EnvironmentState(this);
    next.isMessageHandler = true;
    next.cacheObject = "__cache";
    return next;
  }

  public EnvironmentState scopeNoCost() {
    final var next = new EnvironmentState(this);
    next.isNoCost = true;
    return next;
  }

  public EnvironmentState scopeStatic() {
    final var next = new EnvironmentState(this);
    next.isStatic = true;
    return next;
  }

  public EnvironmentState scopePolicy() {
    final var next = new EnvironmentState(this);
    next.isPolicy = true;
    return next;
  }

  public EnvironmentState scopeBubble() {
    final var next = new EnvironmentState(this);
    next.isBubble = true;
    return next;
  }

  public EnvironmentState scopeWeb(String method) {
    final var next = new EnvironmentState(this);
    next.isWeb = true;
    next.webMethod = method;
    return next;
  }

  public EnvironmentState scopeConstructor() {
    final var next = new EnvironmentState(this);
    next.isConstructor = true;
    return next;
  }

  public EnvironmentState scopePure() {
    final var next = new EnvironmentState(this);
    next.pure = true;
    return next;
  }

  public EnvironmentState scopeReactiveExpression() {
    final var next = new EnvironmentState(this);
    next.reactiveExpression = true;
    return next;
  }

  public EnvironmentState scopeReadonly() {
    final var next = new EnvironmentState(this);
    next.readonly = true;
    next.readonlyEnv = true;
    return next;
  }

  public EnvironmentState scopeWithCache(String cacheObject) {
    final var next = new EnvironmentState(this);
    next.cacheObject = cacheObject;
    return next;
  }

  public EnvironmentState scopeStateMachineTransition() {
    final var next = new EnvironmentState(this);
    next.isStateMachineTransition = true;
    next.cacheObject = "__cache";
    return next;
  }

  public EnvironmentState scopeTesting() {
    final var next = new EnvironmentState(this);
    next.testing = true;
    return next;
  }

  public EnvironmentState scopeWithComputeContext(final ComputeContext newComputeContext) {
    if (computationContext != newComputeContext) {
      final var next = new EnvironmentState(this);
      next.computationContext = newComputeContext;
      return next;
    }
    return this;
  }
}
