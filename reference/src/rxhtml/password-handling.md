# Password Handling

The field name "password" is considered sensitive and is never directly sent to the server. In general, it is not advised to use type="password" with any other name beyond "password" or "new_password" and the handful of [virtual password fields.](#virtual-password-fields)

RxHTML treats passwords (both with names of "new_password" or "password" along with an input type "password") as very restrictive and there are only two allowed ways of sending a password.

First, when sending a message to a channel, the passwords are hashed immediately such that the plain-text password is never seen by the data layer and thus never logged.
Second, when sending a message to the [@authorization handler](/reference/auth-doc-workflow.md), the "password" is striped from the message prior to sending to the @authorization handler while "new_password" is hashed immediately.

## Virtual Password fields

There are three "special" field names that may use type="password" and these are to provide common authorization features like confirming a password or setting a new password.

* confirm-password
* confirm-new_password

Both "confirm-password" and "confirm-new_password" are used within the UI to respectively validate the "password" and "new_password" fields.
These fields are stripped from the final message.
