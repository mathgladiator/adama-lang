/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;

import java.util.ArrayList;

public class MockJsonResponder implements JsonResponder {
    public final ArrayList<String> events;

    public MockJsonResponder() {
        this.events = new ArrayList<>();
    }

    @Override
    public void stream(String json) {
        events.add("STREAM:" + json);
    }

    @Override
    public void finish(String json) {
        events.add("FINISH:" + json);
    }

    @Override
    public void error(ErrorCodeException ex) {
        events.add("ERROR:" + ex.code);
    }
}
