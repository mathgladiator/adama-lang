# API Reference 

## Method: init/setup-account
This initiates developer machine via email verification.

## Method: init/convert-google-user
The converts and validates a google token into an Adama token

## Method: init/complete-account
This establishes a developer machine via email verification.

Copy the code from the email into this request.

The server will generate a key-pair and send the secret to the client to stash within their config, and the
public key will be stored to validate future requests made by this developer machine.

A public key will be held onto for 30 days.

## Method: account/set-password


## Method: account/login


## Method: probe
This is useful to validate an identity without executing anything

## Method: authority/create


## Method: authority/set


## Method: authority/get


## Method: authority/list


## Method: authority/destroy


## Method: space/create


## Method: space/generate-key


## Method: space/usage


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


## Method: connection/update


## Method: connection/end


## Method: configure/make-or-get-asset-key
Here, we ask if the connection if it has an asset key already.
If not, then it will generate one and send it along.
Otherwise, it will return the key bound to the connection.

## Method: attachment/start


## Method: attachment/append


## Method: attachment/finish


