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
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;

public class RuleSetConversion {
  public static void SignalConversionIssue(final Environment environment, final TyType given, final boolean silent) {
    if (!silent && given != null) {
      environment.document.createError(given, String.format("Type check failure: the type `%s` is unable to be converted. Only list<S>, S[], maybe<S> can be converted where S is either a record or a message. Also, if the type is integer, then S must be an enum.", given.getAdamaType()));
    }
  }
}
