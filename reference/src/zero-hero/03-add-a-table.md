# Add a table

Before we add a table, register your email and password to login. Registration will create an identity unique to the document, and you can [read more about authentication](/reference/auth.md#document)! Once you have registered, we can now connect to the document. Since the webapp demo doesn't have a forgot password feature, please don't lose that password!

So, now, let's add a table. However, this is putting the cart before the horse and we need a mission!
We are going to create a contact support tool which allows users to register for and ask questions about their service.
Since we already have the user's email, we primarily need to record a message and some kind of response from the service provider when it is provided.
We first create [record to collect](/guide/records.md).

```adama
record SupportTicket {
  public int id;
  public int user_id;
  public string request;
  public int responder_id;
  public bool responded;
  public string response;
}
```

With the **SupportTicket** in hand, we create a table to hold the records.

```adama
table<SupportTicket> _tickets;
```

The underscore (_) is not needed, but it has emerged as a useful convention because tables are always private. With the table, we now create a [message](/guide/messages.md) for a user to create a ticket.

```adama
message CreateTicket {
  string request;
}
```

And then we create a [channel](/guide/async.md) to accept the message from the user to write into the table.
We write this using two different approaches and then compare and contrast.
First, we simply ingest an [anonymous message](/guide/anonymous.md) with the data at hand.
This requires finding the user's id by looking up within the template's _users table by the principal against the sender of the message (denoted by ```@who```)

```adama
channel create_ticket_uhmmm(CreateTicket ct) {
  if( (iterate _users where_as x: x.who == @who)[0] as user) {
    _tickets <- {user_id:user.id, request:ct.request};
  }
}
```

While this works, this has the disadvantage that adding a new field to the record and message now requires updating create_ticket_1. Instead, we want to simply ingest the data immediately and then patch it.

```adama
channel create_ticket(CreateTicket ct) {
  if( (iterate _users where_as x: x.who == @who)[0] as user) {
    _tickets <- ct as ticket_id;
    if( (iterate _tickets where id == ticket_id)[0] as ticket) {
      ticket.user_id = user.id;
    } else {
      // impossible! but... in case
      abort;
    }
  }
}
```

This has the advantage that a field added to both the SupportTicket and CreateTicket types will flow because the ingestion ```<-``` operator is an effective merge of the right hand side into the left hand side.

We can test this by simply exposing all the tickets via a formula;

```adama
public formula tickets = iterate _tickets;
```

Once you save this (and there are no errors in the devbox), you can go to [http://localhost:8080/], sign in, and then click the little wifi icon in the bottom right.
This will bring up the debugger where you can use the form to inject data directly into the backend by calling a channel.
Have fun playing with the debugger.

Let's update the /product page by editing the frontend/initial.rx.html file and changing the /product page to

```html
    <page uri="/product">
        This is the product!
        <connection use-domain name="product">
            <ul rx:iterate="others">
                <li><lookup path="email" /></li>
            </ul>
            <table>
                <tbody rx:iterate="tickets">
                    <tr><td><lookup path="request" /></td></tr>
                </tbody>
            </table>
        </connection>
    </page>
```

This will iterate over the tickets and show the request in a table's column. We can use the debugger to populate this column as we are viewing the page!

**(Warning, at this point, this is specification work that hasn't been tested.)**

We can add a form to execute **create_ticket**, 

```html
    <page uri="/product">
        This is the product!
        <connection use-domain name="product">
            <ul rx:iterate="others">
                <li><lookup path="email" /></li>
            </ul>
            <table>
                <tbody rx:iterate="tickets">
                    <tr><td><lookup path="request" /></td></tr>
                </tbody>
            </table>
            <form rx:action="send:create_ticket">
                <input name="request"> <br />
                <button type="submit">Create Ticket</button>
            </form>
        </connection>
    </page>
```

At this point, we now have Read (via data binding) and Write (via forms) and all manner of applications are possible. Check out the reference for [RxHTML](/rxhtml/ref.md)