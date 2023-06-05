package org.adamalang.cli.router;

import org.adamalang.cli.router.Arguments.*;
import org.adamalang.cli.runtime.Output.*;

public interface SpaceHandler {
  void create(SpaceCreateArgs args, YesOrError output) throws Exception;
  void delete(SpaceDeleteArgs args, YesOrError output) throws Exception;
  void deploy(SpaceDeployArgs args, YesOrError output) throws Exception;
  void setRxhtml(SpaceSetRxhtmlArgs args, YesOrError output) throws Exception;
  void getRxhtml(SpaceGetRxhtmlArgs args, YesOrError output) throws Exception;
  void upload(SpaceUploadArgs args, YesOrError output) throws Exception;
  void download(SpaceDownloadArgs args, YesOrError output) throws Exception;
  void list(SpaceListArgs args, YesOrError output) throws Exception;
  void usage(SpaceUsageArgs args, YesOrError output) throws Exception;
  void reflect(SpaceReflectArgs args, YesOrError output) throws Exception;
  void setRole(SpaceSetRoleArgs args, YesOrError output) throws Exception;
  void generateKey(SpaceGenerateKeyArgs args, YesOrError output) throws Exception;
  void encryptSecret(SpaceEncryptSecretArgs args, YesOrError output) throws Exception;
}