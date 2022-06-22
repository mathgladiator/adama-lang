/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

/** an event that happens outside of message flow */
public enum DocumentEvent {
  AskCreation(true, "__onCanCreate", "boolean"), //
  AskInvention(true, "__onCanInvent", "boolean"), //
  AskSendWhileDisconnected(true, "__onCanSendWhileDisconnected", "boolean"), //

  AskAssetAttachment(false, "__onCanAssetAttached", "boolean"), //
  AssetAttachment(false, "__onAssetAttached", "void", "NtAsset"), //
  ClientConnected(false, "__onConnected", "boolean"), //
  ClientDisconnected(false, "__onDisconnected", "void"); //

  public final boolean isStaticPolicy;
  public final String prefix;
  public final String returnType;
  public final boolean hasParameter;
  public final String parameterType;

  private DocumentEvent(boolean isStaticPolicy, String prefix, String returnType) {
    this.isStaticPolicy = isStaticPolicy;
    this.prefix = prefix;
    this.returnType = returnType;
    this.hasParameter = false;
    this.parameterType = null;
  }

  private DocumentEvent(boolean isStaticPolicy, String prefix, String returnType, String parameterType) {
    this.isStaticPolicy = isStaticPolicy;
    this.prefix = prefix;
    this.returnType = returnType;
    this.hasParameter = parameterType != null;
    this.parameterType = parameterType;
  }
}
