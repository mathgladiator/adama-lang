/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.definitions;

/** an event that happens outside of message flow */
public enum DocumentEvent {
  AskCreation(true, true, "__onCanCreate", "boolean"), //
  AskInvention(true, true, "__onCanInvent", "boolean"), //
  AskSendWhileDisconnected(true, true, "__onCanSendWhileDisconnected", "boolean"), //

  Load(false, false, "__onLoad", "void"), //

  AskAssetAttachment(false, true, "__onCanAssetAttached", "boolean"), //
  AssetAttachment(false, true, "__onAssetAttached", "void", "NtAsset"), //
  Delete(false, true, "__delete", "boolean"), //
  ClientConnected(false, true, "__onConnected", "boolean"), //
  ClientDisconnected(false, true, "__onDisconnected", "void"); //

  public final boolean hasPrincipal;
  public final boolean isStaticPolicy;
  public final String prefix;
  public final String returnType;
  public final boolean hasParameter;
  public final String parameterType;

  private DocumentEvent(boolean isStaticPolicy, boolean hasPrincipal, String prefix, String returnType) {
    this.isStaticPolicy = isStaticPolicy;
    this.hasPrincipal = hasPrincipal;
    this.prefix = prefix;
    this.returnType = returnType;
    this.hasParameter = false;
    this.parameterType = null;
  }

  private DocumentEvent(boolean isStaticPolicy, boolean hasPrincipal, String prefix, String returnType, String parameterType) {
    this.isStaticPolicy = isStaticPolicy;
    this.hasPrincipal = hasPrincipal;
    this.prefix = prefix;
    this.returnType = returnType;
    this.hasParameter = parameterType != null;
    this.parameterType = parameterType;
  }
}
