/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
  private boolean define;
  private boolean testing;
  private boolean isPolicy;
  private boolean isFilter;
  private boolean isBubble;
  private boolean isWeb;
  private boolean isTrafficHint;
  private boolean isConstructor;
  private boolean isDocumentEvent;
  private String webMethod;
  private String cacheObject;
  private boolean readonlyEnv;
  private boolean abortion;
  private boolean authorize;
  private boolean viewer;
  private String inRecord;

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
    isTrafficHint = prior.isTrafficHint;
    webMethod = prior.webMethod;
    cacheObject = prior.cacheObject;
    readonly = false;
    define = false;
    readonlyEnv = prior.readonlyEnv;
    isConstructor = prior.isConstructor;
    isDocumentEvent = prior.isDocumentEvent;
    abortion = prior.abortion;
    authorize = prior.authorize;
    viewer = prior.viewer;
    inRecord = prior.inRecord;
    isFilter = prior.isFilter;
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
    define = false;
    reactiveExpression = false;
    computationContext = ComputeContext.Unknown;
    isStatic = false;
    isPolicy = false;
    isBubble = false;
    isWeb = false;
    isTrafficHint = false;
    webMethod = null;
    cacheObject = null;
    isConstructor = false;
    isDocumentEvent = false;
    abortion = false;
    authorize = false;
    viewer = false;
    inRecord = null;
    isFilter = false;
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

  public boolean hasViewer() { return viewer; }

  public boolean isWeb() {
    return isWeb;
  }

  public boolean isTrafficHint() {
    return isTrafficHint;
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

  /** does the current environment allow aborting */
  public boolean isAbortable() {
    return abortion;
  }

  /** does the current environment exist within an @authorize */
  public boolean isAuthorize() {
    return authorize;
  }

  public boolean isDocumentEvent() {
    return isDocumentEvent;
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
    next.define = true;
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
    next.define = true;
    return next;
  }


  public EnvironmentState scopeInRecord(String name) {
    final var next = new EnvironmentState(this);
    next.inRecord = name;
    return next;
  }

  public boolean shouldDumpRecordMethodSubscriptions(String recordName) {
    if (recordName != null) {
      return recordName.equals(inRecord);
    }
    return false;
  }

  public EnvironmentState scopePolicy() {
    final var next = new EnvironmentState(this);
    next.isPolicy = true;
    next.define = true;
    return next;
  }

  public EnvironmentState scopeFilter() {
    final var next = new EnvironmentState(this);
    next.isFilter = true;
    next.define = true;
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
    next.define = true;
    return next;
  }

  public EnvironmentState scopeTraffic() {
    final var next = new EnvironmentState(this);
    next.isTrafficHint = true;
    return next;
  }

  public EnvironmentState scopeConstructor() {
    final var next = new EnvironmentState(this);
    next.isConstructor = true;
    next.define = true;
    return next;
  }

  public EnvironmentState scopeDocumentEvent() {
    final var next = new EnvironmentState(this);
    next.isDocumentEvent = true;
    next.define = true;
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


  public EnvironmentState scopeDefineLimit() {
    final var next = new EnvironmentState(this);
    next.define = true;
    return next;
  }

  public boolean isDefineBoundary() {
    return define;
  }

  public EnvironmentState scopeAbortion() {
    final var next = new EnvironmentState(this);
    next.abortion = true;
    return next;
  }

  public EnvironmentState scopeViewer() {
    final var next = new EnvironmentState(this);
    next.viewer = true;
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
    next.define = true;
    return next;
  }

  public EnvironmentState scopeTesting() {
    final var next = new EnvironmentState(this);
    next.testing = true;
    return next;
  }

  public EnvironmentState scopeAuthorize() {
    final var next = new EnvironmentState(this);
    next.readonly = true;
    next.readonlyEnv = true;
    next.authorize = true;
    next.define = true;
    return next;
  }

  public EnvironmentState scope() {
    return new EnvironmentState(this);
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
