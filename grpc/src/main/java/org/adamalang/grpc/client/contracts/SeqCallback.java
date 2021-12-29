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
package org.adamalang.grpc.client.contracts;

/** various document operations (attach/send) result in a sequencer; these operations may also fail */
public interface SeqCallback {
    /** the operation was a success */
    public void success(int seq);

    /** the operation failed */
    public void error(int code);
}
