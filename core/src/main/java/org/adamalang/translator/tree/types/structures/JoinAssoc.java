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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.WatchSet;
import org.adamalang.translator.tree.definitions.DefineAssoc;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.Watcher;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

/** Register a differential join operation on a table */
public class JoinAssoc extends StructureComponent {
  private final Token joinToken;
  private final Token assoc;
  private final Token via;
  public final Token tableName;
  private final Token brackOpen;
  public final Token itemVar;
  private final Token brackClose;
  private final Token fromLabel;
  public final Expression fromExpr;
  private final Token toLabel;
  public final Expression toExpr;
  private final Token semicolon;
  public final WatchSet watching;
  public DefineAssoc foundAssoc;
  public String edgeRecordName;
  private TyType elementType;

  public JoinAssoc(Token joinToken, Token assoc, Token via, Token tableName, Token brackOpen, Token itemVar, Token brackClose, Token fromLabel, Expression fromExpr, Token toLabel, Expression toExpr, Token semicolon) {
    this.joinToken = joinToken;
    this.assoc = assoc;
    this.via = via;
    this.tableName = tableName;
    this.brackOpen = brackOpen;
    this.itemVar = itemVar;
    this.brackClose = brackClose;
    this.fromLabel = fromLabel;
    this.fromExpr = fromExpr;
    this.toLabel = toLabel;
    this.toExpr = toExpr;
    this.semicolon = semicolon;
    this.watching = new WatchSet();
    ingest(joinToken);
    ingest(semicolon);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(joinToken);
    yielder.accept(assoc);
    yielder.accept(via);
    yielder.accept(tableName);
    yielder.accept(brackOpen);
    yielder.accept(itemVar);
    yielder.accept(brackClose);
    yielder.accept(fromLabel);
    fromExpr.emit(yielder);
    yielder.accept(toLabel);
    toExpr.emit(yielder);
    yielder.accept(semicolon);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(joinToken);
    fromExpr.format(formatter);
    toExpr.format(formatter);
    formatter.endLine(semicolon);
  }

  public Environment nextItemEnv(Environment env) {
    Environment itemEnv = env.scopeAsReadOnlyBoundary().scopeWithComputeContext(ComputeContext.Computation);
    itemEnv.define(itemVar.text, elementType, true, this);
    return itemEnv;
  }

  public void typing(final Environment environment, StructureStorage owningStructureStorage) {
    Environment next = environment.watch(Watcher.makeAuto(environment, watching));
    foundAssoc = environment.document.assocs.get(assoc.text);
    String edgeType = null;
    if (foundAssoc == null) {
      environment.document.createError(this, "The assoc '" + assoc.text + "' was not found in the document.");
    } else {
      if (foundAssoc.edgeType != null) {
        edgeType = foundAssoc.edgeType.text;
      }
    }
    FieldDefinition fd = owningStructureStorage.fields.get(tableName.text);
    if (fd == null) {
      environment.document.createError(this, "The table '" + tableName.text + "' was not found within the record.");
    } else {
      if (fd.type instanceof TyReactiveTable) {
        TyReactiveTable rxTable = (TyReactiveTable) fd.type;
        elementType = environment.document.types.get(rxTable.recordName);
        if (elementType != null) {
          edgeRecordName = rxTable.recordName;
          if (edgeType != null && !edgeType.equals(edgeRecordName)) {
            environment.document.createError(this, "The assoc '" + assoc.text + "' requires an edge type of '" + edgeType + "' while being given a table with '" + edgeRecordName + "'.");
          }
          Environment itemEnv = nextItemEnv(next);
          TyType suggestion = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null).withPosition(this);
          TyType fromType = environment.rules.Resolve(fromExpr.typing(itemEnv, suggestion), false);
          TyType toType = environment.rules.Resolve(toExpr.typing(itemEnv, suggestion), false);
          environment.rules.IsInteger(fromType, false);
          environment.rules.IsInteger(toType, false);
        }
      } else {
        environment.document.createError(this, "'" + tableName.text + "' was not a table");
      }
    }
  }
}
