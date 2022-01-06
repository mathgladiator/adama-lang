# API Reference 

## Method: init/start
This establishes a developer machine via email verification. The expectation is that while the email is
being sent, the socket is held open for the developer to complete the operation by providing the generated
code.

The generated code is securely randomized and tied to the socket to provide a secure way to validate the
email on the other end.

Developer accounts are keyed off of email.

## Method: init/revoke-all
When the developer receives an email, this method is invoked to revoke all existing public keys.

This same code, having been validated via email, can then be used to generate a new key pair.

## Method: init/generate-identity
When the developer receives an email, this method is invoked to complete the hand-shake.

The server will generate a key-pair and send the secret to the client to stash within their config, and the
public key will be stored to validate future requests made by this developer machine.

A public key will be held onto for 30 days.

## Method: probe
This is useful to validate an identity without executing anything

## Method: authority/create


## Method: authority/set


## Method: authority/get


## Method: authority/list


## Method: authority/destroy


## Method: space/create


## Method: space/get


## Method: space/set


## Method: space/delete


## Method: space/set-role


## Method: space/reflect


## Method: space/list


## Method: document/create


## Method: document/list


## Method: connection/create


## Method: connection/send


## Method: connection/end


## Method: attachment/start


## Method: attachment/append


## Method: attachment/finish


