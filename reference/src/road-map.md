# Roadmap

This document is a living road map of the Adama Platform. As such, it contains the investment details for the entire vision and future products.

## Developer relations &amp; adoption
The current story for developers is "meh", so these items help improve and modernize the developer experience.

| project               | IP  | milestones/description                                                                                                                                                   |
|-----------------------|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| vs code extension     | X   | (1.) Syntax highlighting, (2.) Language server protocol (LSP) - local                                                                                                    |
| sublime extension     |     | Since sublime is so fast, try to get core Adama language support for syntax highlighting                                                                                 |                                                                                                     
| Android client        |     | (1) Write a simplified web-socket interface, (2) implement interface with OkHttp, (3) update apikit code generator to produce a SDK talking to the web socket interface. |
| iOS client            |     | (1) Write a simplified web-socket interface, (2) implement interface with ?, (3) update apikit code generator to produce a SDK talking to the web socket interface.      |
| capacitor.js template |     | A template to turn an RxHTML project into a mobile app with deployment pipeline                                                                                          |
| integrate-linter      |     | integrate the linter to detect issues prior launch; address #62                                                                                                          |
| lib-react             |     | library to use Adama with React                                                                                                                                          |
| lib-vue               |     | library to use Adama with Vue                                                                                                                                            |
| lib-svelte            |     | library to use Adama with svelte                                                                                                                                         |
| js-client-retry       | X   | Individual retries per document                                                                                                                                          |

## Documentation

| project              | IP | description                                                                                                   |
|----------------------|----|---------------------------------------------------------------------------------------------------------------|
| devbox               |    | Improve the local developer experience                                                                        |
| kickoff demos        | X  | See [https://asciinema.org/](https://asciinema.org/) for more information                                     |
| client-walk          |    | A detailed walkthough about how to use the client library, the expectations, and core APIs                    |
| improve overview     |    | Make the overview easier to understand, more details, etc                                                     |
| cheat-sheet          |    | document the vital aspects of the language and provide copy-pasta examples                                    |
| micro-examples       |    | a mini stack-overflow of sorts                                                                                |
| tutorial-app         |    | walk through the basics of building an Adama with just Adama and RxHTML                                       |
| tutorial-twilio      |    | build a twilio bot as an example with details on calling services                                             |
| tutorial-web         |    | a HOWTO host a static website with Adama                                                                      |
| tutorial-domain      |    | a HOWTO use Adama's domain hosting support                                                                    |
| zero-hero            |    | a breakdown of using the bootstrap tooling to build a complete app                                            |
| feature-complex      |    | Write about complex number support                                                                            |
| feature-maybe        |    | (1) write about how maybes work, (2) write about maybe field deref, (3) write about math and maybe            |
| feature-lists        |    | write more lists                                                                                              |
| feature-map          |    | write about map transforms                                                                                    |
| feature-dynamic      |    | write about dynamic types                                                                                     |
| feature-viewer       |    | write about @viewer                                                                                           |
| feature-context      |    | write about @context                                                                                          |
| feature-web          |    | write about @headers / @parameters                                                                            |
| map/reduce-love      | X  | reduce love along with maps                                                                                   |
| functions            |    | procedure, aborts, functions, methods                                                                         |
| enumeration/dispatch |    | talk about dispatch                                                                                           |
| feature-services     |    | talk about services and linkage to first party                                                                |
| feature-async        |    | talk about async await,decide,fetch, choose, and result                                                       |
| result type          |    | talk about the result type                                                                                    |
| feature-sm           |    | talk about the state machine, invoke, transition, transition-in                                               |
| web-put              |    | talk about the web processing                                                                                 |
| stdlib code-gen      |    | generate documentation from the stdlib (i.e. embed docs in java as annotations) (embed documentation in type) |
| document or kill rpc |    | The rpc helper is an interesting way of making channels, but it isn't used right now... kill or document      |

## Standard Library
| project         | IP | description                                                                                                                             |
|-----------------|----|-----------------------------------------------------------------------------------------------------------------------------------------|
| stats           |    | build out a statistics package that is decent and correct (median isn't good right now) (sum, average, median, product, count_non_zero) |
| to/from Base64  |    | convert a string to and from Base64 with maybe&lt;string&gt;                                                                            |
| substituteFirst |    | find the needle in a haystack and replace the first instance                                                                            |
| substituteLast  |    | find the needle in a haystack and replace the last instance                                                                             |
| substituteAll   |    | find the needle in a haystack and replace the every instance                                                                            |
| substituteNth   |    | find the needle in a haystack and replace the n'th instance                                                                             |
| format          |    | need var_args (just use a string[])  support, but the idea is to allow efficient string construction from many known finite pieces      |
| split /w limit  |    | only split a certain degree                                                                                                             |
| find            |    | find a string within a string                                                                                                           |
| findAll         |    | find a string within a string and produce a list if indicies                                                                            |
| proper          |    | take a string, split on spaces, normalize white space, turn every string into a camel case word, join together                          |
| initials        |    | take a string, split on spaces, normalize white space, turn every string into a concat of just first letters capitalized                |
| convex hull     |    | a 2D version at first                                                                                                                   |                                                                                                             |
| matrix math     |    | inverse, multiply, etc...                                                                                                               |
| exact-math      |    | math that doesn't overflow, and if it does, empty maybe                                                                                 |
| random+         |    | gaussian random + other statisitical random functions                                                                                   |
| even/odd        |    | simple and fun functions                                                                                                                |
| factorial       |    |                                                                                                                                         |
| gamma factorial |    | https://en.wikipedia.org/wiki/Gamma_function                                                                                            |


## Web management
The [online web portal](https://ide.adama-platform.com) needs a lot of work to be useful.
The web portal should be upgraded to provide a manager view along with tools to get started along with steps to make progress.

| project         | IP | milestones/description                                                          |
|-----------------|----|---------------------------------------------------------------------------------|
| render-plan     |    | Render and explain the deployment plan                                          |
| render-routes   |    | Render and explain the routing including both rxhtml and web instructions       |
| better-debugger | X  | The debugger sucks                                                              |
| support fbauth  |    |                                                                                 |
| metrics         |    | A metrics explorer                                                              |
| service calls   |    | A way to explore which documents are making which service calls and the results |

## Contributer Experience
If your name isn't Jeff, then the current environment is not great.

| project                             | IP  | milestones/description                                                                                          |
|-------------------------------------|-----|-----------------------------------------------------------------------------------------------------------------|
| shell script love                   |     | Improve the build experience outside of Ubuntu                                                                  |
| test MacOS                          | X   | Work through issues with unit tests on MacOS and any productivity issues with the python build script           |
| test Windows                        | X   | Work through issues with unit tests on Windows and any productivity issues with the python build script         |
| local mode                          | X   | Adama should be able to run locally with a special version of Adama just for applications and local development |
| faster unit tests                   |     | Improve the testing to not leverage shared resources (stdout, *cough*) such that testing can be made parallel   |
| write documentation about structure |     | Write a document to outlining the high level mono-repo structure                                                |

## Performance/Capacity Issues

| project                | IP | milestones/description                                                                                              |
|------------------------|----|---------------------------------------------------------------------------------------------------------------------|
| bubble view filtering  |    | Bubbles should recompute only with related viewer changes happen rather than the viewer changed                     |
| compressed tables      |    | for tables that don't have deep subscriptions, let's compact records                                                |
| table-paging-{disk/db} |    | for tables that don't have deep subscription and stale data, let's introduce an offload database for caching        |
| java-compiler-service  |    | instead of every adama instance compiling java, let's cache some byte code and maybe offload to an isolated service |

## Language
The language is going to big with many features!

| project                | IP  | milestones/description                                                                                                                                              |
|------------------------|-----|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| index-tensor           |     | Tables should be indexable based on integer tuples. Beyond efficiency, language extensions can help work with tables in a more natural array style (think 2D grids) |
| index-graph            |     | Tables should be able to become hyper graphs for efficient navigation between records using a graph where edges can be annotated (this maps)                        |
| full-text-naive        | X   | string =? string is a full text operator that is naive                                                                                                              |
| full-text-index        |     | introduce full indexing where records describe a rich query language                                                                                                |
| dynamic-order          |     | introduce a special command language for runtime ordering of lists                                                                                                  |
| dynamic-query          |     | introduce a special language for queries to be dynamic                                                                                                              |
| math-matrix            |     | The type system and math library should come with vectors and matrices out of the box                                                                               |
| 2d-primitives          |     | circle, rectangle                                                                                                                                                   |
| xml support            |     | Convert messages to XML                                                                                                                                             |
| metrics emit $num;     | X   | The language should have first class support for metrics (counters, inflight, distributions)                                                                        |
| metrics emit $string;  |     | The language should support emitting tags with metrics to help shred and classifiy metrics                                                                          |
| auto-convert-msg       |     | the binding of messages can be imprecise, need to simplify and automate @convert primarily for services                                                             |
| bubble + privacy       | X   | Add a way to annotate a bubble with a privacy policy to simplify privacy logic                                                                                      |
| privacy-policy caching | X   | instead of making privacy policies executable every single time, cache them by person and invalidate on data changes                                                |
| table-protocol         |     | introduce a way to expose a table protocol for reading and writing tables via a data-grid component                                                                 |
| sum/bag types          |     | a sum type is going to be a special type of message                                                                                                                 |
| auto-convert           |     | auto convert messages to dynamic                                                                                                                                    |
| normalize messages     |     | messages of the same structure should auto convert                                                                                                                  |
| proper-lambdas         | X   | I should be able to accept functions as argruments to other functions                                                                                               |
| extension methods      |     | Extend every message/record based on a structural pattern                                                                                                           |
| message filtering      |     | Apply rules for parsing messages like "trim/lowercase" to help ensure only valid data enters system                                                                 |
| message aborts         |     | Apply simple rules to reject messages that are invalid                                                                                                              |
| decimal type           |     | for arbitray rationals                                                                                                                                              |

### Notes on bag type

```adama

enum E {Aa, Ab, B, C}
bag<E> B {
  A* {
    int x;
  }
  B {
    int x;
    int y;
  }
  C { }
}
```

The bag creates a record with the union of all fields along with various along with a virtual type for each instance which maps to the appropriate subset.
We can derefence it via a match

```adama
B my_bag;
#sm {
  match B as o {
    A* {
      o.x = 123;
    }
    B {
      o.x = 42;
      o.y = 7;
    }
    C {
    }
  }
}
```

Implicitly a bag has a type of the enumeration, and ingestion would be the way to set the type along with parameters.

```adama
my_bag <- {type: E::Aa, x: 42};
```

This will typecheck that the bag has everything needed and will clear other fields out. This is all sketch work, but the key design goal is to provide some degree of compact representation of data based on the kind of a thng.


## Infrastructure - Protocols
For integration across different ecosystems, there are more protocols to bridge gaps.

| project   | IP  | milestones/description                                                                                    |
|-----------|-----|-----------------------------------------------------------------------------------------------------------|
| mqtt      | X   | (1) Write design document how to adapt Adama to MQTT, (2) Build it with TLS, (3) Built it with plain-text |
| sse       |     | (1) Write design document how to adapt Adama to Server-Sent Events, (2) Build it with TLS                 |                             

## Infrastructure - Enterprise data
| project        | IP  | milestones/description                                                                                                                                                                                                                          |
|----------------|-----|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| smaller deltas |     | (1) Define a log format that leverages binary serialization and compacts field definitions, (2) convert JSON deltas to binary in the logger to measure impact (throughput and latency), (3) leverage format upstream to minimize future network |
| healing log    |     | Implement a log data structure that can heal (anti entropy) across machines using Adama's network stack                                                                                                                                         |
| raft           |     | Implement raft leader election and log append using Adama's network stack                                                                                                                                                                       |
| control plane  |     | (1) Manual definition of raft shards definitions, (2) automatic machine management                                                                                                                                                              |

## Infrastructure - Multi-region &amp; massive scale
At some point, Adama is going to be at the edge with hundreds of nodes across the world.

| project         | IP | milestones                                                                                                                               |
|-----------------|----|------------------------------------------------------------------------------------------------------------------------------------------|
| diagram         |    | diagram the usage of the database in the adama service                                                                                   |
| billing         | X  | have billing route partial metering records to billing document ( and globalize )                                                        |
| proxy-mode      | X  | proxy the WS API from region A to region B (or global important services )                                                               |
| remote-finder   | X  | extend WS API to implement a Finder for region A to do core tasks (or globalize)                                                         |
| finder in adama | X  | Turn core service into a finder cache for web tier                                                                                       |
| region-isolate  |    | Allow regions to have storage for local documents                                                                                        |
| capacity-global |    | globalize capacity management                                                                                                            |
| test-heat-cap   |    | validate when an adama host heats up that traffic sheds                                                                                  | 
| test-cold-cap   |    | validate when an adama host cools off that traffic returns for density                                                                   |
| cap-config      |    | make high/low vectors dynamic configurable                                                                                               |
| ro-replica      |    | (1) introduce new observe command which is a read-only version of connect, (2) have web routes go to an in-region replica                |
| reconcile       | X  | every adama host should be lazy with unknown spaces and also reconcile capacity if it should redeploy (due to missed deployment message) | 

## Infrastructure - Core Service
Adama is a service.

| project                  | IP | milestones/description                                                                                                                                                                                                                                                                                             |
|--------------------------|----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| env-bootstrap            |    | automatic the memory and other JVM args                                                                                                                                                                                                                                                                            |
| third-party replication  | X  | the language should allow specification of a endpoint to replace a piece of data to on data changes. This requires maintaining a local copy in the document along with a state machine about status. The tricky bit requires a delete notification. There is also the need to load every document on a deployment. |
| replication-search       |    | provide a "between document" search using replication tech                                                                                                                                                                                                                                                         |
| replication-graph        |    | similar to search, replicate part of the document into a graph database                                                                                                                                                                                                                                            |
| metrics                  | X  | documents should able to emit metrics                                                                                                                                                                                                                                                                              |
| fix-keys                 | X  | document keys are stored with both private and public keys, and this is really bad; we should only store the public key and version the keys along with an expiry (core work done, need to remove old code)                                                                                                        |
| op-query-engine          | X  | a tool to debug the status of a document live                                                                                                                                                                                                                                                                      |
| portlets                 |    | maybe part of replication, but a subdocument that can emit messages that are independent subscriptions (for SSE/MQTT) and for Adama to consume                                                                                                                                                                     |
| adama-actor              | X  | implement Adama as a special first class service                                                                                                                                                                                                                                                                   |
| twilio-service           |    | implement twilio as a first party service                                                                                                                                                                                                                                                                          |
| stripe-service           | X  | implement stripe as a first party service                                                                                                                                                                                                                                                                          |
| discord-service          |    | implement discord as a first party service                                                                                                                                                                                                                                                                         |
| slack-service            |    | implement slack as a first party service                                                                                                                                                                                                                                                                           |
| http-service             |    | study postman and design a generic HTTP service for third party integration                                                                                                                                                                                                                                        |
| results-stream           |    | figure out how to ensure deliveries can overwrite prior entries                                                                                                                                                                                                                                                    |
| portlets - passive net   |    | if we express the desigre for a result to hold the stream results for a foreign document's portlet then this creates a graph such that documents need to be reloaded. that is, we need a persistent subscription                                                                                                   |
| firebase - notifications |    | we should be able to send notifications                                                                                                                                                                                                                                                                            |
| document filter          | X  | instead of relying on privacy and wasting compute, a viewer should be able to opt into which fields to view.                                                                                                                                                                                                       |

## Infrastructure - Web
Adama is a web host provider of sorts!

| project                 | IP | milestones/description                                                                     |
|-------------------------|----|--------------------------------------------------------------------------------------------|
| web-async delete        | X  | allow DELETEs to contain async calls                                                       |
| web-async get           | X  | allow GETs to contain async calls                                                          |
| request caching         | X  | respect the cache_ttl_ms                                                                   |
| doc auth - expiry       |    | infer cache_ttl_ms as an expiry for doc auth                                               | 
| asset transforms        |    | implement some basic asset transforms                                                      |
| web-abort put/delete    | X  | web calls that write should support abort                                                  |
| @context                | X  | ensure web operations can access context                                                   |
| web-metrics             |    | add an API for the client to emit metrics                                                  |
| add auth for web        |    | the principal for web* is currently @no_one; it should be a valid user                     |
| build delta accumulator |    | slow clients may be get overwhelmed, the edge should buffer deltas and merge them together |


## Infrastructure - Overlord
Overlord is how the fleet is managed and aggregator.

| project              | IP  | milestones/description                                                                                                    |
|----------------------|-----|---------------------------------------------------------------------------------------------------------------------------|
| canary               |     | for testing the service health and correctness; overlord should maintain a constant state of various high-value API calls |
| operationalize-super |     | the "super" service needs a secure environment                                                                            |
| ui for query         |     | dynamic queries                                                                                                           |
| billing-send         | X   | Simplify the billing engine and remove the overlord need                                                                  |
| ntp                  |     | look into time sync                                                                                                       |

## RxHTML
RxHTML is a small set of extensions to allow HTML to be dynamic on the fly. The spiritual question is "what minimal number of things does HTML need to build apps?"

| project       | IP  | milestones/description                                                                         |
|---------------|-----|------------------------------------------------------------------------------------------------|
| headwindcss   |     | Port tailwindcss to Java for vertical control                                                  |
| components    | X   | Bring clarity for single javascript extentions for new controls                                |
| time          | X   | Custom component for selecting a time of day (Blocked on *components* model)                   |
| date          | X   | Custom component for selecting a date or a date range (Blocked on *components* model)          |
| color         | X   | Custom component for selecting a color (Blocked on *components* model)                         |
| graph         |     | Custom component with rich config to visualize graphs                                          |
| server-side   |     | Create a customized shell for each page such that server side rendering allows faster presence |
| convert-react |     | Convert the RxHTML forest into a complete React app                                            |
| gc            |     | figure out if there is still a bug with "rxhtml fire delete" isn't cleaning up pubsub          |
| remove-col    |     | remove the rxhtml column from the spaces column and move into document; #127                   |
| auto-ids      |     | A big pain point in HTML is the pair bonding of label to input via id.                         |
| typechecker   |     | A tool to validate an RxHTML forest doesn't have issues                                        |
| ai-tester     |     | A browser extension to walk and validate a site is working and collecting tracking tokens      |

## RxHTML - Mobile

| project | IP | milestones/description |
|---|---|---|
| capacitor | X | should be able to convert an RxHTML SPA to a mobile app |
| notification support| |

## Roslin (RxImage)
The vision is for a runtime just for games. [See this post for more information](https://www.adama-platform.com/2022/12/02/case-new-game-engine.html)

| project           | IP  | milestones/description                               |
|-------------------|-----|------------------------------------------------------|
| design document   |     | Write a design document from Jeff's notes            | 
| runtime-android   |     | Implement a starting runtime for web using android   |
| gameboard demo(s) |     | Write a game board demo of various games             |
| runtime-web       |     | Implement runtime for web using rust                 |
| rxhtml-integ      |     | Integrate runtime-web into RxHTML as a new component | 

## Saul (RxApp)
Similar to RxHTML, the question is how to build a minimal runtime for iOS and Android applications. Tactically speaking, we can use RxHTML with [capacitor](https://capacitorjs.com/).

| project         | IP  | milestones                                                                                               |
|-----------------|-----|----------------------------------------------------------------------------------------------------------|
| design document |     | design a simple XML only way to build Android applications using reactive data binding similar to RxHTML |
