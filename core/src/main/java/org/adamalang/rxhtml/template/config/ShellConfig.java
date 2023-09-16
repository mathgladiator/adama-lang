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
package org.adamalang.rxhtml.template.config;

/** configuration for the shell */
public class ShellConfig {
  public final Feedback feedback;
  public final boolean useLocalAdamaJavascript;

  private ShellConfig(Feedback feedback, boolean useLocalAdamaJavascript) {
    this.feedback = feedback;
    this.useLocalAdamaJavascript = useLocalAdamaJavascript;
  }

  public static Builder start() {
    return new Builder();
  }

  public static class Builder {
    public Feedback feedback;
    public boolean useLocalAdamaJavascript;

    public Builder() {
      this.feedback = Feedback.NoOp;
      this.useLocalAdamaJavascript = false;
    }

    public Builder withFeedback(Feedback feedback) {
      this.feedback = feedback;
      return this;
    }

    public Builder withUseLocalAdamaJavascript(boolean useLocalAdamaJavascript) {
      this.useLocalAdamaJavascript = useLocalAdamaJavascript;
      return this;
    }

    public ShellConfig end() {
      return new ShellConfig(feedback, useLocalAdamaJavascript);
    }
  }
}
