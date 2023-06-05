package org.adamalang.cli.router;

import org.adamalang.cli.router.Arguments.*;
import org.adamalang.cli.runtime.Output.*;

public interface AuthorityHandler {
  void create(AuthorityCreateArgs args, YesOrError output) throws Exception;
  void set(AuthoritySetArgs args, YesOrError output) throws Exception;
  void get(AuthorityGetArgs args, YesOrError output) throws Exception;
  void destroy(AuthorityDestroyArgs args, YesOrError output) throws Exception;
  void list(AuthorityListArgs args, YesOrError output) throws Exception;
  void createLocal(AuthorityCreateLocalArgs args, YesOrError output) throws Exception;
  void appendLocal(AuthorityAppendLocalArgs args, YesOrError output) throws Exception;
  void sign(AuthoritySignArgs args, YesOrError output) throws Exception;
}