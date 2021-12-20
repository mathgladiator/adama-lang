# API Reference 

## Method: init/start
This establishes a developer machine via email verification. The expectation is that while the email is being sent, the socket is held open for the developer to complete the operation by providing the generated code.

The generated code is securely randomized and tied to the socket to provide a secure way to validate the email on the other end.

Developer accounts are keyed off of email.

## Method: init/revoke-all
When the developer receives an email, this method is invoked to revoke all existing public keys.

This same code, having been validated via email, can then be used to generate a new key pair.

## Method: init/generate-new-key-pair
When the developer receives an email, this method is invoked to complete the hand-shake.

The server will generate a key-pair and send the secret to the client to stash within their config, and the public key will be stored to validate future requests made by this developer machine.

A public key will be held onto for 30 days.

## Method: billing/add


## Method: billing/list


## Method: billing/get


## Method: space/billing/set
Each space is bill seperately.

## Method: authority/claim


## Method: authority/transfer-ownership


## Method: authority/list


## Method: authority/keys/add


## Method: authority/keys/list


## Method: authority/keys/remove


## Method: space/create


## Method: space/get


## Method: space/update


## Method: space/delete


## Method: space/role/set


## Method: space/owner/set


## Method: space/reflect


## Method: space/list


## Method: document/create


## Method: document/list


## Method: connection/create


## Method: connection/send


## Method: connection/end


## Method: web-hook/add


## Method: web-hook/list


## Method: web-hook/remove


## Method: attachment/start


## Method: attachment/append


## Method: attachment/finish


