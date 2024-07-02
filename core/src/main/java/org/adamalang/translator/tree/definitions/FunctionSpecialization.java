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
package org.adamalang.translator.tree.definitions;

/**
 * denote a specialization of a function. A pure function is one that requires no state from the
 * document. In the truest sense, a pure function is an actual mathematical function. These are
 * important because these can be put into a variety of unique contexts and easily translate to
 * things like JavaScript!
 *
 * <p>An impure function is really a procedure, and it is given free reign to do whatever it wants.
 */
public enum FunctionSpecialization {
  Impure, //
  Pure
}
