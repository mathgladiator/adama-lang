/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

public interface FrontendHandler {
  void bundle(FrontendBundleArgs args, YesOrError output) throws Exception;
  void decryptProductConfig(FrontendDecryptProductConfigArgs args, YesOrError output) throws Exception;
  void devServer(FrontendDevServerArgs args, YesOrError output) throws Exception;
  void enableEncryption(FrontendEnableEncryptionArgs args, YesOrError output) throws Exception;
  void encryptProductConfig(FrontendEncryptProductConfigArgs args, YesOrError output) throws Exception;
  void make200(FrontendMake200Args args, YesOrError output) throws Exception;
  void measure(FrontendMeasureArgs args, YesOrError output) throws Exception;
  void mobileCapacitor(FrontendMobileCapacitorArgs args, YesOrError output) throws Exception;
  void pushGenerate(FrontendPushGenerateArgs args, YesOrError output) throws Exception;
  void rxhtml(FrontendRxhtmlArgs args, YesOrError output) throws Exception;
  void setLibadama(FrontendSetLibadamaArgs args, YesOrError output) throws Exception;
  void tailwindKick(FrontendTailwindKickArgs args, YesOrError output) throws Exception;
  void validate(FrontendValidateArgs args, YesOrError output) throws Exception;
  void wrapCss(FrontendWrapCssArgs args, YesOrError output) throws Exception;
}
