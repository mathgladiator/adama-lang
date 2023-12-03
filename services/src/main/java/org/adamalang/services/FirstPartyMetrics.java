/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.services;

import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

public class FirstPartyMetrics {
  public final RequestResponseMonitor amazon_ses_send;
  public final RequestResponseMonitor stripe_invoke;
  public final RequestResponseMonitor google_validate;
  public final RequestResponseMonitor sendgrid_sendmail;
  public final RequestResponseMonitor webpush_send;
  public final RequestResponseMonitor capacitor_send;
  public final RequestResponseMonitor facebook_validate;
  public final RequestResponseMonitor github_validate;
  public final RequestResponseMonitor twitter_validate;

  public FirstPartyMetrics(MetricsFactory factory) {
    amazon_ses_send = factory.makeRequestResponseMonitor("fpm_amazon_ses_send");
    stripe_invoke = factory.makeRequestResponseMonitor("fpm_stripe_invoke");
    google_validate = factory.makeRequestResponseMonitor("fpm_google_validate");
    sendgrid_sendmail = factory.makeRequestResponseMonitor("fpm_sendgrid_sendmail");
    webpush_send = factory.makeRequestResponseMonitor("fpm_webpush_send");
    capacitor_send = factory.makeRequestResponseMonitor("fpm_capacitor_send");
    facebook_validate = factory.makeRequestResponseMonitor("fpm_facebook_validate");
    github_validate = factory.makeRequestResponseMonitor("fpm_github_validate");
    twitter_validate = factory.makeRequestResponseMonitor("fpm_twitter_validate");
  }
}
