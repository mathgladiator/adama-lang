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
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;
import java.util.HashMap;

/** This represents where scripts live such that deployments can pull versions based on the key */
public interface LivingDocumentFactoryFactory {
  /** fetch the factory for the given key */
  void fetch(Key key, Callback<LivingDocumentFactory> callback);

  /** account for the memory of the factory */
  void account(HashMap<String, PredictiveInventory.MeteringSample> sample);

  /** fetch the available spaces */
  Collection<String> spacesAvailable();
}
