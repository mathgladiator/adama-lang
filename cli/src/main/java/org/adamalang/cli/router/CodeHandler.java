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

public interface CodeHandler {
  void bundlePlan(CodeBundlePlanArgs args, YesOrError output) throws Exception;
  void compileFile(CodeCompileFileArgs args, YesOrError output) throws Exception;
  void diagram(CodeDiagramArgs args, YesOrError output) throws Exception;
  void lsp(CodeLspArgs args, YesOrError output) throws Exception;
  void reflectDump(CodeReflectDumpArgs args, YesOrError output) throws Exception;
  void validatePlan(CodeValidatePlanArgs args, YesOrError output) throws Exception;
}
