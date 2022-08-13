# API Reference 
 Methods: 
[InitSetupAccount](#method-initsetupaccount), [InitConvertGoogleUser](#method-initconvertgoogleuser), [InitCompleteAccount](#method-initcompleteaccount), [AccountSetPassword](#method-accountsetpassword), [AccountLogin](#method-accountlogin), [Probe](#method-probe), [AuthorityCreate](#method-authoritycreate), [AuthoritySet](#method-authorityset), [AuthorityGet](#method-authorityget), [AuthorityList](#method-authoritylist), [AuthorityDestroy](#method-authoritydestroy), [SpaceCreate](#method-spacecreate), [SpaceGenerateKey](#method-spacegeneratekey), [SpaceUsage](#method-spaceusage), [SpaceGet](#method-spaceget), [SpaceSet](#method-spaceset), [SpaceDelete](#method-spacedelete), [SpaceSetRole](#method-spacesetrole), [SpaceReflect](#method-spacereflect), [SpaceList](#method-spacelist), [DocumentCreate](#method-documentcreate), [DocumentList](#method-documentlist), [ConnectionCreate](#method-connectioncreate), [ConnectionSend](#method-connectionsend), [ConnectionSendOnce](#method-connectionsendonce), [ConnectionCanAttach](#method-connectioncanattach), [ConnectionAttach](#method-connectionattach), [ConnectionUpdate](#method-connectionupdate), [ConnectionEnd](#method-connectionend), [ConfigureMakeOrGetAssetKey](#method-configuremakeorgetassetkey), [AttachmentStart](#method-attachmentstart), [AttachmentAppend](#method-attachmentappend), [AttachmentFinish](#method-attachmentfinish)

## Method: InitSetupAccount
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

## Method: InitConvertGoogleUser
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

## Method: InitCompleteAccount
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

## Method: AccountSetPassword
Set the password for an Adama developer.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| password | yes | String | The password for your account. |


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

## Method: AccountLogin
Sign an Adama developer in with an email and password pair.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| email | yes | String | The email of an Adama developer. |
| password | yes | String | The password for your account. |


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

## Method: Probe
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

## Method: AuthorityCreate
Create an authority. See [Authentication](/reference/auth.md) for more details.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |


### Template
```js
connection.AuthorityCreate(identity, {
  success: function(response) {
    // response.authority
  },
  failure: function(reason) {
  }
});
```


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


### Template
```js
connection.AuthoritySet(identity, authority, key-store, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: AuthorityGet
Get the public keystore for the authority.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| authority | yes | String | An authority is collection of users held together via a key store. |


### Template
```js
connection.AuthorityGet(identity, authority, {
  success: function(response) {
    // response.keystore
  },
  failure: function(reason) {
  }
});
```


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


### Template
```js
connection.AuthorityList(identity, {
  next: function(payload) {
    // payload.authority
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
| authority | String | An authority is collection of third party users authenticated via a public keystore. |

## Method: AuthorityDestroy
Destroy an authority.

This is exceptionally dangerous as it will break authentication for any users that have tokens based on that authority.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| authority | yes | String | An authority is collection of users held together via a key store. |


### Template
```js
connection.AuthorityDestroy(identity, authority, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: SpaceCreate
Create a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
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
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |


### Template
```js
connection.SpaceGenerateKey(identity, space, {
  success: function(response) {
    // response.keyId
    // response.publicKey
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| key-id | Integer | Unique id of the private-key used for a secret. |
| public-key | String | A public key to decrypt a secret with key arrangement. |

## Method: SpaceUsage
Get the most recent space usage in terms of billable hours.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| limit | no | Integer | Maximum number of items to return during a streaming list. |


### Template
```js
connection.SpaceUsage(identity, space, limit, {
  next: function(payload) {
    // payload.hour
    // payload.cpu
    // payload.memory
    // payload.connections
    // payload.documents
    // payload.messages
    // payload.storageBytes
    // payload.bandwidth
    // payload.firstPartyServiceCalls
    // payload.thirdPartyServiceCalls
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
| hour | Integer | The hour of billing. |
| cpu | Long | Cpu (in Adama ticks) used within the hour. |
| memory | Long | Memory (in bytes) used within the hour. |
| connections | Integer | p95 connections for the hour. |
| documents | Integer | p95 documents for the hour. |
| messages | Integer | Messages sent within the hour. |
| storage-bytes | Long | The storage used. |
| bandwidth | Long | Bytes used to transmit. |
| first-party-service-calls | Long | Number of services calls made (managed by platform). |
| third-party-service-calls | Long | Number of services calls made (managed by developers). |

## Method: SpaceGet
Get the deployment plan for a space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |


### Template
```js
connection.SpaceGet(identity, space, {
  success: function(response) {
    // response.plan
  },
  failure: function(reason) {
  }
});
```


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
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| plan | yes | ObjectNode | This 'plan' parameter contains multiple Adama scripts all gated on various rules.              These rules allow for a migration to happen slowly on your schedule.              Note: this value will validated such that the scripts are valid, compile, and will not have any major regressions             during role out. |


### Template
```js
connection.SpaceSet(identity, space, plan, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: SpaceDelete
Delete a space.

This requires no documents to be within the space, and this removes the space from use until garbage collection ensures no documents were created for that space after deletion.
A space may be reserved for 90 minutes until the system is absolutely sure no documents will leak.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |


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
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| email | yes | String | The email of an Adama developer. |
| role | yes | String | The role of a user may determine their capabilities to perform actions. |


### Template
```js
connection.SpaceSetRole(identity, space, email, role, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: SpaceReflect
Get a schema for the space.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are case insensitive using the regex [a-z0-9\.-_]* for validation |


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

## Method: SpaceList
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

## Method: DocumentCreate
Create a document.

The entropy allows the randomization of the document to be fixed at construction time.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are case insensitive using the regex [a-z0-9\.-_]* for validation |
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

## Method: DocumentList
List documents within a space which are after the given marker.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
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

## Method: ConnectionCreate
Create a connection to a document.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are case insensitive using the regex [a-z0-9\.-_]* for validation |
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

## Method: ConnectionSend
Send a message to the document on the given channel.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| message | yes | ObjectNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
stream.Send(channel, message, {
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

## Method: ConnectionSendOnce
Send a message to the document on the given channel with a dedupe key such that sending happens at most once.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| channel | yes | String | Each document has multiple channels available to send messages too. |
| dedupe | no | String | A key used to dedupe request such that at-most once processing is used. |
| message | yes | ObjectNode | The object sent to a document which will be the parameter for a channel handler. |


### Template
```js
stream.SendOnce(channel, dedupe, message, {
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

## Method: ConnectionCanAttach
Ask whether the connection can have attachments attached.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |


### Template
```js
stream.CanAttach({
  success: function(response) {
    // response.yes
  },
  failure: function(reason) {
  }
});
```


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


### Template
```js
stream.Attach(asset-id, filename, content-type, size, digest-md5, digest-sha384, {
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

## Method: ConnectionUpdate
Update the viewer state of the document.

The viewer state is accessible to bubbles to provide view restriction and filtering.
For example, the viewer state is how a document can provide real-time search or pagination.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| viewer-state | no | ObjectNode | A connection to a document has a side-channel for passing information about the client's view into the evaluation of bubbles.             This allows for developers to implement real-time queries and pagination. |


### Template
```js
stream.Update(viewer-state, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: ConnectionEnd
Disconnect from the document document.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |


### Template
```js
stream.End({
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: ConfigureMakeOrGetAssetKey
Here, we ask if the connection if it has an asset key already.
If not, then it will generate one and send it along.
Otherwise, it will return the key bound to the connection.

This is allows anyone to have access to assets which are not exposed directly via a web handler should they see the asset within their document view.
This method has no parameters.

### Template
```js
connection.ConfigureMakeOrGetAssetKey({
  success: function(response) {
    // response.assetKey
  },
  failure: function(reason) {
  }
});
```


### Request response fields
| name | type | documentation |
| --- | --- | --- |
| asset-key | String | A key used to connect the dots from the connection to assets to a browser.             This is a session-based encryption scheme to protect assets from leaking outside the browser. |

## Method: AttachmentStart
Start an upload for the given document with the given filename and content type.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| identity | yes | String | Identity is a token to authenticate a user. |
| space | yes | String | A 'space' is a collection of documents with the same schema and logic, and the 'space' parameter is used to             denote the name of that collection.              Spaces are case insensitive using the regex a-z[a-z0-9\.]* to validation. |
| key | yes | String | Within a space, documents are organized within a map and the 'key' parameter will uniquely identify             documents.              Keys are case insensitive using the regex [a-z0-9\.-_]* for validation |
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

## Method: AttachmentAppend
Append a chunk with an MD5 to ensure data integrity.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |
| chunk-md5 | yes | String | A md5 hash of a chunk being uploaded.             This provides uploads with end-to-end data-integrity. |
| base64-bytes | yes | String | Bytes encoded in base64. |


### Template
```js
stream.Append(chunk-md5, base64-bytes, {
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.

## Method: AttachmentFinish
Finishing uploading the attachment upload.

### Parameters
| name | required | type | documentation |
| --- | --- | --- | --- |


### Template
```js
stream.Finish({
  success: function() {
  },
  failure: function(reason) {
  }
});
```

This method simply returns void.
