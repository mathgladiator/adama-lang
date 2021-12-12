package org.adamalang.web.contracts;


import org.adamalang.web.io.ConnectionContext;

/** this is the base of the service which is used to spawn ServiceConnection's when a new request comes online */
public interface ServiceBase {

    /** a new connection has presented itself */
    public ServiceConnection establish(ConnectionContext context);
}
