/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TypeCheckerProxy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;

/** Define a service */
public class DefineService extends Definition {
  public static class ServiceAspect extends Definition {
    public final Token name;
    public final Token equals;
    public final Expression expression;
    public final Token semicolon;

    public ServiceAspect(Token name, Token equals, Expression expression, Token semicolon) {
      this.name = name;
      this.equals = equals;
      this.expression = expression;
      this.semicolon = semicolon;
      ingest(name);
      ingest(semicolon);
    }

    @Override
    public void emit(Consumer<Token> yielder) {
      yielder.accept(name);
      yielder.accept(equals);
      expression.emit(yielder);
      yielder.accept(semicolon);
    }

    public void typing(Environment environment) {
      expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    }
  }

  public static class ServiceMethod extends Definition {
    public final Token methodToken;
    public final Token secured;
    public final Token pairOpen;
    public final Token inputTypeName;
    public final Token comma;
    public final Token outputTypeName;
    public final Token outputArrayExt;
    public final Token pairClose;
    public final Token name;
    public final Token semicolon;

    public ServiceMethod(Token methodToken, Token secured, Token pairOpen, Token inputTypeName, Token comma, Token outputTypeName, Token outputArrayExt, Token pairClose, Token name, Token semicolon) {
      this.methodToken = methodToken;
      this.secured = secured;
      this.pairOpen = pairOpen;
      this.inputTypeName = inputTypeName;
      this.comma = comma;
      this.outputTypeName = outputTypeName;
      this.outputArrayExt = outputArrayExt;
      this.pairClose = pairClose;
      this.name = name;
      this.semicolon = semicolon;
      ingest(methodToken);
      ingest(semicolon);
    }

    public boolean requiresSecureCaller() {
      return secured != null;
    }

    @Override
    public void emit(Consumer<Token> yielder) {
      yielder.accept(methodToken);
      if (secured != null) {
        yielder.accept(secured);
      }
      yielder.accept(pairOpen);
      yielder.accept(inputTypeName);
      yielder.accept(comma);
      yielder.accept(outputTypeName);
      if (outputArrayExt != null) {
        yielder.accept(outputArrayExt);
      }
      yielder.accept(pairClose);
      yielder.accept(name);
      yielder.accept(semicolon);
    }

    public void typing(Environment environment) {
      environment.rules.FindMessageStructure(inputTypeName.text, this, false);
      environment.rules.FindMessageStructure(outputTypeName.text, this, false);
    }
  }

  public final Token serviceToken;
  public final Token name;
  public final Token open;
  public final ArrayList<ServiceAspect> aspects;
  public final TreeMap<String, ServiceAspect> aspectsMap;
  public final ArrayList<ServiceMethod> methods;
  public final TreeMap<String, ServiceMethod> methodsMap;
  public final ArrayList<Consumer<Consumer<Token>>> emission;
  public final Token close;

  public DefineService(Token serviceToken, Token name, Token open, ArrayList<ServiceAspect> aspects, ArrayList<ServiceMethod> methods, Token close, ArrayList<Consumer<Consumer<Token>>> emission) {
    this.serviceToken = serviceToken;
    this.name = name;
    this.open = open;
    this.aspects = aspects;
    this.methods = methods;
    this.emission = emission;
    this.close = close;
    ingest(serviceToken);
    ingest(close);
    aspectsMap = new TreeMap<>();
    methodsMap = new TreeMap<>();
    for (ServiceMethod sm : methods) {
      methodsMap.put(sm.name.text, sm);
    }
    for (ServiceAspect sa : aspects) {
      aspectsMap.put(sa.name.text, sa);
    }
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(serviceToken);
    yielder.accept(name);
    yielder.accept(open);
    for (Consumer<Consumer<Token>> e : emission) {
      e.accept(yielder);
    }
    yielder.accept(close);
  }

  public void typing(TypeCheckerProxy checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    checker.register(fe.free, (environment) -> {
      HashSet<String> alreadyDefinedAspects = new HashSet<>();
      for (ServiceAspect aspect : aspects) {
        if (alreadyDefinedAspects.contains(aspect.name.text)) {
          environment.document.createError(this, String.format("'%s' was already defined as an aspect within the service.", aspect.name.text), "ServiceDefine");
        }
        alreadyDefinedAspects.add(aspect.name.text);
        aspect.typing(environment);
      }
      HashSet<String> alreadyDefinedMethods = new HashSet<>();
      for (ServiceMethod method : methods) {
        if (alreadyDefinedMethods.contains(method.name.text)) {
          environment.document.createError(this, String.format("'%s' was already defined as a method within the service.", method.name.text), "ServiceDefine");
        }
        alreadyDefinedMethods.add(method.name.text);
        method.typing(environment);
      }
    });
  }
}
