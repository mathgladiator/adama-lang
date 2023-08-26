/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
  void deinit(DeinitArgs args, YesOrError output) throws Exception;
  void dumpenv(DumpenvArgs args, YesOrError output) throws Exception;
  void init(InitArgs args, YesOrError output) throws Exception;
  void kickstart(KickstartArgs args, YesOrError output) throws Exception;
}
