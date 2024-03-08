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
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;

/** a task that has been enqueued to be converted once settled */
public class EnqueuedTask {
  public final int messageId;
  public final NtPrincipal who;
  public final String channel;
  public final NtDynamic message;
  public final int viewId;

  public EnqueuedTask(int messageId, NtPrincipal who, String channel, int viewId, NtDynamic message) {
    this.messageId = messageId;
    this.who = who;
    this.channel = channel;
    this.viewId = viewId;
    this.message = message;
  }

  public void writeTo(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("who");
    writer.writeNtPrincipal(who);
    writer.writeObjectFieldIntro("channel");
    writer.writeString(channel);
    if (viewId > 0) {
      writer.writeObjectFieldIntro("view_id");
      writer.writeInteger(viewId);
    }
    writer.writeObjectFieldIntro("message");
    writer.writeNtDynamic(message);
    writer.endObject();
  }
}
