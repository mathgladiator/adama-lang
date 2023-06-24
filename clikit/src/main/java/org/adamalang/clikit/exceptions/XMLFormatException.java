/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
