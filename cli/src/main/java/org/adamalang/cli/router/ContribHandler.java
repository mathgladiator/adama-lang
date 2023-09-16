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

public interface ContribHandler {
  void bundleJs(ContribBundleJsArgs args, YesOrError output) throws Exception;
  void copyright(ContribCopyrightArgs args, YesOrError output) throws Exception;
  void makeApi(ContribMakeApiArgs args, YesOrError output) throws Exception;
  void makeBook(ContribMakeBookArgs args, YesOrError output) throws Exception;
  void makeCli(ContribMakeCliArgs args, YesOrError output) throws Exception;
  void makeCodec(ContribMakeCodecArgs args, YesOrError output) throws Exception;
  void makeEmbed(ContribMakeEmbedArgs args, YesOrError output) throws Exception;
  void makeEt(ContribMakeEtArgs args, YesOrError output) throws Exception;
  void strTemp(ContribStrTempArgs args, YesOrError output) throws Exception;
  void testsAdama(ContribTestsAdamaArgs args, YesOrError output) throws Exception;
  void testsRxhtml(ContribTestsRxhtmlArgs args, YesOrError output) throws Exception;
  void version(ContribVersionArgs args, YesOrError output) throws Exception;
}
