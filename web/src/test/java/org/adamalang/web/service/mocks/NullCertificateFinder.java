/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service.mocks;

import io.netty.handler.ssl.SslContext;
import org.adamalang.common.Callback;
import org.adamalang.web.contracts.CertificateFinder;

public class NullCertificateFinder implements CertificateFinder {
  @Override
  public void fetch(String domain, Callback<SslContext> callback) {
    callback.success(null);
  }
}
