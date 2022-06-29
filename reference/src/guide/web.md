# Web processing (Draft)

Your adama script is a webserver!

## Get

```adama
@web get / {
  return {
    html: "Oh, Hello there! this is the root document"
  };
}
```

## Post/Put
Adama normalizes url-encoded bodies into JSON.

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