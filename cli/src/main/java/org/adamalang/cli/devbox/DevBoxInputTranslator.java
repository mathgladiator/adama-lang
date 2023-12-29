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
package org.adamalang.cli.devbox;

import org.adamalang.cli.router.Arguments;
import org.adamalang.devbox.*;

/** this file exists until the migration from frontend dev-server to devbox is finished */
public class DevBoxInputTranslator {

  public static Inputs from(Arguments.FrontendDevServerArgs args) {
    Inputs inputs = new Inputs();
    inputs.developerIdentity = args.config.get_string("identity", null);
    inputs.rxhtmlPath = args.rxhtmlPath;
    inputs.assetPath = args.assetPath;
    inputs.microverse = args.microverse;
    inputs.debugger = args.debugger;
    inputs.localLibadamaPath = args.localLibadamaPath;
    inputs.environment = args.environment;
    inputs.preserveView = args.preserveView;
    inputs.types = args.types;
    inputs.localPathForLibAdamaOverride = args.config.get_nullable_string("local-libadama-path-default");
    inputs.webConfig = args.config.get_or_create_child("web");
    return inputs;
  }

  public static Inputs from(Arguments.DevboxArgs args) {
    Inputs inputs = new Inputs();
    inputs.developerIdentity = args.config.get_string("identity", null);
    inputs.rxhtmlPath = args.rxhtmlPath;
    inputs.assetPath = args.assetPath;
    inputs.microverse = args.microverse;
    inputs.debugger = args.debugger;
    inputs.localLibadamaPath = args.localLibadamaPath;
    inputs.environment = args.environment;
    inputs.preserveView = args.preserveView;
    inputs.types = args.types;
    inputs.localPathForLibAdamaOverride = args.config.get_nullable_string("local-libadama-path-default");
    inputs.webConfig = args.config.get_or_create_child("web");
    return inputs;
  }
}
