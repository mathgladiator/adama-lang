/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets.cache;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.cache.Measurable;
import org.adamalang.web.assets.AssetStream;

/** make the cache generic */
public interface CachedAsset extends Measurable {
  /** where the cached code is running */
  public SimpleExecutor executor();

  /** attach the stream to the cache; this returns a non-null valid when the cache needs to be filled */
  public AssetStream attachWhileInExecutor(AssetStream attach);

  /** the cached item needs to be removed */
  public void evict();
}
