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
  public final boolean instrumentPerf;

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
    instrumentPerf = builder.instrumentPerf;
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
    public boolean instrumentPerf;

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
      instrumentPerf = false;
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

    public Builder instrument() {
      instrumentPerf = true;
      return this;
    }
  }
}
