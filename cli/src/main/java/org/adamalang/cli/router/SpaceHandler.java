/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.router;

import org.adamalang.cli.router.Arguments.*;
import org.adamalang.cli.runtime.Output.*;

public interface SpaceHandler {
  void create(SpaceCreateArgs args, YesOrError output) throws Exception;
  void delete(SpaceDeleteArgs args, YesOrError output) throws Exception;
  void deploy(SpaceDeployArgs args, YesOrError output) throws Exception;
  void developers(SpaceDevelopersArgs args, JsonOrError output) throws Exception;
  void encryptSecret(SpaceEncryptSecretArgs args, YesOrError output) throws Exception;
  void generateKey(SpaceGenerateKeyArgs args, YesOrError output) throws Exception;
  void get(SpaceGetArgs args, YesOrError output) throws Exception;
  void getRxhtml(SpaceGetRxhtmlArgs args, YesOrError output) throws Exception;
  void list(SpaceListArgs args, JsonOrError output) throws Exception;
  void reflect(SpaceReflectArgs args, YesOrError output) throws Exception;
  void setRole(SpaceSetRoleArgs args, YesOrError output) throws Exception;
  void setRxhtml(SpaceSetRxhtmlArgs args, YesOrError output) throws Exception;
  void upload(SpaceUploadArgs args, JsonOrError output) throws Exception;
  void usage(SpaceUsageArgs args, JsonOrError output) throws Exception;
}
