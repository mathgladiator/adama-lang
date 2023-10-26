# Web Serving

Adama, at core, is a web server serving HTTP/HTTPS/WebSocket. This section is going to be about how requests are routed. Adama serves content and provides various ways of transacting with Adama documents. From a content servicing perspective, Adama serves up:
* [RxHTML shells](/rxhtml/ref.md)
* Spacial assets
* Document queries

## GET

Adama uses the HTTP Host header to route traffic. Based on the Host, behavior may change. By default, all documents are publically addressable via the production endpoint (like https://aws-us-east-2.adama-platform.com/). Requests against the production endpoint of the form:

```sh
https://aws-us-east-2.adama-platform.com/$space/$key/$uri
```

will load the document and then execute a web get against the document. For example, the given uri

```sh
https://aws-us-east-2.adama-platform.com/your_space/your_key/my_thing
```

will execute the @web get handler:

```adama
@web get /my_thing {
  return {html: "Hello World"};
}
```

Now, granted, this isn't a pretty URI, so Adama offers custom domains. In your domain register, if you point ```www.mydomain.com``` to the production endpoint via a CNAME, then you can map this domain to a space and/or document. In the CLI, this is available via:

```sh
java -jar ~/adama.jar domain map
```

This command requires ``--domain www.mydomain.com`` and ``--space your_space`` minimally. There is also ``--key your_key`` and ``--route false/true``. The route flag only applies to the GET verb as there is a conflict between the document and the space. Here, there is a bit of complexity, so let's bring in a diagram.

<img src="/i/web-custom-domain-flow-get.png" />

The common scenario is to map a domain to a document, see [multi-tenant products](/web/multi-tenant.md).

## POST/PUT/DELETE
Adama unifies and normalizes PUT/POST and always go to a document (as spaces only provide readonly serves and RxHTML has no write path).