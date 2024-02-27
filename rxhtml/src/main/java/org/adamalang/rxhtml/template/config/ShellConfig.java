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

import org.adamalang.common.Platform;
import org.adamalang.rxhtml.template.Base;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/** configuration for the shell */
public class ShellConfig {
  public final Feedback feedback;
  public final boolean useLocalAdamaJavascript;
  public final String version;
  public final String environment;

  private ShellConfig(Feedback feedback, final String version, String environment, boolean useLocalAdamaJavascript) {
    this.feedback = feedback;
    this.version = version;
    this.useLocalAdamaJavascript = useLocalAdamaJavascript;
    this.environment = environment;
  }

  public static Builder start() {
    return new Builder();
  }

  public static class Builder {
    public Feedback feedback;
    public boolean useLocalAdamaJavascript;
    public String version;
    public String environment;

    public Builder() {
      this.feedback = Feedback.NoOp;
      this.useLocalAdamaJavascript = false;
      this.version =  Platform.JS_VERSION;
      this.environment = "prod";
    }

    public Builder withFeedback(Feedback feedback) {
      this.feedback = feedback;
      return this;
    }

    public Builder withVersion(String version) {
      this.version = version;
      return this;
    }

    public Builder withEnvironment(String environment) {
      this.environment = environment;
      return this;
    }

    public Builder withUseLocalAdamaJavascript(boolean useLocalAdamaJavascript) {
      this.useLocalAdamaJavascript = useLocalAdamaJavascript;
      return this;
    }

    public ShellConfig end() {
      return new ShellConfig(feedback, version, environment, useLocalAdamaJavascript);
    }
  }

  public boolean includeInShell(Element element) {
    return Base.checkEnv(element, environment);
  }
}
