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
