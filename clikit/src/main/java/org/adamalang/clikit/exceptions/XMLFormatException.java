package org.adamalang.clikit.exceptions;

import java.util.ArrayList;

public class XMLFormatException extends Exception {
    /** Exception class used to identify Errors with XML Formatting **/
    public ArrayList<String> exceptionStack = new ArrayList<>();
    public boolean isActive = false;
    public XMLFormatException() {
        super();
    }

    @Override
    public String getMessage() {
        String returnString = String.join("\n", exceptionStack);
        return returnString;
    }

    public void addToExceptionStack(String message) {
        exceptionStack.add(message);
        isActive = true;
    }
}