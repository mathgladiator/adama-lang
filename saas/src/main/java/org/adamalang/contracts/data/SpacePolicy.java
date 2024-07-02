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
package org.adamalang.contracts.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.Json;
import org.adamalang.mysql.data.SpaceInfo;

import java.util.Set;

/** the policy backing a space */
public class SpacePolicy {
  public final int id;
  public final int owner;
  private final Set<Integer> developers;
  public ObjectNode policy;

  public SpacePolicy(SpaceInfo space) {
    this.id = space.id;
    this.owner = space.owner;
    this.developers = space.developers;
    this.policy = Json.parseJsonObject(space.policy);
  }

  public boolean checkPolicy(String method, DefaultPolicyBehavior defaultPolicyBehavior, AuthenticatedUser user) {
    if (user.isAdamaDeveloper && user.id == owner) {
      return true;
    }
    JsonNode node = policy.get(method);
    if (node != null && node.isObject()) {
      if (Json.readBool((ObjectNode) node, "developers", false)) {
        if (developers.contains(user.id)) {
          return true;
        }
      }
      {
        JsonNode authorities = node.get("allowed-authorities");
        if (authorities != null && authorities.isArray()) {
          for (int k = 0; k < authorities.size(); k++) {
            JsonNode authority = authorities.get(k);
            if (authority != null && authority.isTextual()) {
              if (user.who.authority.equals(authority.textValue())) {
                return true;
              }
            }
          }
        }
      }
      {
        JsonNode documents = node.get("allowed-documents");
        if (documents != null && documents.isArray()) {
          for (int k = 0; k < documents.size(); k++) {
            JsonNode document = documents.get(k);
            if (document != null && document.isTextual()) {
              if (user.who.authority.equals("doc/" + document.textValue())) {
                return true;
              }
            }
          }
        }
      }
      {
        JsonNode documentSpaces = node.get("allowed-document-spaces");
        if (documentSpaces != null && documentSpaces.isArray()) {
          for (int k = 0; k < documentSpaces.size(); k++) {
            JsonNode document = documentSpaces.get(k);
            if (document != null && document.isTextual()) {
              if (user.who.authority.startsWith("doc/" + document.textValue() + "/")) {
                return true;
              }
            }
          }
        }
      }
    }
    if (defaultPolicyBehavior == DefaultPolicyBehavior.OwnerAndDevelopers) {
      return developers.contains(user.id);
    }
    return false;
  }
}
