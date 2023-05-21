# Roadmap

This document is a living road map of the Adama Platform. As such, it contains the investment details for the entire vision and future products.

## Developer relations &amp; adoption

| project                         | IP | milestones/description                                                                                                                                                                                                         |
|---------------------------------|----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| vs code extension               |    | (1.) Syntax highlighting, (2.) Language server protocol (LSP) - local, (3) LSP - cloud, (4) new ".adama.deploy" file to have 1-click shipping                                                                                  |
| sublime extension               |    | Since sublime is so fast, try to get core Adama language support for syntax highlighting                                                                                                                                       |                                                                                                     
| improve command line experience | X  | (1) Create a new "micro-language" for defining the CLI api to leverage code generation, (2) use language to create new execution framework, (3) shell prediction and completion, (4) address issues #130, #132, #133, and #134 |
| Android client                  |    | (1) Write a simplified web-socket interface, (2) implement interface with OkHttp, (3) update apikit code generator to produce a SDK talking to the web socket interface.                                                       |
| bootstrap                       |    | Build a tool to bootstrap an RxHTML application from an Adama file                                                                                                                                                             |
| integrate-linter                |    | integrate the linter to detect issues prior launch; address #62                                                                                                                                                                |
| lib-react                       |    | library to use Adama with React                                                                                                                                                                                                |
| lib-vue                         |    | library to use Adama with Vue                                                                                                                                                                                                  |
| lib-svelte                      |    | library to use Adama with svelte                                                                                                                                                                                               |
| js-client-retry                 |    | Individual retries per document                                                                                                                                                                                                |

## Web management
| project         | IP | milestones/description                 |
|-----------------|----|----------------------------------------|
| render-plan     |    | Render and explain the deployment plan |
| better-debugger |    | The debugger sucks                     |
| support fbauth  |    |                                        |

## Contributer Experience

| project                             | IP | milestones/description                                                                                            |
|-------------------------------------|----|-------------------------------------------------------------------------------------------------------------------|
| shell script love                   |    | Improve the build experience outside of Ubuntu                                                                    |
| test MacOS                          |    | Work through issues with unit tests on MacOS and any productivity issues with the python build script             |
| local mode                          |    | Adama should be able to run locally with a special version of Adama just for applications and local development   |
| faster unit tests                   |    | Improve the testing to not leverage shared resources (stdout, *cough*) such that testing can be made parallel     |
| write documentation about structure |    | Write a document to outlining the high level mono-repo structure                                                  |

## Language

| project                | IP | milestones/description                                                                                                                                              |
|------------------------|----|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| index-tensor           |    | Tables should be indexable based on integer tuples. Beyond efficiency, language extensions can help work with tables in a more natural array style (think 2D grids) |
| index-graph            |    | Tables should be able to become hyper graphs for efficient navigation between records using a graph where edges can be annotated (this maps)                        |
| math-matrix            |    | The type system and math library should come with vectors and matrices out of the box                                                                               |
| xml support            |    | Convert messages to XML                                                                                                                                             |
| rxhtml-static          |    | Embed rxhtml into compile process                                                                                                                                   |
| rxhtml-dynamic         |    | Embed rxhtml as a first class language feature                                                                                                                      |
| metrics emit id;       |    | The language should have first class support for metrics (counters, inflight, distributions)                                                                        |
| auto-convert-msg       |    | the binding of messages can be imprecise, need to simplify and automate @convert primarily for services                                                             |
| bubble + privacy       |    | Add a way to annotate a bubble with a privacy policy to simplify a privacy                                                                                          |
| privacy-policy caching |    | instead of making privacy policies executable every single time, cache them by person and invalidate on data changes                                                |
 
## Documentation

| project          | IP | description                                                                                |
|------------------|----|--------------------------------------------------------------------------------------------|
| client-walk      |    | A detailed walkthough about how to use the client library, the expectations, and core APIs |
| improve overview |    | Make the overview easier to understand, more details, etc                                  |
| detailed tour    |    | Convert the video to an online document                                                    |
| cheat-sheet      |    | document the vital aspects of the language and provide copy-pasta examples                 |
| micro-examples   |    | a mini stack-overflow of sorts                                                             |
| tutorial-app     |    | walk through the basics of building an Adama with just Adama and RxHTML                    |
| tutorial-twilio  |    | build a twilio bot as an example with details on calling services                          |
| tutorial-web     |    | a HOWTO host a static website with Adama                                                   |
| tutorial-domain  |    | a HOWTO use Adama's domain hosting support                                                 |
| zero-hero        |    | a breakdown of using the bootstrap tooling to build a complete app                         |

## Infrastructure - Protocols
| project   | IP | milestones/description                                                                                    |
|-----------|----|-----------------------------------------------------------------------------------------------------------|
| mqtt      |    | (1) Write design document how to adapt Adama to MQTT, (2) Build it with TLS, (3) Built it with plain-text |
| sse       |    | (1) Write design document how to adapt Adama to Server-Sent Events, (2) Build it with TLS                 |                             
| web async |    | Allow all web operations operations to become asynchronous                                                |

## Infrastructure - Enterprise data
| project        | IP | milestones/description                                                                                                                                                                                                                 |
|----------------|----|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| smaller deltas |    | (1) Define a log format that leverages binary serialization and compacts field definitions, (2) convert JSON deltas to binary in the logger to measure impact (throughput and latency), (3) leverage format upstream to minimize future network |
| healing log    |    | Implement a log data structure that can heal (anti entropy) across machines using Adama's network stack                                                                                                                                |
| raft           |    | Implement raft leader election and log append using Adama's network stack                                                                                                                                                              |
| control plane  |    | (1) Manual definition of raft shards definitions, (2) automatic machine management                                                                                                                                                     |

## Infrastructure - Multi-region &amp; massive scale
| project         | IP  | milestones                                                                                          |
|-----------------|-----|-----------------------------------------------------------------------------------------------------|
| diagram         |     | diagram the usage of the database in the adama service                                              |
| billing         |     | have billing route partial metering records to billing document ( and globalize )                   |
| proxy-mode      |     | proxy the WS API from region A to region B (or global important services )                          |
| spacial-homing  |     | globalizing biases to regions, some spaces may be regional so make their index local to that region |
| remote-finder   |     | extend WS API to implement a Finder for region A to do core tasks (or globalize)                    |
| finder in adama |     | Turn core service into a finder cache for web tier                                                  |
| region-isolate  |     | Allow regions to have storage for local documents                                                   |
| capacity-global |     | globalize capacity management                                                                       |
| test-heat-cap   |     | validate when an adama host heats up that traffic sheds                                             | 
| test-cold-cap   |     | validate when an adama host cools off that traffic returns for density                              | 

## Infrastructure - Core Service
| project                 | IP | milestones/description                                                                                                                                                                                                                                                                                             |
|-------------------------|----|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| env-bootstrap           |    | automatic the memory and other JVM args                                                                                                                                                                                                                                                                            |
| third-party replication |    | the language should allow specification of a endpoint to replace a piece of data to on data changes. This requires maintaining a local copy in the document along with a state machine about status. The tricky bit requires a delete notification. There is also the need to load every document on a deployment. |
| replication-search      |    | provide a "between document" search using replication tech                                                                                                                                                                                                                                                         |
| replication-graph       |    | similar to search, replicate part of the document into a graph database                                                                                                                                                                                                                                            |
| metrics                 |    | documents should able to emit metrics                                                                                                                                                                                                                                                                              |
| fix-keys                |    | document keys are stored with both private and public keys, and this is really bad; we should only store the public key and version the keys along with an expiry                                                                                                                                                  |
| op-query-engine         |    | a tool to debug the status of a document live                                                                                                                                                                                                                                                                      |
| portlets                |    | maybe part of replication, but a subdocument that can emit messages that are independent subscriptions (for SSE/MQTT) and for Adama to consume                                                                                                                                                                     |
| adama-actor             |    | implement Adama as a special first class service                                                                                                                                                                                                                                                                   |
| twilio-service          |    | implement twilio as a first party service                                                                                                                                                                                                                                                                          |
| stribe-service          | X  | implement stripe as a first party service                                                                                                                                                                                                                                                                          |
| BUG: doc-ids            |    | need to make the relationship between document id and key/space ironclad on adama service; it's possible to resurrect old data due to id resurrection                                                                                                                                                              |

## Infrastructure - Web

| project                 | IP | milestones/description                                                                     |
|-------------------------|----|--------------------------------------------------------------------------------------------|
| web-async put           | X  | allow PUTs to contain async calls                                                          |
| web-async delete        |    | allow DELETEs to contain async calls                                                       |
| web-async get           |    | allow GETs to contain async calls                                                          |
| request caching         |    | respect the cache_ttl_ms                                                                   |
| asset transforms        |    | implement some basic asset transforms                                                      |
| web-abort put/delete    |    | web calls that write should support abort                                                  |
| @context                |    | ensure web operations can access context                                                   |
| web-metrics             |    | add an API for the client to emit metrics                                                  |
| add auth for web        |    | the principal for web* is currently @no_one; it should be a valid user                     |
| build delta accumulator |    | slow clients may be get overwhelmed, the edge should buffer deltas and merge them together |

## Infrastructure - Overlord
| project              | IP | milestones/description                                                                                                    |
|----------------------|----|---------------------------------------------------------------------------------------------------------------------------|
| canary               |    | for testing the service health and correctness; overlord should maintain a constant state of various high-value API calls |
| operationalize-super |    | the "super" service needs a secure environment                                                                            |
| ui for query         |    | dynamic queries                                                                                                           |
| billing-send         |    | Simplify the billing engine and remove the overlord need                                                                  |
| ntp                  |    | look into time sync                                                                                                       |

## RxHTML
| project       | IP | milestones/description                                                                         |
|---------------|----|------------------------------------------------------------------------------------------------|
| headwindcss   |    | Port tailwindcss to Java for vertical control                                                  |
| components    |    | Bring clarity for single javascript extentions for new controls                                |
| time          |    | Custom component for selecting a time of day (Blocked on *components* model)                   |
| date          |    | Custom component for selecting a date or a date range (Blocked on *components* model)          |
| color         |    | Custom component for selecting a color (Blocked on *components* model)                         |
| server-side   |    | Create a customized shell for each page such that server side rendering allows faster presence |
| convert-react |    | Convert the RxHTML forest into a complete React app                                            |
| gc            |    | figure out if there is still a bug with "rxhtml fire delete" isn't cleaning up pubsub          |
| remove-col    |    | remove the rxhtml column from the spaces column and move into document; #127                   |

## Roslin (RxImage)
| project           | IP | milestones/description                               |
|-------------------|----|------------------------------------------------------|
| design document   |    | Write a design document from Jeff's notes            | 
| runtime-android   |    | Implement a starting runtime for web using android   |
| gameboard demo(s) |    | Write a game board demo of various games             |
| runtime-web       |    | Implement runtime for web using rust                 |
| rxhtml-integ      |    | Integrate runtime-web into RxHTML as a new component | 

## RxApp

| project         | IP | milestones                                                                                               |
|-----------------|----|----------------------------------------------------------------------------------------------------------|
| design document |    | design a simple XML only way to build Android applications using reactive data binding similar to RxHTML |
