package org.adamalang.web.io;

import org.adamalang.runtime.contracts.Callback;

/** An AsyncTransform transforms an input into an output over time */

@FunctionalInterface
public interface AsyncTransform<In, Out> {

    /** go forth and execute the transformation */
    public void execute(In parameter, Callback<Out> callback);
}
