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
package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.junit.Test;

import java.util.Collections;

public class TypeCheckerStructureTests {
  @Test
  public void sanity() {
    TypeCheckerStructure tcs = new TypeCheckerStructure();
    tcs.issueError(DocumentPosition.ZERO, "message");
    tcs.define(Token.WRAP("x"), Collections.emptySet(), (env) -> {});
    tcs.register(Collections.singleton("x"), (env) -> {});
    TypeCheckerRoot root = new TypeCheckerRoot();
    tcs.transferInto("R", StorageSpecialization.Record, root);
    EnvironmentState es = new EnvironmentState(GlobalObjectPool.createPoolWithStdLib(), CompilerOptions.start().make());
    root.check(Environment.fresh(new Document(), es));
  }
}
