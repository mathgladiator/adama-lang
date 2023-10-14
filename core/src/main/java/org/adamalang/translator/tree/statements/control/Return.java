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
package org.adamalang.translator.tree.statements.control;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.structures.FieldDefinition;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

/** return from the current function (and maybe with a value) */
public class Return extends Statement {
  public final Expression expression;
  public final Token returnToken;
  public final Token semicolonToken;
  private TreeSet<String> webFields;
  private TyNativeMessage webReturnType;

  public Return(final Token returnToken, final Expression expression, final Token semicolonToken) {
    this.returnToken = returnToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    webFields = null;
    ingest(returnToken);
    ingest(semicolonToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(returnToken);
    if (expression != null) {
      expression.emit(yielder);
    }
    yielder.accept(semicolonToken);
  }

  private boolean consider(String field, TyNativeMessage message, Consumer<TyType> check) {
    FieldDefinition fd = message.storage.fields.get(field);
    if (fd != null) {
      check.accept(fd.type);
      webFields.add(field);
      return true;
    }
    return false;
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    if (environment.state.isWeb()) {
      final var givenReturnType = environment.rules.Resolve(expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null), true);
      if (givenReturnType instanceof TyNativeMessage) {
        String method = environment.state.getWebMethod();
        webFields = new TreeSet<>();
        webReturnType = (TyNativeMessage) givenReturnType;
        int body = 0;
        if (consider("html", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("sign", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("xml", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("js", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("css", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("error", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("json", webReturnType, (ty) -> environment.rules.IsNativeMessage(ty, false))) {
          body++;
        }
        if (consider("redirect", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("forward", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("identity", webReturnType, (ty) -> environment.rules.IsString(ty, false))) {
          body++;
        }
        if (consider("asset", webReturnType, (ty) -> environment.rules.IsAsset(ty, false))) {
          consider("asset_transform", webReturnType, (ty) -> environment.rules.IsString(ty, false));
          body++;
        }
        consider("cors", webReturnType, (ty) -> environment.rules.IsBoolean(ty, false));
        consider("cache_ttl_seconds", webReturnType, (ty) -> environment.rules.IsInteger(ty, false));
        if (method.equals("options")) {
          if (body != 0) {
            environment.document.createError(this, String.format("The return statement within a @web expects no body fields; got " + body + " instead"));
          }
        } else {
          if (body != 1) {
            environment.document.createError(this, String.format("The return statement within a @web expects exactly one body type; got " + body + " instead"));
          }
        }
      } else {
        environment.document.createError(this, String.format("The return statement within a @web expects a message type"));
      }
      return ControlFlow.Returns;
    } else {
      final var expectedReturnType = environment.getMostRecentReturnType();
      if (expression != null) {
        if (expectedReturnType != null) {
          final var givenReturnType = expression.typing(environment.scopeWithComputeContext(ComputeContext.Computation), expectedReturnType);
          if (!environment.rules.CanTypeAStoreTypeB(expectedReturnType, givenReturnType, StorageTweak.None, false)) {
            return ControlFlow.Open;
          }
        } else {
          environment.document.createError(this, String.format("The return statement expects no expression"));
        }
      } else if (expectedReturnType != null) {
        environment.document.createError(this, String.format("The return statement expected an expression of type `%s`", expectedReturnType.getAdamaType()));
      }
      return ControlFlow.Returns;
    }
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.state.isWeb()) {
      if (webFields != null) {
        sb.append("{").tabUp().writeNewline();
        String exprName = "__capture" + environment.autoVariable();
        sb.append("RTx").append(webReturnType.name).append(" ").append(exprName).append(" = ");
        expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(";").writeNewline();
        sb.append("return new WebResponse()");
        for (String webField : webFields) {
          sb.append(".").append(webField).append("(").append(exprName).append(".").append(webField).append(")");
        }
        sb.append(";").tabDown().writeNewline();
        sb.append("}");
      }
    } else {
      sb.append("return");
      if (expression != null) {
        sb.append(" ");
        expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      }
      sb.append(";");
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    if (expression != null) {
      expression.free(environment);
    }
  }
}
