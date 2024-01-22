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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.structures.JoinAssoc;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.TreeSet;

public class CodeGenJoins {

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
      sb.append("DifferentialEdgeTracker<RTx").append(ja.edgeRecordName).append("> ").append(tracker).append(" = new DifferentialEdgeTracker<>(").append(ja.tableName.text).append(",__graph.getOrCreate((short)").append("" + ja.foundAssoc.id).append("),").append(edgeMaker).append(");").writeNewline();
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
