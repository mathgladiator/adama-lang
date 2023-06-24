/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

public interface ScanDeploymentCallback {
  /*
  static StreamObserver<ScanDeploymentsResponse> WRAP(ScanDeploymentCallback callback) {
    return new StreamObserver<ScanDeploymentsResponse>() {
      @Override
      public void onNext(ScanDeploymentsResponse scanDeploymentsResponse) {
        callback.success();
      }

      @Override
      public void onError(Throwable throwable) {
        callback.failure();
      }

      @Override
      public void onCompleted() {
      }
    };
  }
   */

  void success();

  void failure();
}
