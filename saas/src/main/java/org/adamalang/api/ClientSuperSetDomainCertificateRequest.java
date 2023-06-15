/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

/** generated request type for super/set-domain-certificate */
public class ClientSuperSetDomainCertificateRequest {
  public String identity;
  public String domain;
  public String certificate;
  public Long timestamp;
}
