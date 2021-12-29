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
package org.adamalang.gossip;

public interface Metrics {
    public void bump_sad_return();

    public void bump_complement();

    public void bump_optimistic_return();

    public void bump_turn_tables();

    public void bump_start();

    public void bump_found_reverse();

    public void bump_quick_gossip();

    public void bump_slow_gossip();

    public void log_error(Throwable cause);
}
