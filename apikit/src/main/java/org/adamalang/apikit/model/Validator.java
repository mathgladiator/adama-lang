package org.adamalang.apikit.model;

import java.util.Locale;

public class Validator {
    public final String service;
    public final String shortServiceName;
    public final int errorCode;

    public Validator(String service, int errorCode) {
        this.service = service;
        int lastDotService = service.lastIndexOf('.');
        shortServiceName = service.substring(lastDotService + 1);
        this.errorCode = errorCode;
    }
}
