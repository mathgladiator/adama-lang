# Document based Auth Workflow

The @authorization handler is fairly complex since hashing and security is handled by the platform rather than the document.
At core, the @authorization handler is a special channel that returns a complex dynamic object to configure the platform.
In the barest form, implementation starts by defining a message to interpret and the response then provides the associated agent along with a hash to check.
The platform will check the hash and all important security details are handled by the platform. 

```adama
message AuthPipeInvoke {
  string email;
}

@authorization (AuthPipeInvoke api) {
  // find the person
  if ((iterate _people where email == api.email)[0] as person) {
    // return the agent and the hash to check
    return {
      agent: "" + person.id,
      hash: person.password_hash,
    };
  }
  abort;
}
```

The methods to invoke this handler are either [document/authorization](/reference/api.html#method-documentauthorization-js) or [document/authorization-domain](/reference/api.html#method-documentauthorizedomain-js).
These both accept a JSON object under the field **message**. This **message** is converted to the associated message structure (in this case: AuthPipeInvoke).

The code then either either returns an structure containing the fields **hash** and **agent** OR aborts.
If the code aborts, then the authorization fails.
If the code returns an **agent** with **hash**, then the **hash** is checked against the provided password (see [password handling](/rxhtml/password-handling.md)) for clarity).
If the provided password satisfies the **hash**, then the authorization allowed and the identity is created using the **agent** field under the authority of 'doc/$space/$key'.

## read-only behavior

The @authorization handler is read-only and unable to mutate the document. We can turn around immediately to perform a write via the **channel** and **success** fields.
The **channel** field allows a successful mutation to be executed with the associated message in the **success** field.

This trampoline allows (1) password resets, (2) one time password cleanup, (3) metrics. Below is sample code of using both the **channel** and **success** fields with one time passwords and resets.

```adama
message AuthPipeInvoke {
  bool otp;
  int otp_id;
  string email;
  maybe<string> new_password;
}

record OneTimePassword {
  private int id;
  private int user_id;
  private string hash;
}

table<OneTimePassword> _otps;

@authorization (AuthPipeInvoke api) {
  if (api.otp) {
    if ((iterate _otps where id == api.otp_id)[0] as otp) {
      if (api.new_password as new_password) {
        return {
          agent: "" + otp.user_id,
          hash: otp.hash,
          channel: "set_password_from_authpipe_otp",
          success: {new_password: new_password}
        };
      } else {
        return {
          agent: "" + otp.user_id,
          hash: otp.hash,
          channel: "post_authorization_clear_otp",
          success: {}
        };
      }
    }
  } else {
    if ((iterate _people where email == api.email)[0] as user) {
      if (api.new_password as new_password) {
        // set password at login
        return {
          agent: "" + user.id,
          hash: user.password_hash,
          channel: "set_password_from_authpipe",
          success: {new_password: new_password}
        };
      } else {
        // just validate
        return {
          agent: "" + user.id,
          hash: user.password_hash,
        };
      }
    }
  }
  abort;
}

message AuthPipeSetPassword {
  string new_password;
}

channel set_password_from_authpipe(AuthPipeSetPassword apsp) {
  if ((iterate _people where who == @who)[0] as user) {
    user.password_hash = apsp.new_password;
  }
}

channel set_password_from_authpipe_otp(AuthPipeSetPassword apsp) {
  if ((iterate _people where who == @who)[0] as user) {
    user.password_hash = apsp.new_password;
    (iterate _otps where user_id == user.id).delete();
  }
}

channel post_authorization_clear_otp(Empty e) {
  if ((iterate _users where who == @who)[0] as user) {
    (iterate _otps where user_id == user.id).delete();
  }
}

```