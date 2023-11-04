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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;
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

  @Override
  public void format(Formatter formatter) {
    expression.format(formatter);
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
