# Web processing

Your adama script is a webserver!
Each Adama document is an opinionated webserver supporting GET, PUT, OPTIONS, and DELETE methods.
These methods can return HTML, JSON, or XML.

Documents are addressable within a region via a URL of the form:
```
https://$region.adama-platform.com/$space/$key/$path
```

Where $region is the region to request the data from, $space is the adama's script name, $key is the document key, and the $path is then handed over to the specified document.

## GET

```adama
@web get / {
  return {
    html: "Oh, Hello there! this is the root document"
  };
}
```

## OPTIONS (Cors Preflight)

```adama
@web options / {
  return {
    cors: true
  };
}
```

## PUT (&amp; POST)
Adama normalizes url-encoded bodies into JSON objects, and converts POST into PUT.

```adama
message TwilioSMS {
  string From;
  string Body;
}

@web put /webhook (TwilioSMS sm) {
  return {
    xml: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Response><Message>Thank you for the data. NOM. NOM.</Message></Response>"
  };
}
```

## DELETE

```adama
@web delete / {
  return {
    json: {}
  };
}
```

## Query parameters
The ```@web``` handler has ```@parameters``` (a [special constant](./constants.md)) for accessing the query parameters as a [dynamic](./rich-types.md).


## Headers
The ```@web``` handler has ```@headers``` (a [special constant](./constants.md)) for accessing the headers parameters as a [map&lt;string,string&gt;](./map-reduce.md).

## Responding

The return value on a ```@web``` is a message that is compile-time interpreted with rules. This message controls the behavior of the web server

### The body

The returned message within a ```@web``` may contain at most one body field.

| field | value type | behavior |
| --- | --- | --- |
| json | message | the message is converted to JSON and sent to the client with content type 'application/json' |
| xml | string | the string is sent to the client with content type 'application/xml' |
| css | string | the string is sent to the client with content type 'text/css' |
| js | string | the string is sent to the client with content type 'text/javascript' |
| error | string | the string is sent to the client with content type 'text/error' |
| html | string | the string is sent to the client with the content type 'text/html' |
| asset | asset | the asset is downloaded and sent to the client with the appropriate content type |
| sign | string agent | the agent is is treated as a document agent and turned into a JWT token signed by Adama [(see auth)](/reference/auth.md)
| identity | string | yield a pre-signed identity |
| forward | url | perform a redirect using 302 (permanent) |
| redirect | url | perform a redirect using 301 (temporary) |


### Cross origin resource sharing

The returned message within a ```@web``` may set a 'cors' field to true.
This will allow the request to be visible to a browser.

```adama
@web get / {
  return {json:{}, cors: true}
}
```

### Caching

Adama supports caching using both internal and browser caching. This is achieved via the 'cache_ttl_seconds' field.

```adama
@web get / {
  return {json:{}, cache_ttl_seconds:60}
}
```