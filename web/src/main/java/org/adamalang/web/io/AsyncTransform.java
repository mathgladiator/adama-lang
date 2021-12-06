package org.adamalang.web.io;

import org.adamalang.runtime.contracts.Callback;

public interface AsyncTransform<In, Out> {
    public void execute(In parameter, Callback<Out> callback);
}
