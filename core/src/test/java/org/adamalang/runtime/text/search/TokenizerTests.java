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
package org.adamalang.runtime.text.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class TokenizerTests {
  @Test
  public void simple() {
    TreeSet<String> words = Tokenizer.of("the quick brown fox did a little dance in the woods");
    Assert.assertTrue(words.contains("the"));
    Assert.assertTrue(words.contains("quick"));
    Assert.assertTrue(words.contains("brown"));
    Assert.assertTrue(words.contains("fox"));
    Assert.assertTrue(words.contains("did"));
    Assert.assertTrue(words.contains("a"));
    Assert.assertTrue(words.contains("little"));
    Assert.assertTrue(words.contains("dance"));
    Assert.assertTrue(words.contains("woods"));
  }
}
