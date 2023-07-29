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

public interface FrontendHandler {
  void devServer(FrontendDevServerArgs args, YesOrError output) throws Exception;
  void edhtml(FrontendEdhtmlArgs args, YesOrError output) throws Exception;
  void make200(FrontendMake200Args args, YesOrError output) throws Exception;
  void rxhtml(FrontendRxhtmlArgs args, YesOrError output) throws Exception;
  void setLibadama(FrontendSetLibadamaArgs args, YesOrError output) throws Exception;
  void studyCss(FrontendStudyCssArgs args, YesOrError output) throws Exception;
  void wrapCss(FrontendWrapCssArgs args, YesOrError output) throws Exception;
}
