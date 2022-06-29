# SMS Bot

## Back-end (Draft)

This is as draft example

```adama
@static {
  create {
    return true;
  }
}

public client owner;

@connected {
  return true;
}

@construct {
  owner = @who;
}

record Entry {
  public int id;
  public dynamic parameters;
  public map<string, string> headers;
  public string from;
  public string body;
}

table<Entry> _entries;

public formula entries = iterate _entries order by id desc;

message TwilioSMS {
  string From;
  string Body;
}

@web put /webhook (TwilioSMS sm) {
  _entries <- {
    from:sm.From,
    body:sm.Body,
    parameters:@parameters,
    headers:@headers
  };
  return {
    xml: "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Response><Message>Thank you for the data. NOM. NOM.</Message></Response>"
  };
}
```