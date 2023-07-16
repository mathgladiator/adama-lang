/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

  public static Builder start() {
    return new Builder();
  }
}
