package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.sys.CoreStream;

/** This is like a callback, but for an infinite stream. */
public interface Streamback {

    /** the stream has been setup and can be interacted with via the core stream */
    void onSetupComplete(CoreStream stream);

    /** the stream has a status representing what is happening at the given moment */
    public static enum StreamStatus {
        Connected,
        Disconnected;
    }

    /** inform the client of a status update */
    void status(StreamStatus status);

    /** inform the client of new data */
    void next(String data);

    /** inform the client that a failure has occurred */
    void failure(ErrorCodeException exception);
}
