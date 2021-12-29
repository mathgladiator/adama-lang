/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.ActiveKeyStream;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockInstantDataService implements DataService {
    private final HashMap<Key, ArrayList<RemoteDocumentUpdate>> logByKey;
    private final ArrayList<String> log;
    private final ArrayList<CountDownLatch> latches;
    private final ArrayList<Key> bootstrap;
    private boolean enableSeqSkip = false;
    private int seqToSkip = 0;
    private boolean infiniteSkip = false;
    private boolean crashSeqSkip = false;

    public MockInstantDataService() {
        this.logByKey = new HashMap<>();
        log = new ArrayList<>();
        this.latches = new ArrayList<>();
        this.bootstrap = new ArrayList<>();
    }

    private synchronized void println(String x) {
        System.out.println(x);
        log.add(x);
        Iterator<CountDownLatch> it = latches.iterator();
        while (it.hasNext()) {
            CountDownLatch latch = it.next();
            latch.countDown();
            if (latch.getCount() == 0) {
                it.remove();
            }
        }
    }

    public void ready(Key key) {
        bootstrap.add(key);
    }

    @Override
    public void scan(ActiveKeyStream streamback) {
        for (Key key : bootstrap) {
            streamback.schedule(key, 0);
        }
        streamback.finish();
    }

    public synchronized void assertLogAt(int k, String expected) {
        Assert.assertEquals(expected, log.get(k));
    }

    public synchronized String getLogAt(int k) {
        return log.get(k);
    }

    public synchronized Runnable latchLogAt(int count) {
        CountDownLatch latch = new CountDownLatch(count);
        latches.add(latch);
        return () -> {
            try {
                Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
            } catch (InterruptedException ie) {
                Assert.fail();
            }
        };
    }

    @Override
    public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        if (logByKey.containsKey(key)) {
            callback.failure(new ErrorCodeException(1));
        } else {
            println("INIT:" + key.space + "/" + key.key + ":" + patch.seq  +"->" + patch.redo);
            ArrayList<RemoteDocumentUpdate> log = new ArrayList<>();
            log.add(patch);
            logByKey.put(key, log);
            callback.success(null);
        }
    }

    @Override
    public void get(Key key, Callback<LocalDocumentChange> callback) {
        ArrayList<RemoteDocumentUpdate> log = logByKey.get(key);
        if (log == null) {
            callback.failure(new ErrorCodeException(0));
            return;
        }
        println("LOAD:" + key.space + "/" + key.key);
        int seq = 0;
        Object obj = null;
        for (RemoteDocumentUpdate update : log) {
            seq = update.seq;
            if (obj == null) {
                obj = new JsonStreamReader(update.redo).readJavaTree();
            } else {
                obj = JsonAlgebra.merge(obj, new JsonStreamReader(update.redo).readJavaTree());
            }
        }
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.writeTree(obj);
        callback.success(new LocalDocumentChange(writer.toString()));
    }

    public void skipAt(int seq) {
        this.enableSeqSkip = true;
        this.seqToSkip = seq;
    }

    public void infiniteSkip() {
        this.infiniteSkip = true;
    }

    public void killSkip() {
        crashSeqSkip = true;
    }

    @Override
    public synchronized void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        println("PATCH:" + key.space + "/" + key.key + ":" + patch.seq  +"->" + patch.redo);
        ArrayList<RemoteDocumentUpdate> log = logByKey.get(key);
        if (infiniteSkip) {
            callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
            println("SKIP++");
            return;
        }
        if (enableSeqSkip) {
            if (patch.seq == seqToSkip) {
                callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF));
                println("SKIP");
                return;
            }
        }
        if (key != null) {
            log.add(patch);
            callback.success(null);
        } else {
            callback.failure(new ErrorCodeException(3));
        }
    }

    private int skipTrack = 0;
    @Override
    public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
        if (method == ComputeMethod.Patch) {
            if (crashSeqSkip) {
                callback.failure(new ErrorCodeException(555));
                println("CRASH");
            } else {
                skipTrack += 10000;
                callback.success(new LocalDocumentChange("{\"x\":" + skipTrack * 7 + ",\"__seq\":" + skipTrack + "}"));
            }
        } else if (method == ComputeMethod.Rewind) {
            callback.success(new LocalDocumentChange("{\"x\":1000}"));
        } else {
            callback.failure(new ErrorCodeException(5));
        }
    }

    @Override
    public void delete(Key key, Callback<Void> callback) {
        logByKey.remove(key);
        callback.success(null);
    }
}
