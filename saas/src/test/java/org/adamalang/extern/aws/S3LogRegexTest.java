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
package org.adamalang.extern.aws;

import org.junit.Assert;
import org.junit.Test;

public class S3LogRegexTest {
  @Test
  public void validate() {
    Assert.assertTrue(S3.shouldConsiderForUpload("error.2022-07-24.0.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("error!2022-07-24!0!log"));
    Assert.assertTrue(S3.shouldConsiderForUpload("error.2022-07-25.10.log"));
    Assert.assertTrue(S3.shouldConsiderForUpload("access.2022-407-224.120.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("error.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("access.log"));
  }
}
