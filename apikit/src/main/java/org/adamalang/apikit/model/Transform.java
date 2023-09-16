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
