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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineAssoc;
import org.adamalang.translator.tree.types.structures.JoinAssoc;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.Map;
import java.util.TreeSet;

public class CodeGenJoins {
  public static void writeGraphs(final StringBuilderWithTabs sb, final Environment environment) {
    for (Map.Entry<String, DefineAssoc> assoc : environment.document.assocs.entrySet()) {
      DefineAssoc da = assoc.getValue();
      sb.append("RxAssocGraph ___assoc_").append(da.name.text).append(" = new RxAssocGraph();");
    }
    if (environment.document.assocs.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("protected long __computeGraphs() { return 0; }").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("protected long __computeGraphs() {").tabUp().writeNewline();
      sb.append("long __gmemory = 0L;").writeNewline();
      for (Map.Entry<String, DefineAssoc> assoc : environment.document.assocs.entrySet()) {
        DefineAssoc da = assoc.getValue();
        sb.append("___assoc_").append(da.name.text).append(".compute();").writeNewline();
        sb.append("__gmemory += 1024 + ___assoc_").append(da.name.text).append(".memory();").writeNewline();
      }
      sb.append("return __gmemory;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }

  public static void writeJoins(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    for (JoinAssoc ja : storage.joins) {
      // make the edge maker
      String edgeMaker = "__EDMK_" + ja.tableName.text + "_" + environment.autoVariable();
      sb.append("EdgeMaker<RTx").append(ja.edgeRecordName).append("> ").append(edgeMaker).append(" = new EdgeMaker<>() {").tabUp().writeNewline();
      Environment next = ja.nextItemEnv(environment);
      sb.append("@Override").writeNewline();
      sb.append("public Integer from(RTx").append(ja.edgeRecordName).append(" ").append(ja.itemVar.text).append(") {").tabUp().writeNewline();
      sb.append("return ");
      ja.fromExpr.writeJava(sb, next);
      // TODO: resolve if maybe
      sb.append(";").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public Integer to(RTx").append(ja.edgeRecordName).append(" ").append(ja.itemVar.text).append(") {").tabUp().writeNewline();
      sb.append("return ");
      ja.toExpr.writeJava(sb, next);
      // TODO: resolve if maybe
      sb.append(";").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("};").writeNewline();
      String tracker = "__DET_" + environment.autoVariable();
      sb.append("DifferentialEdgeTracker<RTx").append(ja.edgeRecordName).append("> ").append(tracker).append(" = new DifferentialEdgeTracker<>(").append(ja.tableName.text).append(",___assoc_").append("" + ja.foundAssoc.name.text).append(",").append(edgeMaker).append(");").writeNewline();
      TreeSet<String> variablesToWatch = new TreeSet<>();
      variablesToWatch.addAll(ja.watching.variables);
      variablesToWatch.addAll(ja.watching.pubsub);
      for(String depend : variablesToWatch) {
        sb.append(depend).append(".__subscribe(").append(tracker).append(");").writeNewline();
      }
      sb.append(ja.tableName.text).append(".pump(").append(tracker).append(");").writeNewline();
    }
  }
}
