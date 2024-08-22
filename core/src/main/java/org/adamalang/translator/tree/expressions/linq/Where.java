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
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.codegen.CodeGenIndexing;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.FieldLookup;
import org.adamalang.translator.tree.expressions.Lookup;
import org.adamalang.translator.tree.expressions.operators.BinaryExpression;
import org.adamalang.translator.tree.expressions.operators.Parentheses;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.watcher.LambdaWatcher;
import org.adamalang.translator.tree.watcher.RuntimeExceptionWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/** performs a filter on an sql statemeent ($sql) where (expr) */
public class Where extends LinqExpression implements LatentCodeSnippet {
  public final Expression expression;
  public final Token tokenWhere;
  private final Token aliasToken;
  private final ArrayList<String> applyQuerySetStatements;
  private final TreeMap<String, String> closureTypes;
  private final TreeMap<String, TyType> closureTyTypes;
  private final Token colonToken;
  private final StringBuilder exprCode;
  private final StringBuilder primaryKeyExpr;
  private final ArrayList<String> trapBuilder;
  private int generatedClassId;
  private String iterType;
  private StructureStorage structureStorage;
  private boolean writtenDependentExpressionsForClosure;

  public Where(final Expression sql, final Token tokenWhere, final Token aliasToken, final Token colonToken, final Expression expression) {
    super(sql);
    this.tokenWhere = tokenWhere;
    this.expression = expression;
    this.aliasToken = aliasToken;
    this.colonToken = colonToken;
    ingest(sql);
    ingest(expression);
    structureStorage = null;
    trapBuilder = new ArrayList<>();
    closureTypes = new TreeMap<>();
    generatedClassId = -1;
    iterType = "?";
    exprCode = new StringBuilder();
    primaryKeyExpr = new StringBuilder();
    applyQuerySetStatements = new ArrayList<>();
    closureTyTypes = new TreeMap<>();
    writtenDependentExpressionsForClosure = false;
  }

  public static Expression findIndex(final Expression root, final String aliasName, final String indexName, BinaryOp mode) {
    if (root instanceof Parentheses) {
      return findIndex(((Parentheses) root).expression, aliasName, indexName, mode);
    }
    if (root instanceof BinaryExpression) {
      // if it is an &&, then search both branches
      if (((BinaryExpression) root).op == BinaryOp.LogicalAnd) {
        final var left = findIndex(((BinaryExpression) root).left, aliasName, indexName, mode);
        if (left != null) {
          return left;
        }
        return findIndex(((BinaryExpression) root).right, aliasName, indexName, mode);
      }
      if (((BinaryExpression) root).op == mode) {
        if (isExpressionIndexedVariable(((BinaryExpression) root).left, aliasName, indexName)) {
          return ((BinaryExpression) root).right;
        } else if (isExpressionIndexedVariable(((BinaryExpression) root).right, aliasName, indexName)) {
          return ((BinaryExpression) root).left;
        }
      }
    }
    return null;
  }

  public static boolean isExpressionIndexedVariable(final Expression root, final String aliasName, final String variableCheck) {
    if (root instanceof Parentheses) {
      return isExpressionIndexedVariable(((Parentheses) root).expression, aliasName, variableCheck);
    }
    if (aliasName == null && root instanceof Lookup) {
      return variableCheck.equals(((Lookup) root).variableToken.text);
    } else if (aliasName != null && root instanceof FieldLookup) {
      if (((FieldLookup) root).fieldNameToken.text.equals(variableCheck)) {
        return isExpressionIndexedVariable(((FieldLookup) root).expression, null, aliasName);
      }
    }
    return false;
  }

  private boolean buildSoloIndex(final Environment environment, Expression expr, boolean allowPrimaryKey) {
    final var intersectCodeByName = new TreeMap<String, String>();
    final var intersectModeByName = new TreeMap<String, String>();
    String primaryExpr = null;
    boolean added = false;
    for (final Map.Entry<String, FieldDefinition> entry : structureStorage.fields.entrySet()) {
      boolean notIndex = !structureStorage.indexSet.contains(entry.getKey());
      boolean primary = entry.getKey().equals("id");
      if (notIndex && !primary) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      CodeGenIndexing.IndexClassification classification = new CodeGenIndexing.IndexClassification(fieldType);
      if (classification.good) {
        // Here is where we wrap this to search for <, <=, ==, >=, >
        // This will let us complete #20 for integers and enums
        var indexValue = findIndex(expr, aliasToken != null ? aliasToken.text : null, entry.getKey(), BinaryOp.Equal);
        if (!primary) {
          String indexLookupMode = "IndexQuerySet.LookupMode.Equals";
          if (indexValue == null && classification.isIntegral) {
            for (BinaryOp mode : new BinaryOp[] {BinaryOp.LessThan, BinaryOp.LessThanOrEqual, BinaryOp.GreaterThan, BinaryOp.GreaterThanOrEqual}) {
              indexValue = findIndex(expr, aliasToken != null ? aliasToken.text : null, entry.getKey(), mode);
              if (indexValue != null) {
                indexLookupMode = "IndexQuerySet.LookupMode." + mode;
                break;
              }
            }
          }
          if (indexValue != null) {
            TyType indexValueType = indexValue.getCachedType();
            boolean isMaybe = indexValueType instanceof TyNativeMaybe || indexValueType instanceof TyReactiveMaybe;
            intersectModeByName.put(entry.getKey(), indexLookupMode);
            var indexValueString = compileIndexExpr(indexValue, environment);
            if (indexValueString != null) {
              if (classification.useHashCode) {
                if (isMaybe) {
                  indexValueString = "(" + indexValueString + ").unpack((__item) -> __item.hashCode())";
                } else {
                  indexValueString += ".hashCode()";
                }
              }
              if (classification.requiresToInt) {
                if (isMaybe) {
                  indexValueString = "(" + indexValueString + ").unpack((__item) -> __item.toInt())";
                } else {
                  indexValueString += ".toInt()";
                }
              }
              if (classification.isBoolean) {
                if (isMaybe) {
                  indexValueString = "(" + indexValueString + ").unpack((__item) -> __item ? 1 : 0)";
                } else {
                  indexValueString = "((" + indexValueString + ") ? 1 : 0)";
                }
              }
              intersectCodeByName.put(entry.getKey(), indexValueString);
            }
          }
        } else {
          primaryExpr = compileIndexExpr(indexValue, environment);
        }
      }
    }
    for (var k = 0; k < structureStorage.indices.size(); k++) {
      String nameToUse = structureStorage.indices.get(k).nameToken.text;
      final var code = intersectCodeByName.get(nameToUse);
      if (code != null) {
        applyQuerySetStatements.add("__set.intersect(" + k + ", " + code + ", "+intersectModeByName.get(nameToUse)+");");
        added = true;
      }
    }
    if (primaryExpr != null && allowPrimaryKey) {
      applyQuerySetStatements.add("__set.primary(" + primaryExpr + ");");
      added = true;
    }
    return added;
  }

  private String compileIndexExpr(final Expression indexValue, final Environment prior) {
    try {
      final var nope = prior.watch(new RuntimeExceptionWatcher()).scopeWithComputeContext(ComputeContext.Computation);
      for (final Map.Entry<String, TyType> whatIs : closureTyTypes.entrySet()) {
        nope.define(whatIs.getKey(), whatIs.getValue(), true, whatIs.getValue());
      }
      final var str = new StringBuilder();
      indexValue.writeJava(str, nope);
      return str.toString();
    } catch (final Throwable t) {
      return null;
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(tokenWhere);
    if (aliasToken != null) {
      yielder.accept(aliasToken);
      yielder.accept(colonToken);
    }
    expression.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    sql.format(formatter);
    expression.format(formatter);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    generatedClassId = environment.document.inventClassId();
    environment.document.add(this);
    // the environment must have a read boundary for native types, because we about
    // to misbehave
    final var typeFrom = sql.typing(environment, null);
    if (typeFrom != null && environment.rules.IsNativeListOfStructure(typeFrom, false)) {
      final var storageType = (IsStructure) environment.rules.Resolve(((TyNativeList) environment.rules.Resolve(typeFrom, false)).elementType, false);
      structureStorage = storageType.storage();
      final var watch = environment.watch(new LambdaWatcher(environment, closureTyTypes, closureTypes)).captureSpecials();
      HashMap<String, TyType> specialsUsed = watch.specials();
      final var next = watch.scopeWithComputeContext(ComputeContext.Computation);
      iterType = "RTx" + storageType.name();
      var toUse = next;
      if (aliasToken != null) {
        next.define(aliasToken.text, (TyType) storageType, true, new DocumentPosition().ingest(aliasToken));
      } else {
        toUse = next.trap(name -> {
          final var result = next.lookupDirect(name);
          if (result != null) {
            return result;
          }
          final var fieldDef = structureStorage.fields.get(name);
          TyType resolvedType = null;
          if (fieldDef != null) {
            resolvedType = RuleSetCommon.Resolve(environment, fieldDef.type, false);
            var addGet = false;
            if (resolvedType instanceof DetailComputeRequiresGet) {
              addGet = true;
              resolvedType = RuleSetCommon.Resolve(environment, ((DetailComputeRequiresGet) resolvedType).typeAfterGet(environment), false);
            }
            if (resolvedType != null) {
              final var nativeType = resolvedType.getJavaConcreteType(environment);
              final var trapToAdd = nativeType + " " + name + " = __obj." + name + (addGet ? ".get();" : ";");
              trapBuilder.add(trapToAdd);
              next.define(name, resolvedType, true, resolvedType);
            }
          }
          return resolvedType;
        });
      }
      final var expressionType = expression.typing(toUse, null);
      for (Map.Entry<String, TyType> entry : specialsUsed.entrySet()) {
        closureTyTypes.put(entry.getKey(), entry.getValue());
        closureTypes.put(entry.getKey(), entry.getValue().getJavaConcreteType(environment));
      }
      environment.rules.IsBoolean(expressionType, false);
      return typeFrom.makeCopyWithNewPosition(this, typeFrom.behavior);
    }
    return null;
  }

  public void branches(Expression expression, ArrayList<Expression> results) {
    if (expression instanceof Parentheses) {
      branches(((Parentheses) expression).expression, results);
    } else if (expression instanceof BinaryExpression) {
      // if it is an &&, then search both branches
      if (((BinaryExpression) expression).op == BinaryOp.LogicalOr) {
        branches(((BinaryExpression) expression).left, results);
        branches(((BinaryExpression) expression).right, results);
      } else {
        results.add(expression);
      }
    } else {
      results.add(expression);
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (passedTypeChecking() && structureStorage != null) {
      sql.writeJava(sb, environment);
      sb.append(".where(").append(intermediateExpression ? "false, " : "true, ").append("new __CLOSURE_WhereClause" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getKey());
      }
      // list the variables
      sb.append("))");
      if (!writtenDependentExpressionsForClosure) {
        expression.writeJava(exprCode, environment.scopeWithComputeContext(ComputeContext.Computation));
        var primaryKey = findIndex(expression, aliasToken != null ? aliasToken.text : null, "id", BinaryOp.Equal);
        if (primaryKey != null) {
          FreeEnvironment fe = FreeEnvironment.root();
          primaryKey.free(fe);
          for (String testField : fe.free) {
            if (structureStorage.fields.containsKey(testField)) {
              primaryKey = null;
              break;
            }
          }
        }
        if (primaryKey != null) {
          boolean forceId = false;
          if (primaryKey.typing(environment, null) instanceof TyNativeMaybe) {
            primaryKeyExpr.append("LibMath.forceId(");
            forceId = true;
          }
          primaryKey.writeJava(primaryKeyExpr, environment.scopeWithComputeContext(ComputeContext.Computation));
          if (forceId) {
            primaryKeyExpr.append(")");
          }
        } else {
          primaryKeyExpr.append("null");
        }
        ArrayList<Expression> foundBranches = new ArrayList<>();
        branches(expression, foundBranches);
        if (foundBranches.size() <= 1) {
          buildSoloIndex(environment.scopeWithComputeContext(ComputeContext.Computation), expression, false);
        } else {
          int pushesRemain = foundBranches.size() - 1;
          for(Expression branch : foundBranches) {
            buildSoloIndex(environment.scopeWithComputeContext(ComputeContext.Computation), branch, true);
            if (pushesRemain > 0) {
              applyQuerySetStatements.add("__set.push();");
            }
            pushesRemain--;
          }
        }
        writtenDependentExpressionsForClosure = true;
      }
    }
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    sb.append("private class __CLOSURE_WhereClause" + generatedClassId + " implements WhereClause<" + iterType + "> {").tabUp().writeNewline();
    for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
      sb.append("private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public void scopeByIndicies(IndexQuerySet __set) {").tabUp().writeNewline();
    if ((applyQuerySetStatements.size() > 0)) {
      sb.append("__code_cost += ").append("" + (applyQuerySetStatements.size() * 10)).append(";").writeNewline(); // a bad estimate, but indexing isn't free
    }
    for (var k = 0; k < applyQuerySetStatements.size(); k++) {
      sb.append(applyQuerySetStatements.get(k)).writeNewline();
    }
    sb.append("__set.finish();").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public Integer getPrimaryKey() {").tabUp().writeNewline();
    sb.append("return ").append(primaryKeyExpr.toString()).append(";");
    sb.tabDown().writeNewline().append("}").writeNewline();
    if (closureTypes.size() > 0) {
      sb.append("private __CLOSURE_WhereClause" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getValue()).append(" ").append(entry.getKey());
      }
      sb.append(") {").tabUp().writeNewline();
      var untilTabDown = closureTypes.size();
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        sb.append("this." + entry.getKey() + " = " + entry.getKey() + ";");
        if (--untilTabDown <= 0) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public boolean test(").append(iterType);
    if (aliasToken != null) {
      sb.append(" ").append(aliasToken.text).append(") {");
    } else {
      sb.append(" __obj) {");
    }
    sb.tabUp().writeNewline();
    for (final String trapToWrite : trapBuilder) {
      sb.append(trapToWrite).writeNewline();
    }
    sb.append(String.format("__code_cost ++;")).writeNewline();
    sb.append("return " + exprCode.toString() + ";").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    FreeEnvironment next = environment;
    if (aliasToken != null) {
      next = next.push();
      next.define(aliasToken.text);
    }
    expression.free(next);
  }
}
