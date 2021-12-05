package org.adamalang.web.contracts;


import org.adamalang.web.io.ConnectionContext;

public interface ServiceBase {
    public ServiceConnection establish(ConnectionContext context);
}
