/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions.web;

import org.adamalang.common.web.UriMatcher;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
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
    Parser parser = new Parser(engine);
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
