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
package org.adamalang.runtime.remote.replication;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;
import org.junit.Test;

public class TombStoneTests {
  @Test
  public void flowingCoverageOfGlory() {
    final String json;
    {
      TombStone ts = new TombStone("service", "method", "key");
      JsonStreamWriter writer = new JsonStreamWriter();
      ts.dump(writer);
      json = writer.toString();
    }
    Assert.assertEquals("{\"s\":\"service\",\"m\":\"method\",\"k\":\"key\"}", json);
    TombStone t = TombStone.read(new JsonStreamReader(json));
    Assert.assertEquals("service", t.service);
    Assert.assertEquals("method", t.method);
    Assert.assertEquals("key", t.key);
  }

  @Test
  public void skipNonObject() {
    Assert.assertNull(TombStone.read(new JsonStreamReader("null")));
    Assert.assertNull(TombStone.read(new JsonStreamReader("123")));
    Assert.assertNull(TombStone.read(new JsonStreamReader("true")));
    Assert.assertNull(TombStone.read(new JsonStreamReader("[123,42]")));
  }

  @Test
  public void skipJunk() {
    TombStone t = TombStone.read(new JsonStreamReader("{\"x\":123,\"s\":\"service\",\"nope\":true,\"m\":\"method\",\"yo\":[{}],\"k\":\"key\"}"));
    Assert.assertEquals("service", t.service);
    Assert.assertEquals("method", t.method);
    Assert.assertEquals("key", t.key);
  }

  @Test
  public void generated() {
    TombStone t = TombStone.read(new JsonStreamReader("{\"x\":123,\"s\":\"service\",\"nope\":true,\"m\":\"method\",\"yo\":[{}],\"k\":\"key\"}"));
    TombStone t2 = TombStone.read(new JsonStreamReader("{\"x\":8,\"s\":\"service\",\"nope\":false,\"m\":\"method\",\"yo\":[{}],\"k\":\"key\"}"));
    Assert.assertEquals(761482098, t.hashCode());
    Assert.assertEquals(t, t);
    Assert.assertEquals(t, t2);
    Assert.assertNotEquals(t, null);
    Assert.assertNotEquals(t, "nope");
  }
}
