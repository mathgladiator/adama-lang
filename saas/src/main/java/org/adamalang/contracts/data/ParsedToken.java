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
package org.adamalang.contracts.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;

import java.util.Base64;
import java.util.regex.Pattern;

/** a pre-validated parsed token; we parse to find which keys to look up */
public class ParsedToken {
  public final String iss;
  public final String sub;
  public final int key_id;
  public final int proxy_user_id;
  public final String proxy_authority;
  public final String proxy_origin;
  public final String proxy_ip;
  public final String proxy_asset_key;
  public final String proxy_useragent;

  public ParsedToken(String token) throws ErrorCodeException {
    String[] parts = token.split(Pattern.quote("."));
    if (parts.length == 3) {
      try {
        String middle = new String(Base64.getDecoder().decode(parts[1]));
        JsonMapper mapper = new JsonMapper();
        JsonNode treeRaw = mapper.readTree(middle);
        if (treeRaw != null && treeRaw.isObject()) {
          ObjectNode tree = (ObjectNode) treeRaw;
          JsonNode _iss = tree.get("iss");
          JsonNode _sub = tree.get("sub");
          JsonNode _key_id = tree.get("kid");
          if (_key_id != null && _key_id.isIntegralNumber()) {
            this.key_id = _key_id.asInt();
          } else {
            this.key_id = -1;
          }
          if (tree.has("puid")) {
            this.proxy_user_id = tree.get("puid").asInt();
          } else {
            this.proxy_user_id = 0;
          }
          this.proxy_authority = Json.readString(tree, "pa");
          this.proxy_origin = Json.readString(tree, "po");
          this.proxy_ip = Json.readString(tree, "pip");
          this.proxy_asset_key = Json.readString(tree, "pak");
          this.proxy_useragent = Json.readString(tree, "pua");
          if (_iss != null && _iss.isTextual() && _sub != null && _sub.isTextual()) {
            this.iss = _iss.textValue();
            this.sub = _sub.textValue();
            return;
          }
        }
        throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_JSON_COMPLETE);
      } catch (Exception ex) {
        throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_JSON, ex);
      }
    } else {
      throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_LAYOUT);
    }
  }
}
