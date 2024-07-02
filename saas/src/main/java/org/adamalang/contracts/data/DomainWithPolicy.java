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
package org.adamalang.contracts.data;

import org.adamalang.runtime.sys.domains.Domain;

/** a domain with an associated space policy */
public class DomainWithPolicy {
  public final Domain domain;
  public final SpacePolicy policy;

  public DomainWithPolicy(Domain domain, SpacePolicy policy) {
    this.domain = domain;
    this.policy = policy;
  }
}
