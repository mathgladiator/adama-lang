/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.DefineService;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetIngestion;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetMessages;
import org.adamalang.translator.tree.types.natives.TyNativeDynamic;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class ReplicationDefinition extends StructureComponent  {
  private final Token replication;
  private final Token open;
  public final Token service;
  private final Token split;
  public final Token method;
  private final Token close;
  public final Token name;
  private final Token equals;
  public final Expression expression;
  private final Token end;
  public final LinkedHashSet<String> servicesToWatch;
  public final LinkedHashSet<String> variablesToWatch;

  public ReplicationDefinition(Token replication, Token open, Token service, Token split, Token method, Token close, Token name, Token equals, Expression expression, Token end) {
    this.replication = replication;
    this.open = open;
    this.service = service;
    this.split = split;
    this.method = method;
    this.close = close;
    this.name = name;
    this.equals = equals;
    this.expression = expression;
    this.end = end;
    ingest(open);
    ingest(end);
    servicesToWatch = new LinkedHashSet<>();
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(replication);
    yielder.accept(open);
    yielder.accept(service);
    yielder.accept(split);
    yielder.accept(method);
    yielder.accept(close);
    yielder.accept(name);
    yielder.accept(equals);
    expression.emit(yielder);
    yielder.accept(end);
  }

  public void typing(Environment prior) {
    Environment env = prior.scopeWithComputeContext(ComputeContext.Computation);
    DefineService definition = env.document.services.get(service.text);
    if (definition == null) {
      env.document.createError(ReplicationDefinition.this, "The service '" + service.text + "' was not found for replication at '" + name.text + "'");
      return;
    }
    DefineService.ServiceReplication replication = definition.replicationsMap.get(method.text);
    if (replication == null) {
      env.document.createError(ReplicationDefinition.this, "The service '" + service.text + "' was had no replication for '" + method.text + "'");
      return;
    }
    if ("dynamic".equals(replication.inputTypeName.text)) {
      TyType type = expression.typing(env, new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null));
      RuleSetCommon.IsDynamic(env, type, false);
    } else {
      TyType expectedType = env.document.types.get(replication.inputTypeName.text);
      if (RuleSetMessages.IsNativeMessage(env, expectedType, false)) {
        TyType type = env.rules.Resolve(expression.typing(env, expectedType), false);
        if (RuleSetMessages.IsNativeMessage(env, type, false)) {
          RuleSetIngestion.CanAIngestB(env, expectedType, type, false);
        }
      }
    }
  }
}
