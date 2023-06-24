/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

/** the state of an item within the queue */
public enum WebQueueState {
  Created, // the item has been created
  Steady, // nothing new has happened
  Dirty, // the item has experienced a change
  Remove // the item needs to be removed
}
