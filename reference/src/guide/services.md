# Interacting with remote services

Adama supports third party services that allow the document to escape it's container and talk to the fullness of the world.

## Google SSO
A simple way to leverage google to provide single-sign on is to get a client token and then have the server convert that token to an email.

```adama
// this brings the service into the document
@link googlevalidator {}

message GoogleSignin {
  string token;
}

@web put /google (GoogleSignin gs) {
  // invoking the service method available
  if (googlevalidator.validate(@who, {token:gs.token}).await() as validated) {
    if((iterate _users where email == validated.email)[0] as user) {
      return {sign:"" + user.id};
    } else {
      return {error:"Not a valid user in the system"};
    }
  }
  return {error:"Failed to Sign in with Google", cors:true};
}
```

## Built-in services

TODO: Code generate this!