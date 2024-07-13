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
package org.adamalang.translator.tree.types.reactive;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;
import org.junit.Assert;
import org.junit.Test;

public class TyReactiveReplicationStatusTests {
  @Test
  public void coverage() {
    ReplicationDefinition defn = new ReplicationDefinition(null, null, Token.WRAP("service"), null, Token.WRAP("method"), null, Token.WRAP("name"), null, null, null);
    TyReactiveReplicationStatus ty = new TyReactiveReplicationStatus(defn);
    ty.format(null);
    ty.emitInternal((t) -> {});
    Assert.assertEquals("replication", ty.getAdamaType());
    Assert.assertEquals("RxReplicationStatus", ty.getJavaBoxType(null));
    Assert.assertEquals("RxReplicationStatus", ty.getJavaConcreteType(null));
    ty.makeCopyWithNewPositionInternal(DocumentPosition.ZERO, TypeBehavior.ReadWriteNative);
    ty.typing(null);
    JsonStreamWriter writer = new JsonStreamWriter();
    ty.writeTypeReflectionJson(writer, ReflectionSource.Root);
    Assert.assertEquals("{\"nature\":\"reactive_value\",\"type\":\"replication_status\"}", writer.toString());
    Assert.assertEquals("DReplicationStatus", ty.getDeltaType(null));
    Assert.assertNull(ty.lookupMethod("nope", null));
  }
}
