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

public interface ContribHandler {
  void bundleJs(ContribBundleJsArgs args, YesOrError output) throws Exception;
  void copyright(ContribCopyrightArgs args, YesOrError output) throws Exception;
  void makeApi(ContribMakeApiArgs args, YesOrError output) throws Exception;
  void makeCli(ContribMakeCliArgs args, YesOrError output) throws Exception;
  void makeCodec(ContribMakeCodecArgs args, YesOrError output) throws Exception;
  void makeEt(ContribMakeEtArgs args, YesOrError output) throws Exception;
  void testsAdama(ContribTestsAdamaArgs args, YesOrError output) throws Exception;
  void testsRxhtml(ContribTestsRxhtmlArgs args, YesOrError output) throws Exception;
  void version(ContribVersionArgs args, YesOrError output) throws Exception;
}
