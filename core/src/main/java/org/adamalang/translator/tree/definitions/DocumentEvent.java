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
