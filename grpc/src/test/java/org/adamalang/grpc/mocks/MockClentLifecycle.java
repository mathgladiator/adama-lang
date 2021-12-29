/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.Lifecycle;

public class MockClentLifecycle implements Lifecycle {
    public StringBuilder events;

    public MockClentLifecycle() {
        this.events = new StringBuilder();
    }
    @Override
    public synchronized void connected(InstanceClient client) {
        events.append("C");
    }

    @Override
    public synchronized void disconnected(InstanceClient client) {
        events.append("D");
    }

    @Override
    public synchronized String toString() {
        return events.toString();
    }
}
