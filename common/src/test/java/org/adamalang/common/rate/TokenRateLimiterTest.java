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
package org.adamalang.common.rate;

import org.adamalang.common.Json;
import org.adamalang.common.gossip.MockTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class TokenRateLimiterTest {
  @Test
  public void exhausts_and_refills() {
    MockTime time = new MockTime();
    TokenRateLimiter defaults = new TokenRateLimiter(Json.newJsonObject(), time);
    TokenGrant grant;
    for (int k = 0; k < 6; k++) {
      time.currentTime += 1000;
      grant = defaults.ask();
      Assert.assertEquals(5, grant.tokens);
      Assert.assertEquals(250, grant.millseconds);
    }
    grant = defaults.ask();
    Assert.assertEquals(3, grant.tokens);
    Assert.assertEquals(4000, grant.millseconds);
    time.currentTime += 1000;
    grant = defaults.ask();
    Assert.assertEquals(0, grant.tokens);
    Assert.assertEquals(2000, grant.millseconds);

    time.currentTime += 160000;

    for (int k = 0; k < 6; k++) {
      time.currentTime += 1000;
      grant = defaults.ask();
      Assert.assertEquals(5, grant.tokens);
      Assert.assertEquals(250, grant.millseconds);
    }
    grant = defaults.ask();
    Assert.assertEquals(2, grant.tokens);
    Assert.assertEquals(6000, grant.millseconds);
    time.currentTime += 1000;
    grant = defaults.ask();
    Assert.assertEquals(1, grant.tokens);
    Assert.assertEquals(8000, grant.millseconds);
    grant = defaults.ask();
    Assert.assertEquals(0, grant.tokens);
    Assert.assertEquals(2000, grant.millseconds);
  }

  public static class Sample {
    public final long time;
    public final int tokens;

    public Sample(long time, int tokens) {
      this.time = time;
      this.tokens = tokens;
    }
  }

  @Test
  public void validate_rate_greedy() {
    MockTime time = new MockTime();
    TokenRateLimiter defaults = new TokenRateLimiter(Json.newJsonObject(), time);
    ArrayList<Sample> samples = new ArrayList<>();
    long tokens = 0;
    while (time.currentTime < 100 * 60000) {
      TokenGrant grant = defaults.ask();
      tokens += grant.tokens;
      if (grant.tokens > 0) {
        samples.add(new Sample(time.currentTime, grant.tokens));
      }
      time.currentTime += 250;
    }
    Assert.assertEquals(3029, tokens);
    Random rng = new Random();
    for (int init = 0; init < samples.size(); init++) {
      long start = samples.get(init).time;
      int j = init;
      int k = init;
      while (j < samples.size() && (samples.get(j).time - start) < 60000) {
        j++;
      }
      int window = 0;
      while (k < j) {
        window += samples.get(k).tokens;
        k++;
      }
      // the burst rate is actually 2X the max tokens, but the steady state rate is max tokens
      Assert.assertTrue(window <= 60);
    }
  }
}
