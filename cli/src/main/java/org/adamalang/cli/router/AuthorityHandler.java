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
package org.adamalang.cli.router;

import org.adamalang.cli.router.Arguments.*;
import org.adamalang.cli.runtime.Output.*;

public interface AuthorityHandler {
  void appendLocal(AuthorityAppendLocalArgs args, YesOrError output) throws Exception;
  void create(AuthorityCreateArgs args, JsonOrError output) throws Exception;
  void createLocal(AuthorityCreateLocalArgs args, YesOrError output) throws Exception;
  void destroy(AuthorityDestroyArgs args, YesOrError output) throws Exception;
  void get(AuthorityGetArgs args, YesOrError output) throws Exception;
  void list(AuthorityListArgs args, JsonOrError output) throws Exception;
  void set(AuthoritySetArgs args, YesOrError output) throws Exception;
  void sign(AuthoritySignArgs args, JsonOrError output) throws Exception;
}
