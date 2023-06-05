package org.adamalang.cli.runtime;

import org.adamalang.cli.Util;

/** Represents the different types of outputs, STDOUT changes based on class **/
public class Output {
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
