package org.adamalang.grpc.client.contracts;

import io.grpc.stub.StreamObserver;
import org.adamalang.grpc.proto.ScanDeploymentsResponse;

public interface ScanDeploymentCallback {
  public void success();

  public void failure();

  public static StreamObserver<ScanDeploymentsResponse> WRAP(ScanDeploymentCallback callback) {
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
}
