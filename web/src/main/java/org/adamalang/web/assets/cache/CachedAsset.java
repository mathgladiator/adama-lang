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
