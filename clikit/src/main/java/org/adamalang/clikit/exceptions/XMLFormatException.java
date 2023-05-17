package org.adamalang.clikit.exceptions;

import java.util.ArrayList;

public class XMLFormatException extends Exception {

    public ArrayList<String> exceptionStack = new ArrayList<>();
    public boolean isActive = false;
    public StackTraceElement[] firstStack;
    public XMLFormatException() {
        super();
    }
    public XMLFormatException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String returnString = String.join("\n", exceptionStack);
        return returnString;
    }


    @Override
    public StackTraceElement[] getStackTrace() {
        return firstStack;
    }
    public void addToExceptionStack(String message) {
        exceptionStack.add(message);

        if (firstStack == null) {
            super.fillInStackTrace();
            firstStack = super.getStackTrace();
        }

        isActive = true;
    }
}