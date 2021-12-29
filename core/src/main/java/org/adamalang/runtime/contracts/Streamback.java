/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;
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
