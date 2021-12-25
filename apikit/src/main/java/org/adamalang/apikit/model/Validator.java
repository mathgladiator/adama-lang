package org.adamalang.apikit.model;


public class Validator {
    public final String service;
    public final String shortServiceName;

    public Validator(String service) {
        this.service = service;
        int lastDotService = service.lastIndexOf('.');
        shortServiceName = service.substring(lastDotService + 1);
    }
}
