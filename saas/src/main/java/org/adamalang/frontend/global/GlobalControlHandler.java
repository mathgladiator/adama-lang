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
package org.adamalang.frontend.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.common.dns.ApexDomain;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.contracts.data.DefaultPolicyBehavior;
import org.adamalang.frontend.Session;
import org.adamalang.frontend.SpaceTemplates;
import org.adamalang.mysql.data.*;
import org.adamalang.mysql.model.*;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.AsyncCompiler;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.contracts.SpacePolicyLocator;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.client.*;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.io.NoOpJsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;

public class GlobalControlHandler implements RootGlobalHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalControlHandler.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  private final GlobalExternNexus nexus;
  private final Random rng;
  private final SpacePolicyLocator policyLocator;

  public GlobalControlHandler(GlobalExternNexus nexus, SpacePolicyLocator policyLocator) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
    this.policyLocator = policyLocator;
  }

  @Override
  public void handle(Session session, DocumentDownloadArchiveRequest request, BackupStreamResponder responder) {
    Key key = new Key(request.space, request.key);
    nexus.finder.find(key, new Callback<>() {
      @Override
      public void success(DocumentLocation location) {
        if (location.archiveKey == null) {
          responder.error(new ErrorCodeException(ErrorCodes.CUSTOMER_BACKUP_DOWNLOAD_NO_ARCHIVE_YET));
          return;
        }
        nexus.s3.streamBackupArchive(key, location.archiveKey, new SimpleHttpResponder() {
          boolean good = false;
          @Override
          public void start(SimpleHttpResponseHeader header) {
            good = header.status == 200;
            if (!good) {
              responder.error(new ErrorCodeException(ErrorCodes.CUSTOMER_BACKUP_DOWNLOAD_FAILED));
            }
          }

          @Override
          public void bodyStart(long size) {}

          @Override
          public void bodyFragment(byte[] chunk, int offset, int len) {
            if (good) {
              MessageDigest digest = Hashing.md5();
              digest.update(chunk, offset, len);
              String md5 = Hashing.finishAndEncode(digest);
              final String b64;
              if (offset == 0 && len == chunk.length) {
                b64 = new String(Base64.getEncoder().encode(chunk), StandardCharsets.UTF_8);
              } else {
                byte[] clone = new byte[len];
                System.arraycopy(chunk, offset, clone, 0, len);
                b64 = new String(Base64.getEncoder().encode(clone), StandardCharsets.UTF_8);
              }
              responder.next(b64, md5);
            }
          }

          @Override
          public void bodyEnd() {
            if (good) {
              responder.finish();
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            good = false;
            responder.error(ex);
          }
        });
      }
      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentListPushTokensRequest request, TokenStreamResponder responder) {
    try {
      List<DeviceSubscription> subscriptions = PushSubscriptions.list(nexus.database, request.domain, new NtPrincipal(request.agent, "doc/" + request.space + "/" + request.key));
      for (DeviceSubscription sub : subscriptions) {
        responder.next((long) sub.id, Json.parseJsonObject(sub.subscription), Json.parseJsonObject(sub.deviceInfo));
      }
      responder.finish();
    } catch (Exception ex) {
      responder.error(new ErrorCodeException(ErrorCodes.FAILED_LIST_PUSH_TOKENS_UNKNOWN));
    }
  }

  @Override
  public void handle(Session session, DeinitRequest request, SimpleResponder responder) {
    try {
      if (!Spaces.list(nexus.database, request.who.id, "", 10).isEmpty()) {
        responder.error(new ErrorCodeException(ErrorCodes.FAILED_DEINIT_SPACES_EXIST));
        return;
      }
      if (!Authorities.list(nexus.database, request.who.id).isEmpty()) {
        responder.error(new ErrorCodeException(ErrorCodes.FAILED_DEINIT_AUTHORITIES_EXIST));
        return;
      }
      if (!Domains.list(nexus.database, request.who.id).isEmpty()) {
        responder.error(new ErrorCodeException(ErrorCodes.FAILED_DEINIT_DOMAINS_EXIST));
        return;
      }
      Users.deleteUser(nexus.database, request.who.id);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.FAILED_DEINIT_UNKONWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        String hash = SCryptUtil.scrypt(request.password, 16384, 8, 1);
        Users.setPasswordHash(nexus.database, request.who.id, hash);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_ONLY_ADAMA_DEV_EXCEPTION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_PASSWORD_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AccountGetPaymentPlanRequest request, PaymentResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        // Do the DB lookup now
        String paymentInfo = Users.getPaymentInfo(nexus.database, request.who.id);
        // TODO: if no customerId, then bind one here and put it back into storage (need to handle conflicts better, or just use a better schema)
        // TODO: parse out information that is useful for stripe to update credit card, etc...
        responder.complete("none", "disabled");
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GET_PAYMENT_INFO_ONLY_ADAMA_DEV_EXCEPTION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GET_PAYMENT_INFO_UNKNOWN, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder) {
    try {
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + request.accessToken);
      SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://www.googleapis.com/oauth2/v1/userinfo", headers, SimpleHttpRequestBody.EMPTY);
      nexus.webBase.execute(get, new StringCallbackHttpResponder(LOG, nexus.frontendMetrics.google_account_translate.start(), new Callback<String>() {
        @Override
        public void success(String value) {
          ObjectNode googleProfile = Json.parseJsonObject(value);
          String email = googleProfile.get("email").textValue();
          try {
            ValidateEmail.validate(email);
          } catch (ErrorCodeException failedValidate) {
            responder.error(failedValidate);
            return;
          }
          getOrCreateUser(email, new Callback<Integer>() {
            @Override
            public void success(Integer userId) {
              try {
                String profileOld = Users.getProfile(nexus.database, userId);
                ObjectNode profile = Json.parseJsonObject(profileOld);
                boolean changedProfile = false;
                if (!profile.has("name") && googleProfile.has("name")) {
                  profile.set("name", googleProfile.get("name"));
                  changedProfile = true;
                }
                if (!profile.has("picture") && googleProfile.has("picture")) {
                  profile.set("picture", googleProfile.get("picture"));
                  changedProfile = true;
                }
                if (!profile.has("locale") && googleProfile.has("locale")) {
                  profile.set("locale", googleProfile.get("locale"));
                  changedProfile = true;
                }
                if (changedProfile) {
                  Users.setProfileIf(nexus.database, userId, profile.toString(), profileOld);
                }
                KeyPair pair = Jwts.SIG.ES256.keyPair().build();
                String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
                long expiry = System.currentTimeMillis() + 3 * 24 * 60 * 60000;
                Users.addKey(nexus.database, userId, publicKey, expiry);
                Users.validateUser(nexus.database, userId);
                responder.complete(Jwts.builder().subject("" + userId).expiration(new Date(expiry)).issuer("adama").signWith(pair.getPrivate()).compact());
              } catch (Exception ex) {
                responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CONVERT_TOKEN_VALIDATE_EXCEPTION, ex, LOGGER));
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              responder.error(ex);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      }));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CONVERT_TOKEN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder) {
    try {
      String hash = Users.getPasswordHash(nexus.database, request.userId);
      if (SCryptUtil.check(request.password, hash)) {
        KeyPair pair = Jwts.SIG.ES256.keyPair().build();
        String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
        long expiry = System.currentTimeMillis() + 14 * 24 * 60 * 60000;
        Users.addKey(nexus.database, request.userId, publicKey, expiry);
        responder.complete(Jwts.builder().subject("" + request.userId).expiration(new Date(expiry)).issuer("adama").signWith(pair.getPrivate()).compact());
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_INVALID));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LOGIN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private void getOrCreateUser(String email, Callback<Integer> callback) {
    try {
      int userId = Users.createUserId(nexus.database, email);
      nexus.email.sendWelcome(email);
      JsonStreamWriter arg = new JsonStreamWriter();
      arg.beginObject();
      arg.writeObjectFieldIntro("email");
      arg.writeString(email);
      arg.endObject();
      AuthenticatedUser user = new AuthenticatedUser(userId, new NtPrincipal("" + userId, "adama"), new ConnectionContext("adama", "0.0.0.0", "adama", null, null));
      nexus.adama.create(user, "billing", "" + userId, null, arg.toString(), new Callback<Void>() {
        @Override
        public void success(Void value) {
          callback.success(userId);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    } catch (Exception failedCreate) {
      try {
        callback.success(Users.getUserId(nexus.database, email));
      } catch (Exception failedFind) {
        callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_FAILED_FIND_UNKNOWN_EXCEPTION, failedFind, LOGGER));
      }
    }
  }

  @Override
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder) {
    getOrCreateUser(request.email, new Callback<Integer>() {
      @Override
      public void success(Integer userId) {
        try {
          String generatedCode = generateCode();
          String hash = SCryptUtil.scrypt(generatedCode, 16384, 8, 1);
          Users.addInitiationPair(nexus.database, userId, hash, System.currentTimeMillis() + 15 * 60000);
          nexus.email.sendCode(request.email, generatedCode);
          responder.complete();
        } catch (Exception ex) {
          responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_SETUP_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder) {
    try {
      for (IdHashPairing idHash : Users.listInitiationPairs(nexus.database, request.userId)) {
        if (SCryptUtil.check(request.code, idHash.hash)) {
          KeyPair pair = Jwts.SIG.ES256.keyPair().build();
          String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
          if (request.revoke != null && request.revoke) {
            Users.removeAllKeys(nexus.database, request.userId);
          }
          Users.addKey(nexus.database, request.userId, publicKey, System.currentTimeMillis() + 14 * 24 * 60 * 60000);
          responder.complete(Jwts.builder().subject("" + request.userId).issuer("adama").signWith(pair.getPrivate()).compact());
          Users.validateUser(nexus.database, request.userId);
          Users.deleteInitiationPairing(nexus.database, idHash.id);
          return;
        }
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_INIT_COMPLETE_CODE_MISMATCH));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_COMPLETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private String generateCode() {
    StringBuilder code = new StringBuilder();
    for (int j = 0; j < 2; j++) {
      int val = rng.nextInt(26 * 26 * 26 * 26);
      for (int k = 0; k < 4; k++) {
        code.append(('A' + (val % 26)));
        val /= 26;
      }
    }
    return code.toString();
  }

  @Override
  public void handle(Session session, ProbeRequest request, SimpleResponder responder) {
    responder.complete();
  }

  @Override
  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        String authority = ProtectedUUID.generate();
        Authorities.createAuthority(nexus.database, request.who.id, authority);
        responder.complete(authority);
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(nexus.database, request.who.id, request.authority, request.keyStore.toString());
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        // NOTE: getKeystorePublic validates ownership
        responder.complete(Json.parseJsonObject(Authorities.getKeystorePublic(nexus.database, request.who.id, request.authority)));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        for (String authority : Authorities.list(nexus.database, request.who.id)) {
          responder.next(authority);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        // NOTE: deleteAuthority validates ownership
        Authorities.deleteAuthority(nexus.database, request.who.id, request.authority);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  /** HACK: change *.blah.com to wildcard.blah.com */
  private String fixDomain(String domain) {
    return domain.replaceAll(Pattern.quote("*"), "wildcard");
  }

  @Override
  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder) {
    try {
      if (request.resolvedDomain.domain != null && request.resolvedDomain.policy != null && request.resolvedDomain.policy.checkPolicy("domain/get", DefaultPolicyBehavior.Owner, request.who)) {
        Domain domain = Domains.get(nexus.database, fixDomain(request.domain));
        if (domain != null) {
          responder.complete(domain.space);
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_GET_NOT_FOUND));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_GET_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_GET_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private void handleDomainMap(int spaceOwner, String domain, String certificate, String space, String key, boolean route, SimpleResponder responder) {
    try {
      String cert = certificate != null ? MasterKey.encrypt(nexus.masterKey, certificate) : null;
      if (Domains.map(nexus.database, spaceOwner, fixDomain(domain), space, key, route, cert)) { // Domains.map ensures ownership on UPDATE to prevent conflicts
        if (cert == null) {
          nexus.signalControl.raiseAutomaticDomain(domain);
        }
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_MAP_FAILED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_MAP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DomainClaimApexRequest request, DomainVerifyResponder responder) {
    if (!request.who.isAdamaDeveloper) {
      responder.error(new ErrorCodeException(ErrorCodes.APEX_DOMAIN_CLAIM_NOT_DEVELOPER));
      return;
    }
    if (!ApexDomain.test(request.domain)) {
      responder.error(new ErrorCodeException(ErrorCodes.APEX_DOMAIN_CLAIM_NOT_APEX_DOMAIN));
      return;
    }
    // we hash the future owner with the domain to create a token
    String stringToSign = request.who.who.agent + "/" + request.who.who.authority + "/" + request.domain;
    MessageDigest digest = Hashing.sha384();
    digest.update(stringToSign.getBytes(StandardCharsets.UTF_8));
    digest.update(stringToSign.getBytes(StandardCharsets.UTF_8));
    String token = "adama" + Hashing.finishAndEncodeHex(digest).substring(0, 32);

    // ask DNS for the token
    nexus.dnsTxtResolver.query(request.domain, new Callback<String[]>() {
      @Override
      public void success(String[] txtRecords) {
        // token was either found or not
        boolean found = false;
        for (String txt : txtRecords) {
          if (txt.equalsIgnoreCase(token)) {
            found = true;
          }
        }

        if (found) {
          nexus.dnsClaimer.execute(new NamedRunnable("claim-dns") {
            @Override
            public void execute() throws Exception {
              // TODO: ensureRecordExistsUnderOwner
              responder.complete(true, token);
            }
          });
        } else {
          responder.complete(false, token);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DomainRedirectRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainConfigureRequest request, SimpleResponder responder) {
    if (request.resolvedDomain.domain != null && request.resolvedDomain.policy != null && request.resolvedDomain.policy.checkPolicy("domain/configure", DefaultPolicyBehavior.OwnerAndDevelopers, request.who)) {
      try {
        Domains.putNativeAppConfig(nexus.database, request.domain, MasterKey.encrypt(nexus.masterKey, request.productConfig.toString()));
        responder.complete();
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_CONFIGURE_UNKNOWN_EXCEPTION, ex, LOGGER));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_CONFIGURE_NOT_AUTHORIZED));
    }
  }

  @Override
  public void handle(Session session, DomainGetVapidPublicKeyRequest request, DomainVapidResponder responder) {
    try {
      VAPIDPublicPrivateKeyPair pair = Domains.getOrCreateVapidKeyPair(nexus.database, request.domain, nexus.vapidFactory);
      responder.complete(pair.publicKeyBase64);
    } catch (Exception ex) {
      responder.error(new ErrorCodeException(ErrorCodes.VAPID_CREATE_UNKNOWN_FAILURE));
    }
  }

  @Override
  public void handle(Session session, PushRegisterRequest request, SimpleResponder responder) {
    try {
      // TODO: ask if the domain allows push notifications
      PushSubscriptions.registerSubscription(nexus.database, request.domain, request.who.who, request.subscription.toString(), request.deviceInfo.toString(), System.currentTimeMillis() + 14 * 90000);
      responder.complete();
    } catch (Exception ex) {
      responder.error(new ErrorCodeException(ErrorCodes.PUSH_REGISTER_UNKNOWN_FAILURE));
    }
  }

  @Override
  public void handle(Session session, DomainMapRequest request, SimpleResponder responder) {
    handleDomainMap(request.policy.owner, request.domain, request.certificate, request.space, null, false, responder);
  }

  @Override
  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder) {
    handleDomainMap(request.policy.owner, request.domain, request.certificate, request.space, request.key, false, responder);
  }

  @Override
  public void handle(Session session, DomainListRequest request, DomainListingResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        for (Domain domain : Domains.list(nexus.database, request.who.id)) {
          responder.next(domain.domain, domain.space, domain.key, domain.routeKey, domain.forwardTo);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_LIST_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DomainListBySpaceRequest request, DomainListingResponder responder) {
    try {
      for (Domain domain : Domains.listBySpace(nexus.database, request.space)) {
        responder.next(domain.domain, domain.space, domain.key, domain.routeKey, domain.forwardTo);
      }
      responder.finish();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_LIST_BY_SPACE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder) {
    try {
      if (request.resolvedDomain.domain != null && request.resolvedDomain.policy != null && request.resolvedDomain.policy.checkPolicy("domain/unmap", DefaultPolicyBehavior.Owner, request.who)) {
        if (Domains.unmap(nexus.database, request.resolvedDomain.domain.owner, fixDomain(request.domain))) {
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_UNMAP_FAILED));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_UNMAP_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_UNMAP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetPolicyRequest request, AccessPolicyResponder responder) {
    responder.complete(request.policy.policy);
  }

  @Override
  public void handle(Session session, SpaceSetPolicyRequest request, SimpleResponder responder) {
    try {
      Spaces.setPolicy(nexus.database, request.policy.id, request.space);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_POLICY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder) {
    try {
      responder.complete(Spaces.getRxHtml(nexus.database, request.policy.id));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_RXHTML_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder) {
    try {
      Spaces.setRxHtml(nexus.database, request.policy.id, request.rxhtml);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_RXHTML_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceMetricsRequest request, MetricsAggregateResponder responder) {
    try {
      List<String> metrics = Metrics.downloadMetrics(nexus.database, request.space, request.prefix == null ? "" : request.prefix);
      nexus.metrics.execute(new NamedRunnable("build-report") {
        @Override
        public void execute() throws Exception {
          ObjectNode result = Json.newJsonObject();
          for (String metric : metrics) {
            Iterator<Map.Entry<String, JsonNode>> it = Json.parseJsonObject(metric).fields();
            while (it.hasNext()) {
              Map.Entry<String, JsonNode> val = it.next();
              JsonNode prior = result.get(val.getKey());
              if (prior == null) {
                result.set(val.getKey(), val.getValue());
              } else {
                result.put(val.getKey(), val.getValue().doubleValue() + prior.doubleValue());
              }
            }
          }
          responder.complete(result, metrics.size());
        }
      });
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_METRICS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private boolean isReservedSpace(String space) {
    return "ide".equals(space) || "wildcard".equals(space) || "billing".equals(space);
  }

  @Override
  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder) {
    try {
      if (isReservedSpace(request.space)) {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_SPACE_RESERVED));
        return;
      }
      if (request.who.isAdamaDeveloper) {
        int spaceId = Spaces.createSpace(nexus.database, request.who.id, request.space);
        SpaceTemplates.SpaceTemplate template = SpaceTemplates.REGISTRY.of(request.template);
        nexus.adama.create(request.who, "ide", request.space, null, template.idearg(request.space), new Callback<Void>() {
          @Override
          public void success(Void value) {
            SpacePolicy policy = new SpacePolicy(new SpaceInfo(spaceId, request.who.id, Collections.singleton(request.who.id), true, 0, "{}"));
            handle(session, new SpaceSetRequest(request.identity, request.who, request.space, policy, template.plan()), new SimpleResponder(new NoOpJsonResponder()));
            try {
              Spaces.setRxHtml(nexus.database, spaceId, template.initialRxHTML(request.space)); // TODO: put into createSpace? Or, rely on the document
              responder.complete();
            } catch (Exception ex) {
              responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_UNABLE_SET_RXHTML_EXCEPTION, ex, LOGGER));
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            if (ex.code == ErrorCodes.SERVICE_DOCUMENT_ALREADY_CREATED) {
              // it already exists, assume it has been deployed so we don't nuke it!
              responder.complete();
            } else {
              responder.error(ex);
            }
          }
        });
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_CREATE_FAILED_NOT_ADAMA_DEVELOPER));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRequest request, PlanResponder responder) {
    try {
      responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.database, request.policy.id)));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder) {
    try {
      String planJson = request.plan.toString();
      // hash the plan
      MessageDigest digest = Hashing.md5();
      digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
      String hash = Hashing.finishAndEncode(digest);
      // Change the master plan
      Spaces.setPlan(nexus.database, request.policy.id, planJson, hash);

      Callback<Integer> postDirectSend = new Callback<>() {
        @Override
        public void success(Integer value) {
          // iterate the targets with this space loaded
          nexus.adama.deployLocal(request.space);
          nexus.adama.waitForCapacity(request.space, 30000, (found) -> {
            if (found) {
              responder.complete();
            } else {
              responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_DEPLOYMENT_FAILED_FINDING_CAPACITY));
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      };

      DeploymentPlan plan = new DeploymentPlan(planJson, LOGGER);
      AsyncCompiler.forge(request.space, null, plan, Deliverer.FAILURE, new TreeMap<>(), nexus.byteCodeCache, new Callback<>() {
        @Override
        public void success(DeploymentFactory value) {
          nexus.adama.directSend(request.who, "ide", request.space, null, "signal_deployment", "{}", postDirectSend);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder) {
    try {
      nexus.adama.deployLocal(request.space);
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_KICK_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder) { // policy check is generated
    try {
      if (FinderOperations.list(nexus.database, request.space, null, 1).size() > 0) {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NOT_EMPTY));
        return;
      }
      Spaces.changePrimaryOwner(nexus.database, request.policy.id, request.policy.owner, 0);
      Domains.deleteSpace(nexus.database, request.space);
      Capacity.removeAll(nexus.database, request.space);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_DELETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder) {
    try {
      Role role = Role.from(request.role);
      Spaces.setRole(nexus.database, request.policy.id, request.userId, role);
      SpaceInfo updatedSpaceInfo = Spaces.getSpaceInfo(nexus.database, request.space);
      JsonStreamWriter syncDevelopers = new JsonStreamWriter();
      syncDevelopers.beginObject();
      syncDevelopers.writeObjectFieldIntro("developers");
      syncDevelopers.beginArray();
      for (Integer devId : updatedSpaceInfo.developers) {
        syncDevelopers.writeNtPrincipal(new NtPrincipal("" + devId, "adama"));
      }
      syncDevelopers.endArray();
      syncDevelopers.endObject();
      nexus.adama.directSend(request.who, "ide", request.space, null, "set_developers_from_frontend", syncDevelopers.toString(), new Callback<Integer>() {
        @Override
        public void success(Integer value) {
          responder.complete();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceListDevelopersRequest request, DeveloperResponder responder) {
    try {
      for (Developer developer : Spaces.listDevelopers(nexus.database, request.policy.id)) {
        responder.next(developer.email, developer.role);
      }
      responder.finish();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_DEVELOPERS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder) {
    nexus.adama.reflect(request.space, request.key, new Callback<String>() {
      @Override
      public void success(String value) {
        responder.complete(Json.parseJsonObject(value));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DomainReflectRequest request, ReflectionResponder responder) {
    if (request.resolvedDomain.domain != null && request.resolvedDomain.policy != null && request.resolvedDomain.policy.checkPolicy("domain/reflect", DefaultPolicyBehavior.OwnerAndDevelopers, request.who)) {
      String key = request.resolvedDomain.domain.key != null ? request.resolvedDomain.domain.key : "";
      nexus.adama.reflect(request.resolvedDomain.domain.space, key, new Callback<String>() {
        @Override
        public void success(String value) {
          responder.complete(Json.parseJsonObject(value));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_REFLECT_BY_DOMAIN_NOT_AUTHORIZED));
    }
  }

  @Override
  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder) {
    try {
      if (request.who.isAdamaDeveloper) {
        for (SpaceListingItem spaceListingItem : Spaces.list(nexus.database, request.who.id, request.marker, request.limit == null ? 100 : request.limit)) {
          responder.next(spaceListingItem.name, spaceListingItem.callerRole, spaceListingItem.created, spaceListingItem.enabled, spaceListingItem.storageBytes);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }



  @Override
  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder) {
    try {
      for (DocumentIndex item : FinderOperations.list(nexus.database, request.space, request.marker, request.limit != null ? request.limit : 100)) {
        responder.next(item.key, item.created, item.updated, item.seq, item.backup != null ? item.backup : "never");
      }
      responder.finish();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_DOCUMENTS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGenerateKeyRequest request, KeyPairResponder responder) {
    try {
      KeyPair pair = PublicPrivateKeyPartnership.genKeyPair();
      String privateKeyEncrypted = MasterKey.encrypt(nexus.masterKey, PublicPrivateKeyPartnership.privateKeyOf(pair));
      int keyId = Secrets.insertSecretKey(nexus.database, request.space, privateKeyEncrypted);
      responder.complete(keyId, PublicPrivateKeyPartnership.publicKeyOf(pair));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GENERATE_KEY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder) {
    if ("super".equals(request.who.who.authority)) {
      try {
        Sentinel.ping(nexus.database, "super", System.currentTimeMillis());
        responder.complete();
      } catch (Exception ex) {
        responder.error(new ErrorCodeException(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_CHECKIN));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_CHECKIN));
    }
  }

  @Override
  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder) {
    if ("super".equals(request.who.who.authority)) {
      try {
        for (Domain domain : Domains.superListAutoDomains(nexus.database, request.timestamp)) {
          responder.next(domain.domain, domain.timestamp);
        }
        responder.finish();
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_LIST, ex, LOGGER));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_LIST));
    }
  }

  @Override
  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder) {
    if ("super".equals(request.who.who.authority)) {
      try {
        if (request.certificate != null) {
          Domains.superSetAutoCert(nexus.database, request.domain, MasterKey.encrypt(nexus.masterKey, request.certificate), request.timestamp);
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.SUPER_INVALID_CERT_SET_AUTOMATIC));
          return;
        }
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_SET_AUTOMATIC, ex, LOGGER));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_SET_AUTOMATIC));
    }
  }

  private boolean checkRegionalHost(AuthenticatedUser who, JsonResponder responder) {

    return false;
  }

  /*********
   * Initializing a regional host
   *********/

  @Override
  public void handle(Session session, RegionalInitHostRequest request, HostInitResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      try {
        int publicKeyId = Hosts.initializeHost(nexus.database, request.region, request.machine, request.role, request.publicKey);
        responder.complete(publicKeyId);
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_FAILED_HOST_INIT, ex, LOGGER));
      }
    }
  }

  /*********
   * Looking up domains from data regions
   *********/
  @Override
  public void handle(Session session, RegionalDomainLookupRequest request, DomainRawResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      try {
        Domain domain = request.resolvedDomain.domain;
        String cert = domain.certificate;
        if (domain.certificate != null) {
          cert = MasterKey.decrypt(nexus.masterKey, cert);
        }
        responder.complete(domain.domain, domain.owner, domain.space, domain.key, domain.forwardTo, domain.routeKey, cert, domain.timestamp);
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.GLOBAL_DOMAIN_FIND_EXCEPTION, ex, LOGGER));
      }
    }
  }

  @Override
  public void handle(Session session, RegionalEmitMetricsRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.metricsReporter.emitMetrics(new Key(request.space, request.key), request.metrics.toString());
      responder.complete();
    }
  }

  /*********
   * Finding and binding keys to machines for the data regions
   *********/
  @Override
  public void handle(Session session, RegionalFinderFindRequest request, FinderResultResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finder.find(new Key(request.space, request.key), WRAP(responder));
    }
  }

  @Override
  public void handle(Session session, RegionalFinderBindRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.bind(new Key(request.space, request.key), request.region, request.machine, new Callback<Void>() {
        @Override
        public void success(Void value) {
          responder.complete();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          success(null);
        }
      });
    }
  }
  @Override
  public void handle(Session session, RegionalFinderDeleteCommitRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.commitDelete(new Key(request.space, request.key), request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalFinderDeleteMarkRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.markDelete(new Key(request.space, request.key), request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalFinderDeletionListRequest request, KeysResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.listDeleted(request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalFinderBackUpRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.backup(new Key(request.space, request.key), new BackupResult(request.archive, request.seq, request.deltaBytes, request.assetBytes), request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalFinderListRequest request, KeysResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.list(request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalFinderFreeRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.finderCore.free(new Key(request.space, request.key), request.region, request.machine, WRAP(responder));
    }
  }

  /*********
   * Auth for regions
   *********/
  @Override
  public void handle(Session session, RegionalAuthRequest request, AuthResultResponder responder) {
    AuthenticatedUser user = request.who;
    responder.complete((long) user.id, user.who.agent, user.who.authority);
  }

  /*********
   * Regional deployment support
   *********/
  @Override
  public void handle(Session session, RegionalGetPlanRequest request, PlanWithKeysResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      try {
        ObjectNode keys = Json.newJsonObject();
        for (Map.Entry<Integer, PrivateKeyBundle> entry : Secrets.getKeys(nexus.database, nexus.masterKey, request.space).entrySet()) {
          keys.put("" + entry.getKey(), entry.getValue().getPrivateKey());
        }
        responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.database, request.policy.id)), keys);
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(0, ex, LOGGER));
      }
    }
  }

  /*********
   * Capacity Management for regions
   *********/
  @Override
  public void handle(Session session, RegionalCapacityAddRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.add(request.space, request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityNukeRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.nuke(request.space, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityRemoveRequest request, SimpleResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.add(request.space, request.region, request.machine, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityPickSpaceHostRequest request, CapacityHostResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.pickStableHostForSpace(request.space, request.region, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityPickSpaceHostNewRequest request, CapacityHostResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.pickNewHostForSpace(request.space, request.region, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityListSpaceRequest request, CapacityListResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.listAllSpace(request.space, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityListRegionRequest request, CapacityListResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.listWithinRegion(request.space, request.region, WRAP(responder));
    }
  }
  @Override
  public void handle(Session session, RegionalCapacityListMachineRequest request, CapacityListResponder responder) {
    if (checkRegionalHost(request.who, responder.responder)) {
      nexus.overseer.listAllOnMachine(request.region, request.machine, WRAP(responder));
    }
  }

  @Override
  public void disconnect() {
  }

  private static Callback<Integer> WRAP(SeqResponder seqResponder) {
    return new Callback<>() {
      @Override
      public void success(Integer seq) {
        seqResponder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        seqResponder.error(ex);
      }
    };
  }
  private static Callback<Void> WRAP(SimpleResponder responder) {
    return new Callback<Void>() {
      @Override
      public void success(Void v) {
        responder.complete();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
  private Callback<List<CapacityInstance>> WRAP(CapacityListResponder responder) {
    return new Callback<>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          responder.next(instance.space, instance.region, instance.machine, instance.override);
        }
        responder.finish();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
  public static Callback<DocumentLocation> WRAP(FinderResultResponder responder) {
    return new Callback<>() {
      @Override
      public void success(DocumentLocation value) {
        responder.complete(value.id, value.location.type, value.archiveKey, value.region, value.machine, value.deleted);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
  public static Callback<List<Key>> WRAP(KeysResponder responder) {
    return new Callback<List<Key>>() {
      @Override
      public void success(List<Key> list) {
        for (Key key : list) {
          responder.next(key.space, key.key);
        }
        responder.finish();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
  public static Callback<String> WRAP(CapacityHostResponder responder) {
    return new Callback<String>() {
      @Override
      public void success(String machine) {
        responder.complete(machine);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    };
  }
}
