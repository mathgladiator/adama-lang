# Roadmap

This document is a living road map of the Adama Platform. As such, it contains the investment details for the entire vision and future products.

## Developer relations &amp; adoption

| project                         | milestones/description                                                                                                                                                          |
|---------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| vs code extension               | (1.) Syntax highlighting, (2.) Language server protocol (LSP) - local, (3) LSP - cloud, (4) new ".adama.deploy" file to have 1-click shipping                                   |
| sublime extension               | Since sublime is so fast, try to get core Adama language support for syntax highlighting                                                                                        |                                                                                                     
| improve command line experience | (1) Create a new "micro-language" for defining the CLI api to leverage code generation, (2) use language to create new execution framework, (3) shell prediction and completion |
| Android client                  | (1) Write a simplified web-socket interface, (2) implement interface with OkHttp, (3) update apikit code generator to produce a SDK talking to the web socket interface.        |

## Contributer Experience

| project                             | milestones/description                                                                                                      |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| shell script love                   | Improve the build experience outside of Ubuntu                                                                  |
| test MacOS                          | Work through issues with unit tests on MacOS and any productivity issues with the python build script           |      |
| local mode                          | Adama should be able to run locally with a special version of Adama just for applications and local development |
| faster unit tests                   | Improve the testing to not leverage shared resources (stdout, *cough*) such that testing can be made parallel   |
| write documentation about structure | Write a document to outlining the high level mono-repo structure                                                |

## Language

| project      | milestones/description                                                                                                                                              |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| index-tensor | Tables should be indexable based on integer tuples. Beyond efficiency, language extensions can help work with tables in a more natural array style (think 2D grids) |
| index-graph  | Tables should be able to become hyper graphs for efficient navigation between records using a graph where edges can be annotated (this maps)                        |
| math-matrix  | The type system and math library should come with vectors and matrices out of the box                                                                               |
| xml support  | Convert messages to XML                                                                                                                                             |
| rxhtml       | Embed rxhtml into compile process                                                                                                                                   |

## Infrastructure - Protocols
| project   | milestones/description                                                                                    |
|-----------|-----------------------------------------------------------------------------------------------------------|
| mqtt      | (1) Write design document how to adapt Adama to MQTT, (2) Build it with TLS, (3) Built it with plain-text |
| sse       | (1) Write design document how to adapt Adama to Server-Sent Events, (2) Build it with TLS                 |                             
| web async | Allow all web operations operations to become asynchronous                                                |

## Infrastructure - Enterprise data
| project        | milestones/description                                                                                                                                                                                                                 |
|----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| smaller deltas | (1) Define a log format that leverages binary serialization and compacts field definitions, (2) convert JSON deltas to binary in the logger to measure impact (throughput and latency), (3) leverage format upstream to minimize future network |
| healing log    | Implement a log data structure that can heal (anti entropy) across machines using Adama's network stack                                                                                                                                |
| raft           | Implement raft leader election and log append using Adama's network stack                                                                                                                                                              |
| control plane  | (1) Manual definition of raft shards definitions, (2) automatic machine management                                                                                                                                                     |

## Infrastructure - Multi-region &amp; massive scale
| project    | milestones                     |
|------------|-------------------------------------------|
| proxy-mode | proxy the WS API from region A to region B |
| remote-finder | extend WS API to implement a Finder for region A to do core tasks |
| finder in adama | Turn core service into a finder cache for web tier |
| region-isolate | Allow regions to have storage for local documents |

## Infrastructure - Core Service
| project | milestones/description        |
|---------|-------------------------------|
| dx      |  |

## Infrastructure - Overlord
| --------- | -------------------------------                                                                                           |
|-----------|---------------------------------------------------------------------------------------------------------------------------|
| canary    | for testing the service health and correctness; overlord should maintain a constant state of various high-value API calls |

## RxHTML
| project     | milestones/description                                                                         |
|-------------|------------------------------------------------------------------------------------------------|
| headwindcss | Port tailwindcss to Java for vertical control                                                  |
| components  | Bring clarity for single javascript extentions for new controls                                |
| time        | Custom component for selecting a time of day (Blocked on *components* model)                   |
| date        | Custom component for selecting a date or a date range (Blocked on *components* model)          |
| color       | Custom component for selecting a color (Blocked on *components* model)                         |
| server-side | Create a customized shell for each page such that server side rendering allows faster presence | 

## Roslin (RxImage)
| project           | milestones/description                               |
|-------------------|------------------------------------------------------|
| design document   | Write a design document from Jeff's notes            | 
| runtime-android   | Implement a starting runtime for web using android   |
| gameboard demo(s) | Write a game board demo of various games             |
| runtime-web       | Implement runtime for web using rust                 |
| rxhtml-integ      | Integrate runtime-web into RxHTML as a new component | 

## RxApp

| project         | milestones                                                                                               |
|-----------------|----------------------------------------------------------------------------------------------------------|
| design document | design a simple XML only way to build Android applications using reactive data binding similar to RxHTML |



| project | milestones/description  |
|---------|------------|
|  |            |

