package org.adamalang.runtime.sys;

import org.adamalang.runtime.ErrorCodes;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.HashMap;
import java.util.concurrent.Executor;

/** This defines the state required within a thread to run a document. As Documents run in isolated
 * thread without synchronization, access to a durable living document must be access via this base.
 */
public class DocumentThreadBase {
    public final DataService service;
    public final Executor executor; // TODO: turn this into a scheduled executor service
    public final HashMap<DataService.Key, DurableLivingDocument> map;
    public final TimeSource time;

    public DocumentThreadBase(DataService service, Executor executor, TimeSource time) {
        this.service = service;
        this.executor = executor;
        this.time = time;
        this.map = new HashMap<>();
    }

    public void createIfNotExists(DataService.Key key, LivingDocumentFactory factory, NtClient who, String arg, String entropy, DocumentMonitor monitor, Callback<DurableLivingDocument> callback) {
        executor.execute(() -> {
            DurableLivingDocument.fresh(key, factory, who, arg, entropy, monitor, this, new Callback<DurableLivingDocument>() {
                @Override
                public void success(DurableLivingDocument value) {
                    executor.execute(() -> {
                        if (map.putIfAbsent(key, value) != null) {
                            callback.failure(new ErrorCodeException(ErrorCodes.E4_CREATE_ALREADY_EXISTS));
                        } else {
                            callback.success(value);
                        }
                    });
                }

                @Override
                public void failure(ErrorCodeException ex) {
                    callback.failure(ex);
                }
            });
        });
    }

    public void findOrLoad(DataService.Key key, LivingDocumentFactory factory, DocumentMonitor monitor, Callback<DurableLivingDocument> callback) {
        executor.execute(() -> {
            DurableLivingDocument document = map.get(key);
            if (document == null) {
                DurableLivingDocument.load(key, factory, monitor, this, new Callback<>() {
                    @Override
                    public void success(DurableLivingDocument value) {
                        executor.execute(() -> {
                            DurableLivingDocument prior = map.putIfAbsent(key, value);
                            if (prior != null) {
                                callback.success(prior);
                            } else {
                                callback.success(value);
                            }
                        });
                    }

                    @Override
                    public void failure(ErrorCodeException ex) {
                        callback.failure(ex);
                    }
                });
            } else {
                callback.success(document);
            }
        });
    }
}
