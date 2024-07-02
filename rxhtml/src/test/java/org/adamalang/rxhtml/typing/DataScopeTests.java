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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.Consumer;

public class DataScopeTests {

  public static class ErrorAccum implements Consumer<String> {
    public ArrayList<String> log;

    public ErrorAccum() {
      this.log = new ArrayList<>();
    }

    @Override
    public void accept(String e) {
      System.err.println(e);
      log.add(e);
    }
  };

  public static class UsedAccum implements UnusedReport {
    public TreeSet<String> set;
    public UsedAccum() {
      this.set = new TreeSet<>();
    }
    @Override
    public void reportUnused(String type, String field) {
      set.add(type + "::" + field);
    }
  }

  @Test
  public void scope_iterate_into_root_list() {
    ObjectNode forest = Json.parseJsonObject("{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":" + //
        "{\"_users\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"User\"},\"privacy\":\"private\"}" + //
        ",\"_projects\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"Project\"},\"privacy\":\"private\"}," + //
        "\"projects\":{\"type\":{\"nature\":\"native_list\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"Project\"}},\"privacy\":\"public\"}," +
        "\"others\":{\"type\":{\"nature\":\"native_list\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"User\"}},\"privacy\":\"public\"}," +
        "\"current_project\":{\"type\":{\"nature\":\"native_maybe\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"Project\"}},\"privacy\":\"bubble\"}," +
        "\"current_task\":{\"type\":{\"nature\":\"native_maybe\",\"type\":{\"nature\":\"native_ref\",\"ref\":\"Task\"}},\"privacy\":\"bubble\"}}}," +
        "\"User\":{\"nature\":\"reactive_record\",\"name\":\"User\",\"fields\":{\"id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"}," +
        "\"who\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"principal\"},\"privacy\":\"private\"},\"name\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"public\"}," +
        "\"email\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"public\"},\"password_hash\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"private\"},\"temp_password_hash\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"private\"},\"temp_password_hash_expires\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"datetime\"},\"privacy\":\"private\"}}}}}");

    UsedAccum reportA = new UsedAccum();
    UnusedReport.drive(forest, reportA);
    DataScope scope = DataScope.root(forest);
    ErrorAccum ea = new ErrorAccum();
    PrivacyFilter privacy = new PrivacyFilter();
    DataSelector ds = scope.select(privacy, "projects", ea);
    Assert.assertEquals("[__Root]", ds.scope.toString());
    DataScope next = ds.iterateInto(ea);
    Assert.assertEquals("[__Root][list:Project][Project]", next.toString());
    UsedAccum reportB = new UsedAccum();
    UnusedReport.drive(forest, reportB);
    Assert.assertTrue(reportA.set.contains("__Root::projects"));
    Assert.assertFalse(reportB.set.contains("__Root::projects"));
  }
}
