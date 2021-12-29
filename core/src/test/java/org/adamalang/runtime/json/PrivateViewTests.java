/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class PrivateViewTests {
    @Test
    public void killing() {
        ArrayList<String> list = new ArrayList<>();
        PrivateView pv = new PrivateView(NtClient.NO_ONE, new Perspective() {
            @Override
            public void data(String data) {
                list.add(data);
            }

            @Override
            public void disconnect() {

            }
        }) {

            @Override
            public void dumpViewer(JsonStreamWriter writer) {
            }

            @Override
            public void update(JsonStreamWriter writer) {

            }

            @Override
            public void ingest(JsonStreamReader reader) {

            }
        };
        Assert.assertTrue(pv.isAlive());
        pv.kill();
        Assert.assertFalse(pv.isAlive());
        pv.deliver("{}");
        Assert.assertEquals("{}", list.get(0));
    }

    @Test
    public void usurp() {
        ArrayList<String> list = new ArrayList<>();
        PrivateView pv1 = new PrivateView(NtClient.NO_ONE, new Perspective() {
            @Override
            public void data(String data) {
                list.add(data);
            }

            @Override
            public void disconnect() {

            }
        }) {

            @Override
            public void dumpViewer(JsonStreamWriter writer) {
            }

            @Override
            public void update(JsonStreamWriter writer) {

            }

            @Override
            public void ingest(JsonStreamReader reader) {

            }
        };

        PrivateView pv2 = new PrivateView(NtClient.NO_ONE, pv1.perspective) {

            @Override
            public void dumpViewer(JsonStreamWriter writer) {
            }

            @Override
            public void update(JsonStreamWriter writer) {

            }

            @Override
            public void ingest(JsonStreamReader reader) {

            }
        };

        pv2.usurp(pv1);
        Assert.assertTrue(pv1.isAlive());
        pv2.kill();
        Assert.assertFalse(pv1.isAlive());
    }
}
