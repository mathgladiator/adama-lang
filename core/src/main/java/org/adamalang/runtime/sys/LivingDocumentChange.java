/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.sys;

import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.json.PrivateView;

import java.util.List;

/** A living document is changing; this represents the side-effects of that change for both the
 * data store (i.e. durability) and the clients (i. the people). This change is bundled such
 * that these two concerns can be ordered such that clients never see artifacts of uncommitted
 * changes.
 */
public class LivingDocumentChange {

    /** a closure to combine who to send too  */
    public static class Broadcast {
        private final PrivateView view;
        private final String data;
        public Broadcast(PrivateView view, String data) {
            this.view = view;
            this.data = data;
        }

        public void complete() {
            view.deliver(data);
        }
    }

    /** the data change */
    public final DataService.RemoteDocumentUpdate update;
    private final List<Broadcast> broadcasts;

    /** wrap both the update and broadcasts into a nice package */
    public LivingDocumentChange(DataService.RemoteDocumentUpdate update, List<Broadcast> broadcasts) {
        this.update = update;
        this.broadcasts = broadcasts;
    }

    /** complete the update. This is to be called once the change is made durable */
    public void complete() {
        if (broadcasts != null) {
            for (Broadcast bc : broadcasts) {
                bc.complete();
            }
        }
    }
}
