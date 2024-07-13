# Replication

The design of replication is such that some message needs to be replicated to another service which is expressed via the sync
```adama
replication<$service:$replicationMethod> $statusVariable = $expression;
```

This signals that the given expression should be replicated to the intended service on the given method.
It is the responsibility of the service to define how replication is made idempotent as replication ought to happen every time the expression changes (with some operational knobs to limit and regulate replication, of course).
This feature requires careful design such that the third party service is kept in sync.

As an example service, consider a search service to provide cross-document indexing.
This service implementation can pull information from $expression along with the space and key name to define an idempotent key.
That is, whenever the $expression changes, we will need recompute the idempotent key.

```adama
idempotentKey = deriveIK($expression, $space, $key)
```

This key is then used against the service to ensure retries don't recreate new entries as we expect failures to happen for a multitude of reasons.
The service then has two primary async interfaces to implement: put($idempotentKey, $value) and delete($idempotentKey).
A tricky element at hand is handling idempotent key change, and the way to handle it is to issue a delete against the old $oldIdempotentKey prior to putting a new $newIdempotentKey

As such, this requires a persistent state machine which will embed within the document to keep track of the state. We will call this RxReplicationStatus.

We have to model the state machine such that:
* the key may change at any time
* we respect the other service may have variable (i.e. high latency)
* we only want one operation out at any time for any single replication
* failures may happen within and across hosts (i.e. a request is issued, machine failures, another request starts on another machine)

So, we will introduce a simple state machine with states that are persisted.

| state                        | description                                                                 |
|------------------------------|-----------------------------------------------------------------------------|
| Fresh                        | the state machine has been created and whether or not.                      |
| Sending[key, @time]          | replication is about to start at the given time.                            |
| Replicated[key, hash, @time] | replication finished for the given key using the hash of the value.         |
| Failed[key, hash, @time]     | replication failed for the given key using the given hash at the given time |

Since we intend to have a single action allowed at once, we also need a transient state

| state                   | description                                                           |
|-------------------------|-----------------------------------------------------------------------|
| Sending_Ready           | Sending[key, @time] was written, but no network action has been taken |
| Sending_Inflight[retry] | The current machine is executing. This exists to block new action     |

## Reconstruction








