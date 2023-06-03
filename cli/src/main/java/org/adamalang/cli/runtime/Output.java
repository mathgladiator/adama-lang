package org.adamalang.cli.runtime;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorTable;
import org.adamalang.cli.Util;
import org.adamalang.common.ErrorCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Output {
    /** Represents the different types of outputs, STDOUT changes based on class **/
    private final boolean color;
    private final boolean json;

    public Output(String[] args) {
        color = false;
        json = true;
    }

    public class YesOrError {
        public void out() {
            System.out.println(Util.prefix("\u2705", Util.ANSI.Green));
        }
    }
    public YesOrError makeYesOrError() {
        return new YesOrError();
    }
}
