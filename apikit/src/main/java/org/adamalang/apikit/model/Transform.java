/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

import java.util.Locale;

public class Transform {
  public final String inputName;
  public final String fieldInputName;
  public final Type inputType;
  public final String service;
  public final String shortServiceName;
  public final String outputName;
  public final String outputJavaType;
  public final String shortOutputJavaType;
  public final int errorCode;

  public Transform(String inputName, Type inputType, String service, String outputName, String outputJavaType, int errorCode) {
    this.inputName = inputName;
    String camelInputName = Common.camelize(inputName);
    this.fieldInputName = camelInputName.substring(0, 1).toLowerCase(Locale.ROOT) + camelInputName.substring(1) + "Service";
    this.inputType = inputType;
    this.service = service;
    int lastDotService = service.lastIndexOf('.');
    shortServiceName = service.substring(lastDotService + 1);
    this.outputName = outputName;
    this.outputJavaType = outputJavaType;
    int lastDotOutputJavaType = outputJavaType.lastIndexOf('.');
    this.shortOutputJavaType = outputJavaType.substring(lastDotOutputJavaType + 1);
    this.errorCode = errorCode;
  }
}
