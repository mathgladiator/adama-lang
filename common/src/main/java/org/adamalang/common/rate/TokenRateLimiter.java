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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.TimeSource;

/** a simple rate limiter using a token bucket that fills up */
public class TokenRateLimiter {
  private final int maxTokensInWindow;
  private final int windowMilliseconds;
  private final int maxGrant;
  private final TimeSource time;
  private double tokens;
  private long at;
  private final double refreshGuard;
  private final int minimumWait;

  public TokenRateLimiter(ObjectNode config, TimeSource time) {
    this.maxTokensInWindow = Json.readInteger(config, "max-tokens", 30);
    this.windowMilliseconds = Json.readInteger(config, "window-ms", 60000);
    this.maxGrant = Json.readInteger(config, "max-grant", 5);
    this.minimumWait = Json.readInteger(config, "minimum-wait", 250);
    this.time = time;
    this.tokens = maxTokensInWindow;
    this.at = time.nowMilliseconds();
    this.refreshGuard = windowMilliseconds / maxTokensInWindow;
  }

  public synchronized TokenGrant ask() {
    long now = time.nowMilliseconds();
    long delta = now - at;
    if (delta >= refreshGuard) { // threshold for numerical significance
      tokens += delta * maxTokensInWindow / windowMilliseconds;
      tokens = Math.min(maxTokensInWindow, Math.ceil(tokens));
      at = now;
    }
    if (tokens >= 1) {
      int tokensToTake = (int) Math.min(maxGrant, tokens);
      tokens -= tokensToTake;
      return new TokenGrant(tokensToTake, Math.max(minimumWait, (int) ((maxGrant - tokensToTake) * refreshGuard)));
    } else {
      return new TokenGrant(0, (int) Math.max(refreshGuard, minimumWait));
    }
  }
}
