# API Reference 
 Methods: 
[InitSetupAccount](#method-initsetupaccount), [InitConvertGoogleUser](#method-initconvertgoogleuser), [InitCompleteAccount](#method-initcompleteaccount), [Deinit](#method-deinit), [AccountSetPassword](#method-accountsetpassword), [AccountGetPaymentPlan](#method-accountgetpaymentplan), [AccountLogin](#method-accountlogin), [Probe](#method-probe), [Stats](#method-stats), [IdentityHash](#method-identityhash), [IdentityStash](#method-identitystash), [AuthorityCreate](#method-authoritycreate), [AuthoritySet](#method-authorityset), [AuthorityGet](#method-authorityget), [AuthorityList](#method-authoritylist), [AuthorityDestroy](#method-authoritydestroy), [SpaceCreate](#method-spacecreate), [SpaceGenerateKey](#method-spacegeneratekey), [SpaceGet](#method-spaceget), [SpaceSet](#method-spaceset), [SpaceRedeployKick](#method-spaceredeploykick), [SpaceSetRxhtml](#method-spacesetrxhtml), [SpaceGetRxhtml](#method-spacegetrxhtml), [SpaceSetPolicy](#method-spacesetpolicy), [PolicyGenerateDefault](#method-policygeneratedefault), [SpaceGetPolicy](#method-spacegetpolicy), [SpaceMetrics](#method-spacemetrics), [SpaceDelete](#method-spacedelete), [SpaceSetRole](#method-spacesetrole), [SpaceListDevelopers](#method-spacelistdevelopers), [SpaceReflect](#method-spacereflect), [SpaceList](#method-spacelist), [PushRegister](#method-pushregister), [DomainMap](#method-domainmap), [DomainClaimApex](#method-domainclaimapex), [DomainRedirect](#method-domainredirect), [DomainConfigure](#method-domainconfigure), [DomainReflect](#method-domainreflect), [DomainMapDocument](#method-domainmapdocument), [DomainList](#method-domainlist), [DomainListBySpace](#method-domainlistbyspace), [DomainGetVapidPublicKey](#method-domaingetvapidpublickey), [DomainUnmap](#method-domainunmap), [DomainGet](#method-domainget), [DocumentDownloadArchive](#method-documentdownloadarchive), [DocumentListBackups](#method-documentlistbackups), [DocumentDownloadBackup](#method-documentdownloadbackup), [DocumentListPushTokens](#method-documentlistpushtokens), [DocumentAuthorization](#method-documentauthorization), [DocumentAuthorizationDomain](#method-documentauthorizationdomain), [DocumentAuthorize](#method-documentauthorize), [DocumentAuthorizeDomain](#method-documentauthorizedomain), [DocumentAuthorizeWithReset](#method-documentauthorizewithreset), [DocumentAuthorizeDomainWithReset](#method-documentauthorizedomainwithreset), [DocumentCreate](#method-documentcreate), [DocumentDelete](#method-documentdelete), [DocumentList](#method-documentlist), [MessageDirectSend](#method-messagedirectsend), [MessageDirectSendOnce](#method-messagedirectsendonce), [ConnectionCreate](#method-connectioncreate), [ConnectionCreateViaDomain](#method-connectioncreateviadomain), [ConnectionSend](#method-connectionsend), [ConnectionPassword](#method-connectionpassword), [ConnectionSendOnce](#method-connectionsendonce), [ConnectionCanAttach](#method-connectioncanattach), [ConnectionAttach](#method-connectionattach), [ConnectionUpdate](#method-connectionupdate), [ConnectionEnd](#method-connectionend), [DocumentsHashPassword](#method-documentshashpassword), [BillingConnectionCreate](#method-billingconnectioncreate), [AttachmentStart](#method-attachmentstart), [AttachmentStartByDomain](#method-attachmentstartbydomain), [AttachmentAppend](#method-attachmentappend), [AttachmentFinish](#method-attachmentfinish)

## Method: InitSetupAccount (JS)
This initiates developer machine via email verification.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| email | yes | String | The email of an Adama developer. |


### Template
```js
connection.InitSetupAccount(email, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: InitConvertGoogleUser (JS)
The converts and validates a google token into an Adama token.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| access-token | yes | String | A token from a third party authorization service. |


### Template
```js
connection.InitConvertGoogleUser(access-token, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: InitCompleteAccount (JS)
This establishes a developer machine via email verification.

Copy the code from the email into this request.

The server will generate a key-pair and send the secret to the client to stash within their config, and the
public key will be stored to validate future requests made by this developer machine.

A public key will be held onto for 30 days.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| email | yes | String | The email of an Adama developer. |
| revoke | no | Boolean | A flag to indicate wiping out previously granted tokens. |
| code | yes | String | A randomly (secure) generated code to validate a user via 2FA auth (via email). |


### Template
```js
connection.InitCompleteAccount(email, revoke, code, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: Deinit (JS)
This will destroy a developer account. We require all spaces to be deleted along with all authorities.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.Deinit(identity, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: AccountSetPassword (JS)
Set the password for an Adama developer.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| password | yes | String | The password for your account or a document |


### Template
```js
connection.AccountSetPassword(identity, password, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: AccountGetPaymentPlan (JS)
Get the payment plan information for the developer.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.AccountGetPaymentPlan(identity, {
  success: function(response) {
    // response.paymentPlan
    // response.publishableKey
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| payment-plan | String | Payment plan name. The current default is "none" which can be upgraded to "public". |
| publishable-key | String | The public key from the merchant provider. |

## Method: AccountLogin (JS)
Sign an Adama developer in with an email and password pair.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| email | yes | String | The email of an Adama developer. |
| password | yes | String | The password for your account or a document |


### Template
```js
connection.AccountLogin(email, password, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: Probe (JS)
This is useful to validate an identity without executing anything.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.Probe(identity, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: Stats (JS)
Get stats for the current connection
This method has no parameters.

### Template
```js
connection.Stats({
  next: function(payload) {
    // payload.statKey
    // payload.statValue
    // payload.statType
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| stat-key | String | A key for the stats |
| stat-value | String | The value for a stat |
| stat-type | String | The type of the stat. |

## Method: IdentityHash (JS)
Validate an identity and convert to a public and opaque base64 crypto hash.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.IdentityHash(identity, {
  success: function(response) {
    // response.identityHash
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity-hash | String | A hash of an identity |

## Method: IdentityStash (JS)
Stash an identity locally in the connection as if it was a cookie

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| name | yes | String | An identifier to name the resource. |


### Template
```js
connection.IdentityStash(identity, name, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: AuthorityCreate
Create an authority. See [Authentication](/reference/auth.md) for more details.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| authority | String | An authority is collection of third party users authenticated via a public keystore. |

## Method: AuthoritySet
Set the public keystore for the authority.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| authority | yes | String | An authority is collection of users held together via a key store. |
| key-store | yes | ObjectNode | A collection of public keys used to validate an identity within an authority. |


This method simply returns void.

## Method: AuthorityGet
Get the public keystore for the authority.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| authority | yes | String | An authority is collection of users held together via a key store. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| keystore | ObjectNode | A bunch of public keys to validate tokens for an authority. |

## Method: AuthorityList
List authorities for the given developer.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| authority | String | An authority is collection of third party users authenticated via a public keystore. |

## Method: AuthorityDestroy
Destroy an authority.

This is exceptionally dangerous as it will break authentication for any users that have tokens based on that authority.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| authority | yes | String | An authority is collection of users held together via a key store. |


This method simply returns void.

## Method: SpaceCreate (JS)
Create a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| template | no | String | When creating a space, the template is a known special identifier for how to bootstrap the defaults. Examples: none (default when template parameter not present). |


### Template
```js
connection.SpaceCreate(identity, space, template, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: SpaceGenerateKey
Generate a secret key for a space.

First party and third party services require secrets such as api tokens or credentials.

These credentials must be encrypted within the Adama document using a public-private key, and the secret is derived via a key exchange.
Here, the server will generate a public/private key pair and store the private key securely and give the developer a public key.
The developer then generates a public/private key, encrypts the token with the private key, throws away the private key, and then embeds the key id, the developer's public key, and the encrypted credential within the adama source code.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| key-id | Integer | Unique id of the private-key used for a secret. |
| public-key | String | A public key to decrypt a secret with key arrangement. |

## Method: SpaceGet
Get the deployment plan for a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| plan | ObjectNode | A plan is a predictable mapping of keys to implementations.             The core reason for having multiple concurrent implementations is to have a smooth and orderly deployment.             See [deployment plans](/reference/deployment-plan.md) for more information. |

## Method: SpaceSet
Set the deployment plan for a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| plan | yes | ObjectNode | This 'plan' parameter contains multiple Adama scripts all gated on various rules.              These rules allow for a migration to happen slowly on your schedule.              Note: this value will validated such that the scripts are valid, compile, and will not have any major regressions             during role out. |


This method simply returns void.

## Method: SpaceRedeployKick
A diagnostic call to optimistically to refresh a space's deployment

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |


This method simply returns void.

## Method: SpaceSetRxhtml
Set the RxHTML forest for the space when viewed via a domain name.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| rxhtml | yes | String | A RxHTML forest which provides simplified web hosting. |


This method simply returns void.

## Method: SpaceGetRxhtml
Get the RxHTML forest for the space when viewed via a domain name.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| rxhtml | String | The RxHTML forest for a space. |

## Method: SpaceSetPolicy
Set the access control policy for a space

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| access-policy | yes | ObjectNode | A policy to control who can do what against a space. |


This method simply returns void.

## Method: PolicyGenerateDefault
Generate a default policy template for inspection and use

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| access-policy | ObjectNode | A policy to control who can do what against a space. |

## Method: SpaceGetPolicy
Returns the policy for a specific space

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| access-policy | ObjectNode | A policy to control who can do what against a space. |

## Method: SpaceMetrics
For regional proxies to emit metrics for a document

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| prefix | no | String | A prefix of a key used to filter results in a listing or computation |
| metric-query | no | ObjectNode | A metric query to override the behavior on aggregation for specific fields |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| metrics | ObjectNode | A metrics object is a bunch of counters/event-tally |
| count | Integer | The number of items considered/available. |

## Method: SpaceDelete (JS)
Delete a space.

This requires no documents to be within the space, and this removes the space from use until garbage collection ensures no documents were created for that space after deletion.
A space may be reserved for 90 minutes until the system is absolutely sure no documents will leak.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |


### Template
```js
connection.SpaceDelete(identity, space, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: SpaceSetRole
Set the role of an Adama developer for a particular space.

Spaces can be shared among Adama developers.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| email | yes | String | The email of an Adama developer. |
| role | yes | String | The role of a user may determine their capabilities to perform actions. |


This method simply returns void.

## Method: SpaceListDevelopers
List the developers with access to this space

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| email | String | A developer email |
| role | String | Each developer has a role to a document. |

## Method: SpaceReflect (JS)
Get a schema for the space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |


### Template
```js
connection.SpaceReflect(identity, space, key, {
  success: function(response) {
    // response.reflection
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| reflection | ObjectNode | Schema of a document. |

## Method: SpaceList (JS)
List the spaces available to the user.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| marker | no | String | A key to skip ahead a listing.             When iterating, values will be returned that are after marker.             To paginate an entire list, pick the last key or name returned and use it as the next marker. |
| limit | no | Integer | Maximum number of items to return during a streaming list. |


### Template
```js
connection.SpaceList(identity, marker, limit, {
  next: function(payload) {
    // payload.space
    // payload.role
    // payload.created
    // payload.enabled
    // payload.storageBytes
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| space | String | A space which is a collection of documents with a common Adama schema. |
| role | String | Each developer has a role to a document. |
| created | String | When the item was created. |
| enabled | Boolean | Is the item in question enabled. |
| storage-bytes | Long | The storage used. |

## Method: PushRegister (JS)
Register a device for push notifications

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| subscription | yes | ObjectNode | A push subscription which is an abstract package for push notifications |
| device-info | yes | ObjectNode | Information about a device |


### Template
```js
connection.PushRegister(identity, domain, subscription, device-info, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: DomainMap
Map a domain to a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| certificate | no | String | A TLS/SSL Certificate encoded as json. |


This method simply returns void.

## Method: DomainClaimApex
Claim an apex domain to be used only by your account

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| claimed | Boolean | Has the apex domain been claimed and validated? |
| txt-token | String | The TXT field to introduce under the domain to prove ownership |

## Method: DomainRedirect
Map a domain to another domain

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| destination-domain | yes | String | A domain name to forward to |


This method simply returns void.

## Method: DomainConfigure
Configure a domain with internal guts that are considered secret.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| product-config | yes | ObjectNode | Product config for various native app and integrated features. |


This method simply returns void.

## Method: DomainReflect (JS)
Get a schema for the domain

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |


### Template
```js
connection.DomainReflect(identity, domain, {
  success: function(response) {
    // response.reflection
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| reflection | ObjectNode | Schema of a document. |

## Method: DomainMapDocument
Map a domain to a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| route | no | Boolean | A domain can route to the space or to a document's handler |
| certificate | no | String | A TLS/SSL Certificate encoded as json. |


This method simply returns void.

## Method: DomainList
List the domains for the given developer

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| domain | String | A domain name. |
| space | String | A space which is a collection of documents with a common Adama schema. |
| key | String | The key. |
| route | Boolean | Does the domain route GET to the document or the space. |
| forward | String | Does the domain have a forwarding address |

## Method: DomainListBySpace
List the domains for the given developer

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| domain | String | A domain name. |
| space | String | A space which is a collection of documents with a common Adama schema. |
| key | String | The key. |
| route | Boolean | Does the domain route GET to the document or the space. |
| forward | String | Does the domain have a forwarding address |

## Method: DomainGetVapidPublicKey (JS)
Get the public key for the VAPID

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |


### Template
```js
connection.DomainGetVapidPublicKey(identity, domain, {
  success: function(response) {
    // response.publicKey
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| public-key | String | A public key to decrypt a secret with key arrangement. |

## Method: DomainUnmap
Unmap a domain

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |


This method simply returns void.

## Method: DomainGet
Get the domain mapping

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| space | String | A space which is a collection of documents with a common Adama schema. |

## Method: DocumentDownloadArchive
Download a complete archive

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| base64-bytes | String | Bytes encoded in base64. |
| chunk-md5 | String | MD5 of a chunk |

## Method: DocumentListBackups
List snapshots for a document

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| backup-id | String | The id of a backup (encoded) |
| date | String | The date of a backup |
| seq | Integer | The sequencer for the item. |

## Method: DocumentDownloadBackup
Download a specific snapshot

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| backup-id | yes | String | The reason a backup was made |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| base64-bytes | String | Bytes encoded in base64. |
| chunk-md5 | String | MD5 of a chunk |

## Method: DocumentListPushTokens
List push tokens for a given agent within a document

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| domain | yes | String | A domain name. |
| agent | yes | String | Agent within a principal |



### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| id | Long | a long id |
| subscription-info | ObjectNode | Subscription information for a push subscriber. |
| device-info | ObjectNode | Device information for a push subscriber. |

## Method: DocumentAuthorization (JS)
Send an authorization request to the document

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
connection.DocumentAuthorization(space, key, message, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentAuthorizationDomain (JS)
Send an authorization request to a document via a domain

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| domain | yes | String | A domain name. |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
connection.DocumentAuthorizationDomain(domain, message, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentAuthorize (JS)
Authorize a username and password against a document.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| username | yes | String | The username for a document authorization |
| password | yes | String | The password for your account or a document |


### Template
```js
connection.DocumentAuthorize(space, key, username, password, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentAuthorizeDomain (JS)
Authorize a username and password against a document via a domain

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| domain | yes | String | A domain name. |
| username | yes | String | The username for a document authorization |
| password | yes | String | The password for your account or a document |


### Template
```js
connection.DocumentAuthorizeDomain(domain, username, password, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentAuthorizeWithReset (JS)
Authorize a username and password against a document, and set a new password

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| username | yes | String | The username for a document authorization |
| password | yes | String | The password for your account or a document |
| new_password | yes | String | The new password for your account or document |


### Template
```js
connection.DocumentAuthorizeWithReset(space, key, username, password, new_password, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentAuthorizeDomainWithReset (JS)
Authorize a username and password against a document, and set a new password

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| domain | yes | String | A domain name. |
| username | yes | String | The username for a document authorization |
| password | yes | String | The password for your account or a document |
| new_password | yes | String | The new password for your account or document |


### Template
```js
connection.DocumentAuthorizeDomainWithReset(domain, username, password, new_password, {
  success: function(response) {
    // response.identity
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| identity | String | A private token used to authenticate to Adama. |

## Method: DocumentCreate (JS)
Create a document.

The entropy allows the randomization of the document to be fixed at construction time.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| entropy | no | String | Each document has a random number generator. When 'entropy' is present, it will seed the random number             generate such that the randomness is now deterministic at the start. |
| arg | yes | ObjectNode | The parameter for a document's @construct event. |


### Template
```js
connection.DocumentCreate(identity, space, key, entropy, arg, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: DocumentDelete
Delete a document (invokes the @delete document policy).

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |


This method simply returns void.

## Method: DocumentList (JS)
List documents within a space which are after the given marker.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| marker | no | String | A key to skip ahead a listing.             When iterating, values will be returned that are after marker.             To paginate an entire list, pick the last key or name returned and use it as the next marker. |
| limit | no | Integer | Maximum number of items to return during a streaming list. |


### Template
```js
connection.DocumentList(identity, space, marker, limit, {
  next: function(payload) {
    // payload.key
    // payload.created
    // payload.updated
    // payload.seq
    // payload.lastBackup
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| key | String | The key. |
| created | String | When the item was created. |
| updated | String | When the item was last updated. |
| seq | Integer | The sequencer for the item. |
| last-backup | String | The time of the last internal backup. |

## Method: MessageDirectSend (JS)
Send a message to a document without a connection

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
connection.MessageDirectSend(identity, space, key, channel, message, {
  success: function(response) {
    // response.seq
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| seq | Integer | The sequencer for the item. |

## Method: MessageDirectSendOnce (JS)
Send a message to a document without a connection

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| dedupe | no | String | A key used to dedupe request such that at-most once processing is used. |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
connection.MessageDirectSendOnce(identity, space, key, dedupe, channel, message, {
  success: function(response) {
    // response.seq
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| seq | Integer | The sequencer for the item. |

## Method: ConnectionCreate (JS)
Create a connection to a document.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| viewer-state | no | ObjectNode | A connection to a document has a side-channel for passing information about the client's view into the evaluation of bubbles.             This allows for developers to implement real-time queries and pagination. |


### Template
```js
connection.ConnectionCreate(identity, space, key, viewer-state, {
  next: function(payload) {
    // payload.delta
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| delta | ObjectNode | A json delta representing a change of data. See the [delta format](/reference/deltas.md) for more information. |

## Method: ConnectionCreateViaDomain (JS)
Create a connection to a document via a domain name.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| viewer-state | no | ObjectNode | A connection to a document has a side-channel for passing information about the client's view into the evaluation of bubbles.             This allows for developers to implement real-time queries and pagination. |


### Template
```js
connection.ConnectionCreateViaDomain(identity, domain, viewer-state, {
  next: function(payload) {
    // payload.delta
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| delta | ObjectNode | A json delta representing a change of data. See the [delta format](/reference/deltas.md) for more information. |

## Method: ConnectionSend
Send a message to the document on the given channel.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| seq | Integer | The sequencer for the item. |

## Method: ConnectionPassword
Set the viewer's password to the document; requires their old password.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| username | yes | String | The username for a document authorization |
| password | yes | String | The password for your account or a document |
| new_password | yes | String | The new password for your account or document |


This method simply returns void.

## Method: ConnectionSendOnce
Send a message to the document on the given channel with a dedupe key such that sending happens at most once.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| dedupe | no | String | A key used to dedupe request such that at-most once processing is used. |
| message | yes | JsonNode | The object sent to a document which will be the parameter for a channel handler. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| seq | Integer | The sequencer for the item. |

## Method: ConnectionCanAttach
Ask whether the connection can have attachments attached.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| yes | Boolean | The result of a boolean question. |

## Method: ConnectionAttach
This is an internal API used only by Adama for multi-region support.

Start an upload for the given document with the given filename and content type.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| asset-id | yes | String | The id of an asset. |
| filename | yes | String | A filename is a nice description of the asset being uploaded. |
| content-type | yes | String | The MIME type like text/json or video/mp4. |
| size | yes | Long | The size of an attachment. |
| digest-md5 | yes | String | The MD5 of an attachment. |
| digest-sha384 | yes | String | The SHA384 of an attachment. |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| seq | Integer | The sequencer for the item. |

## Method: ConnectionUpdate
Update the viewer state of the document.

The viewer state is accessible to bubbles to provide view restriction and filtering.
For example, the viewer state is how a document can provide real-time search or pagination.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| viewer-state | no | ObjectNode | A connection to a document has a side-channel for passing information about the client's view into the evaluation of bubbles.             This allows for developers to implement real-time queries and pagination. |


This method simply returns void.

## Method: ConnectionEnd
Disconnect from the document.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |


This method simply returns void.

## Method: DocumentsHashPassword (JS)
For documents that want to hold secrets, then these secrets should not be stored plaintext.

This method provides the client the ability to hash a password for plain text transmission.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| password | yes | String | The password for your account or a document |


### Template
```js
connection.DocumentsHashPassword(password, {
  success: function(response) {
    // response.passwordHash
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| password-hash | String | The hash of a password. |

## Method: BillingConnectionCreate (JS)
Create a connection to the billing document of the given identity.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.BillingConnectionCreate(identity, {
  next: function(payload) {
    // payload.delta
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| delta | ObjectNode | A json delta representing a change of data. See the [delta format](/reference/deltas.md) for more information. |

## Method: AttachmentStart (JS)
Start an upload for the given document with the given filename and content type.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are lower case ASCII using the regex a-z[a-z0-9\-]* to validation with a minimum length of three characters. The space name must also not contain a '--' |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are lower case ASCII using the regex [a-z0-9\._\-]* for validation |
| filename | yes | String | A filename is a nice description of the asset being uploaded. |
| content-type | yes | String | The MIME type like text/json or video/mp4. |


### Template
```js
connection.AttachmentStart(identity, space, key, filename, content-type, {
  next: function(payload) {
    // payload.chunk_request_size
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| chunk_request_size | Integer | The attachment uploader is asking for a chunk size.             Using the WebSocket leverages a flow control based uploader such that contention on the WebSocket is minimized. |

## Method: AttachmentStartByDomain (JS)
Start an upload for the given document with the given filename and content type.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| domain | yes | String | A domain name. |
| filename | yes | String | A filename is a nice description of the asset being uploaded. |
| content-type | yes | String | The MIME type like text/json or video/mp4. |


### Template
```js
connection.AttachmentStartByDomain(identity, domain, filename, content-type, {
  next: function(payload) {
    // payload.chunk_request_size
  },
  complete: function() {
  },
  failure: function(reason) {
  }
});
```


### Streaming payload fields
| name | type | documentation |
| --- | --- | --- |
| chunk_request_size | Integer | The attachment uploader is asking for a chunk size.             Using the WebSocket leverages a flow control based uploader such that contention on the WebSocket is minimized. |

## Method: AttachmentAppend
Append a chunk with an MD5 to ensure data integrity.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| chunk-md5 | yes | String | A md5 hash of a chunk being uploaded.             This provides uploads with end-to-end data-integrity. |
| base64-bytes | yes | String | Bytes encoded in base64. |


This method simply returns void.

## Method: AttachmentFinish
Finishing uploading the attachment upload.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |



### Request response fields
| name | type | documentation |
| --- | --- | --- |
| asset-id | String | The id of an uploaded asset. |
