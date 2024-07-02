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

public interface SpaceHandler {
  void create(SpaceCreateArgs args, YesOrError output) throws Exception;
  void delete(SpaceDeleteArgs args, YesOrError output) throws Exception;
  void deploy(SpaceDeployArgs args, YesOrError output) throws Exception;
  void developers(SpaceDevelopersArgs args, JsonOrError output) throws Exception;
  void encryptPriv(SpaceEncryptPrivArgs args, YesOrError output) throws Exception;
  void encryptSecret(SpaceEncryptSecretArgs args, YesOrError output) throws Exception;
  void generateKey(SpaceGenerateKeyArgs args, YesOrError output) throws Exception;
  void generatePolicy(SpaceGeneratePolicyArgs args, YesOrError output) throws Exception;
  void get(SpaceGetArgs args, YesOrError output) throws Exception;
  void getPolicy(SpaceGetPolicyArgs args, YesOrError output) throws Exception;
  void getRxhtml(SpaceGetRxhtmlArgs args, YesOrError output) throws Exception;
  void list(SpaceListArgs args, JsonOrError output) throws Exception;
  void metrics(SpaceMetricsArgs args, JsonOrError output) throws Exception;
  void reflect(SpaceReflectArgs args, YesOrError output) throws Exception;
  void setPolicy(SpaceSetPolicyArgs args, YesOrError output) throws Exception;
  void setRole(SpaceSetRoleArgs args, YesOrError output) throws Exception;
  void setRxhtml(SpaceSetRxhtmlArgs args, YesOrError output) throws Exception;
  void upload(SpaceUploadArgs args, JsonOrError output) throws Exception;
}
