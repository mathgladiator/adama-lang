package org.adamalang.clikit.exceptions;

import java.util.ArrayList;

/** Exception class used to identify errors with XML Formatting **/
public class XMLFormatException extends Exception {

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