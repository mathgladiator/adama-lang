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
package org.adamalang.web.contracts;


import org.adamalang.web.io.ConnectionContext;

/** this is the base of the service which is used to spawn ServiceConnection's when a new request comes online */
public interface ServiceBase {

    /** a new connection has presented itself */
    public ServiceConnection establish(ConnectionContext context);
}
