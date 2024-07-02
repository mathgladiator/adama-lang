/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common.metrics;

public class NoOpMetricsFactory implements MetricsFactory {
  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String name) {
    return new RequestResponseMonitor() {
      @Override
      public RequestResponseMonitorInstance start() {
        return new RequestResponseMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void extra() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public StreamMonitor makeStreamMonitor(String name) {
    return new StreamMonitor() {
      @Override
      public StreamMonitorInstance start() {
        return new StreamMonitorInstance() {
          @Override
          public void progress() {

          }

          @Override
          public void finish() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    return new CallbackMonitor() {
      @Override
      public CallbackMonitorInstance start() {
        return new CallbackMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public Runnable counter(String name) {
    return () -> {
    };
  }

  @Override
  public Inflight inflight(String name) {
    return new Inflight() {
      @Override
      public void up() {

      }

      @Override
      public void down() {

      }

      @Override
      public void set(int value) {
      }
    };
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    return new ItemActionMonitor() {
      @Override
      public ItemActionMonitorInstance start() {
        return new ItemActionMonitorInstance() {
          @Override
          public void executed() {

          }

          @Override
          public void rejected() {

          }

          @Override
          public void timeout() {

          }
        };
      }
    };
  }

  @Override
  public void page(String name, String title) {
  }

  @Override
  public void section(String title) {
  }
}
