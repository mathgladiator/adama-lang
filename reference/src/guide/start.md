# Language Guide

Welcome to the Adama Language Guide.

Adama is a programming language designed to facilitate building online applications for both the web and mobile.
It requires defining an Adama specification file; this Adama specification file defines both the structure and logic required to power interactive experiences.

The main challenge when building an Adama application is structuring the state within your document.
You need to define the variables and tables that hold the state of your application and how those variables change over time.
Adama also allows you to open channels for people to send messages to change the state of your application.
For instance, users can submit forms, click buttons, or interact with other elements on your application to trigger events that change the state of the application.

Adama also enables the use of state machines to breathe life into your document.
State machines help you define the different states that your application can be in and how it transitions from one state to another.
This allows you to build complex applications with dynamic behavior, such as multi-step forms, wizards, and interactive workflows.
By using Adama's state machine, you can define the rules that govern how your application behaves and responds to user actions, making your application more interactive and engaging for your users.

## Unique to Adama
Adama is a cutting-edge programming language that curates syntax of several well-known languages, including C++, Java, and JavaScript, to provide a unique and powerful development experience.
This tour of Adama's features is structured in order of their uniqueness, highlighting the most innovative aspects of the language first.
By exploring these features, you will gain a deeper understanding of Adama's capabilities and how it differs from other languages.

- [Document layout](./document.md); learn the fundamental techniques for structuring your state within your application. This involves defining variables that hold the current state of your application.
- [Static policies and document events](./static-policies-document-events.md); in order to understand how users connect to documents in Adama, it's essential to learn about access control and the various document events that trigger it. Through these events, you can control who has access to your application and what they can do with it. By mastering Adama's access control features, you can ensure that your application is secure and only accessible to authorized users. This is a critical component of building robust and reliable web and mobile applications, and understanding how it works is essential for any Adama developer.
- [Privacy and bubbles](./privacy-and-bubbles.md); controlling information disclosure is a vital aspect of any modern project, and Adama takes a unique approach to achieving this through privacy rules and visibility modifiers. By mastering Adama's privacy rules and visibility modifiers, you can control the information that your application exposes to its users and external entities. This is a critical component of building secure and reliable web and mobile applications, and Adama's approach sets it apart. Through these features, you can ensure that your application's data and logic remain protected and that your users have the best possible experience.
- [Reactive formulas](./formulas.md); learn how to compute data reactively based on changes to other data within the document.
- [The glorious state machine](./state-machine.md); the state machine is a powerful tool for managing complex workflows and processes. By defining states and transitions, you can easily model the behavior of your system and handle different scenarios based on the current state.
- [Async with channels, futures, and handlers](./async.md); the document can accept messages directly or ask connected users for messages. Learn how to define handlers and ask user for messages.

Information is structured either for persistence or communication.
Persistence refers to data that is stored for a long time and needs to be retrieved later, while communication refers to data that is transmitted from one point to another in real-time.
 
- [Records](./records.md); learn how to structure persistence within a document using records.
- [Messages](./messages.md); learn how to structure communication to and within document using messages.
- [Tables and integrated query](./tables-linq.md); learn how to collect records and messages into tables which can be queried with integrated query. 
- [Anonymous messages and arrays](./anonymous.md); learn how to use anonymous messages to instantiate messages with or without a type.
- [Maps and reduce](./map-reduce.md); learn how to use maps as a basic collection.

Defining and changing state is great, but Adama takes it a step further by enabling connections to other services, handling web requests, and communication between documents.
These additional capabilities allow Adama to operate within a larger ecosystem of services and data sources.
With Adama, users can create sophisticated applications that interact with various data sources, making it a powerful tool for building complex systems.

- [Web processing](./web.md)
- [Interacting with remote services](./services.md) 
- [Talking to other Adama Documents as an Actor Network](./actor.md)

## Common language guide

- [Comments are good for your health](./comments.md)
- [Constants](./constants.md)
- [Local variables and assignment](./local-variables-and-assignment.md)
- [Doing math](./math.md)
- [Maybe some data, maybe not](./maybe.md)
- [Standard control](./control.md)
- [Functions, procedures, and methods oh my](./functions-procedures-methods-oh-my.md)
- [Enumerations and dynamic dispatch](./enumerations.md)
