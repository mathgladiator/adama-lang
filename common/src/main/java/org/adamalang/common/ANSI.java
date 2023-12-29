package org.adamalang.common;

/** ANSI Codes are a sign that you love working with the machine... see https://en.wikipedia.org/wiki/ANSI_escape_code  */
public enum ANSI {
  Black("\u001b[30m"), //
  Red("\u001b[31m"), //
  Green("\u001b[32m"), //
  Yellow("\u001b[33m"), //
  Blue("\u001b[34m"), //
  Magenta("\u001b[35m"), //
  Cyan("\u001b[36m"), //
  White("\u001b[37m"), //

  BoldBlack("\u001b[1;30m"), //
  BoldRed("\u001b[1;31m"), //
  BoldGreen("\u001b[1;32m"), //
  BoldYellow("\u001b[1;33m"), //
  BoldBlue("\u001b[1;34m"), //
  BoldMagenta("\u001b[1;35m"), //
  BoldCyan("\u001b[1;36m"), //
  BoldWhite("\u001b[1;37m"), //

  UnderlineBlack("\u001b[4;30m"), //
  UnderlineRed("\u001b[4;31m"), //
  UnderlineGreen("\u001b[4;32m"), //
  UnderlineYellow("\u001b[4;33m"), //
  UnderlineBlue("\u001b[4;34m"), //
  UnderlineMagenta("\u001b[4;35m"), //
  UnderlineCyan("\u001b[4;36m"), //
  UnderlineWhite("\u001b[4;37m"), //

  BackgroundBlack("\u001b[40m"), //
  BackgroundRed("\u001b[41m"), //
  BackgroundGreen("\u001b[42m"), //
  BackgroundYellow("\u001b[43m"), //
  BackgroundBlue("\u001b[44m"), //
  BackgroundMagenta("\u001b[45m"), //
  BackgroundCyan("\u001b[46m"), //
  BackgroundWhite("\u001b[47m"), //

  HighIntensityBlack("\u001b[90m"), //
  HighIntensityRed("\u001b[91m"), //
  HighIntensityGreen("\u001b[92m"), //
  HighIntensityYellow("\u001b[93m"), //
  HighIntensityBlue("\u001b[94m"), //
  HighIntensityMagenta("\u001b[95m"), //
  HighIntensityCyan("\u001b[96m"), //
  HighIntensityWhite("\u001b[97m"), //

  BoldHighIntensityBlack("\u001b[1;90m"), //
  BoldHighIntensityRed("\u001b[1;91m"), //
  BoldHighIntensityGreen("\u001b[1;92m"), //
  BoldHighIntensityYellow("\u001b[1;93m"), //
  BoldHighIntensityBlue("\u001b[1;94m"), //
  BoldHighIntensityMagenta("\u001b[1;95m"), //
  BoldHighIntensityCyan("\u001b[1;96m"), //
  BoldHighIntensityWhite("\u001b[1;97m"), //

  HighIntensityBackgroundBlack("\u001b[100m"), //
  HighIntensityBackgroundRed("\u001b[101m"), //
  HighIntensityBackgroundGreen("\u001b[102m"), //
  HighIntensityBackgroundYellow("\u001b[103m"), //
  HighIntensityBackgroundBlue("\u001b[104m"), //
  HighIntensityBackgroundMagenta("\u001b[105m"), //
  HighIntensityBackgroundCyan("\u001b[106m"), //
  HighIntensityBackgroundWhite("\u001b[107m"), //

  Bold("\u001b[1m"), //
  Reset("\u001b[0m"), //
  Normal("\u001b[39m");


  public final String ansi;

  ANSI(String ansi) {
    this.ansi = ansi;
  }
}
