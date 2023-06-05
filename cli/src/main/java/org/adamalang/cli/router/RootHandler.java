package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Output.*;
import org.adamalang.cli.router.Arguments.*;

public interface RootHandler {
  SpaceHandler makeSpaceHandler();
  AuthorityHandler makeAuthorityHandler();
  void init(InitArgs args, YesOrError output) throws Exception;
}