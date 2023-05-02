/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import java.util.ArrayList;

/** compiler options to control anything */
public class CompilerOptions {
  public final String className;
  public final boolean disableBillingCost; // G2G
  public final int goodwillBudget; // G2G
  public final String[] inputFiles;
  public final String outputFile;
  public final boolean produceCodeCoverage; // G2G
  public final boolean removeTests;
  public final String[] searchPaths;
  public final boolean stderrLoggingCompiler; // G2G

  private CompilerOptions(final Builder builder) {
    stderrLoggingCompiler = builder.stderrLoggingCompiler;
    produceCodeCoverage = builder.produceCodeCoverage;
    disableBillingCost = builder.disableBillingCost;
    removeTests = builder.removeTests;
    goodwillBudget = builder.goodwillBudget;
    className = builder.className;
    outputFile = builder.outputFile;
    searchPaths = builder.searchPaths.toArray(new String[builder.searchPaths.size()]);
    inputFiles = builder.inputFiles.toArray(new String[builder.inputFiles.size()]);
  }

  public static Builder start() {
    return new Builder();
  }

  public static class Builder {
    public String className;
    public boolean disableBillingCost;
    public int goodwillBudget;
    public ArrayList<String> inputFiles;
    public String outputFile;
    public String packageName;
    public boolean produceCodeCoverage;
    public boolean removeTests;
    public ArrayList<String> searchPaths;
    public boolean stderrLoggingCompiler;

    private Builder() {
      stderrLoggingCompiler = true;
      produceCodeCoverage = false;
      disableBillingCost = false;
      removeTests = false;
      goodwillBudget = 100000;
      packageName = null;
      className = "AGame";
      outputFile = null;
      searchPaths = new ArrayList<>();
      inputFiles = new ArrayList<>();
    }

    public Builder args(final int offset, final String... args) {
      for (var k = offset; k + 1 < args.length; k += 2) {
        final var key = args[k];
        final var value = args[k + 1].toLowerCase().trim();
        switch (key) {
          case "--billing":
            disableBillingCost = "no".equals(value) || "false".equals(value);
            break;
          case "--code-coverage":
            produceCodeCoverage = "yes".equals(value) || "true".equals(value);
            break;
          case "--remove-tests":
            removeTests = "yes".equals(value) || "true".equals(value);
            break;
          case "--silent":
            stderrLoggingCompiler = "no".equals(value) || "false".equals(value);
            break;
          case "--goodwill-budget":
            goodwillBudget = Integer.parseInt(value);
            break;
          case "--input":
            inputFiles.add(args[k + 1].trim());
            break;
          case "--add-search-path":
            searchPaths.add(args[k + 1].trim());
            break;
          case "--output":
            outputFile = args[k + 1].trim();
            break;
          case "--package":
            packageName = args[k + 1].trim();
            break;
          case "--class":
            className = args[k + 1].trim();
            break;
        }
      }
      return this;
    }

    public Builder enableCodeCoverage() {
      produceCodeCoverage = true;
      return this;
    }

    public CompilerOptions make() {
      return new CompilerOptions(this);
    }

    public Builder noCost() {
      disableBillingCost = true;
      return this;
    }
  }
}
