# Principals

The principal represents an agent on behalf of an authority. The default authority is 'adama' which represents Adama Developers authenticated via the [Adama Platform](https://www.adama-platform.com). The principal has a unique facet where only the platform is allowed to create them as they represent identity, and this identity can be used for access control and privacy.

Part of access control is also validating that a user is from the right place which is where the standard library provides some simple functions to check a few things.

# Type: principal

| Method | Description | Result type |
| --- | --- | --- |
| isAdamaDeveloper() | Returns whether the principal is an Adama Developer | bool |
| fromAuthority(string authority) | Returns whether the principal was derived from the given authority. See [authentication](./auth.md) for how to bring your own authentication. | bool | 
