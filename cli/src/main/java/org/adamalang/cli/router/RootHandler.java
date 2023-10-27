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

import org.adamalang.cli.runtime.Output.*;
import org.adamalang.cli.router.Arguments.*;

public interface RootHandler {
  SpaceHandler makeSpaceHandler();
  AuthorityHandler makeAuthorityHandler();
  AccountHandler makeAccountHandler();
  CodeHandler makeCodeHandler();
  ContribHandler makeContribHandler();
  DatabaseHandler makeDatabaseHandler();
  DocumentHandler makeDocumentHandler();
  DomainHandler makeDomainHandler();
  FrontendHandler makeFrontendHandler();
  ServicesHandler makeServicesHandler();
  void canary(CanaryArgs args, YesOrError output) throws Exception;
  void deinit(DeinitArgs args, YesOrError output) throws Exception;
  void devbox(DevboxArgs args, YesOrError output) throws Exception;
  void dumpenv(DumpenvArgs args, YesOrError output) throws Exception;
  void init(InitArgs args, YesOrError output) throws Exception;
  void kickstart(KickstartArgs args, YesOrError output) throws Exception;
  void version(VersionArgs args, YesOrError output) throws Exception;
}
