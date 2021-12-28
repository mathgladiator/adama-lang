package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;

/** Represents a stream for the consumer to interact with the document. This simplifies the interaction model such that
 * consumers don't need to think about how threading happens.
 */
public class CoreStream {
    private final NtClient who;
    private final DurableLivingDocument document;
    private final PrivateView view;

    public CoreStream(NtClient who, DurableLivingDocument document, PrivateView view) {
        this.who = who;
        this.document = document;
        this.view = view;
    }

    /** send a message to the document */
    public void send(String channel, String marker, String message, Callback<Integer> callback) {
        document.base.executor.execute(() -> {
            document.send(who, marker, channel, message, callback);
        });
    }

    public void canAttach(Callback<Boolean> callback) {
        document.base.executor.execute(() -> {
            callback.success(document.canAttach(who));
        });
    }

    public void attach(NtAsset asset, Callback<Integer> callback) {
        document.base.executor.execute(() -> {
            document.attach(who, asset, callback);
        });
    }

    /** disconnect this stream from the document */
    public void disconnect() {
        document.base.executor.execute(() -> {
            // disconnect this view
            view.kill();
            // clean up and keep things tidy
            if (document.garbageCollectPrivateViewsFor(who) == 0) {
                // falling edge disconnects the person
                document.disconnect(who, Callback.DONT_CARE_INTEGER);
            } else {
                document.invalidate(Callback.DONT_CARE_INTEGER);
            }
            // tell the client
            view.perspective.disconnect();
        });
    }
}
