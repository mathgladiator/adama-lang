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
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.common.web.UriMatcher;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.SymbolIndex;
import org.junit.Assert;
import org.junit.Test;

public class UriTests {
  @Test
  public void double_coverage() {
    Assert.assertTrue(Uri.isDouble("1.2"));
    Assert.assertFalse(Uri.isDouble("x"));
  }
  @Test
  public void long_coverage() {
    Assert.assertTrue(Uri.isLong("1234123431245"));
    Assert.assertFalse(Uri.isLong("x"));
  }
  @Test
  public void int_coverage() {
    Assert.assertTrue(Uri.isInteger("123"));
    Assert.assertFalse(Uri.isInteger("x"));
  }
  private UriMatcher of(String path) throws Exception {
    TokenEngine engine = new TokenEngine("test", path.codePoints().iterator());
    Parser parser = new Parser(engine, new SymbolIndex(), Scope.makeRootDocument());
    return parser.uri().matcher();
  }
  @Test
  public void matching_simple_fixed_1() throws Exception {
    Assert.assertFalse(of("/nope/yep").matches("/yep"));
    Assert.assertFalse(of("/nope/yep").matches("/nope/xyz"));
    Assert.assertFalse(of("/yep/xyz").matches("/yep"));
    Assert.assertTrue(of("/yep/xyz").matches("/yep/xyz"));
  }
  @Test
  public void matching_simple_fixed() throws Exception {
    Assert.assertFalse(of("/nope").matches("/yep"));
    Assert.assertTrue(of("/yep").matches("/yep"));
  }
  @Test
  public void matching_simple_root() throws Exception {
    Assert.assertFalse(of("/nope").matches("/"));
    Assert.assertTrue(of("/").matches("/"));
  }
  @Test
  public void matching_str() throws Exception {
    Assert.assertTrue(of("/$id:string").matches("/name"));
    Assert.assertFalse(of("/$id:string").matches("/name/blah"));
    Assert.assertTrue(of("/$id*").matches("/name/blah/blah/blah"));
  }
  @Test
  public void matching_int() throws Exception {
    Assert.assertTrue(of("/$id:int").matches("/123"));
    Assert.assertTrue(of("/$id:int/name").matches("/123/name"));
    Assert.assertFalse(of("/$id:int").matches("/xyz"));
  }
  @Test
  public void matching_long() throws Exception {
    Assert.assertTrue(of("/$id:long").matches("/123"));
    Assert.assertTrue(of("/$id:long/name").matches("/123/name"));
    Assert.assertFalse(of("/$id:long").matches("/xyz"));
  }
  @Test
  public void matching_double() throws Exception {
    Assert.assertTrue(of("/$id:double").matches("/123"));
    Assert.assertTrue(of("/$id:double/name").matches("/123/name"));
    Assert.assertFalse(of("/$id:double").matches("/xyz"));
  }
  @Test
  public void matching_bool() throws Exception {
    Assert.assertTrue(of("/$id:bool").matches("/true"));
    Assert.assertTrue(of("/$id:bool/name").matches("/false/name"));
    Assert.assertFalse(of("/$id:bool").matches("/xyz"));
  }
  @Test
  public void matching_long_path() throws Exception {
    Assert.assertTrue(of("/start/$id:string/fixed/$val:int").matches("/start/xyz/fixed/123"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz/fixed/123"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz/nope"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz/fixed"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz/fixed/xyz"));
    Assert.assertFalse(of("/start/$id:string/fixed/$val:int").matches("/stop/xyz/fixed/123/more"));
  }
}
