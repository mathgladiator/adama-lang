---
slug: saas-march-towards-production
title: A SaaS marches towards production; defining an engineering culture.
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [infrastructure, saas]
---

Good news!

I'm now testing the integration of the components that I've been talking about by building a staging/integ environment.
This new environment looks and feels like the future production site except I play fast and loose.
As I move fast, I reveal problems which will be problematic, and I'm taking notes.
Fortunately, most things are working as intended. My gossip is gossiping. My routing tables are routing. Deploy kind of works.

The open question is when can I launch a single production cluster.
Well, I could probably start using the current environment as-is to build games and start launching games and debug the production issues as I go.
That's the smart business move.

I'm not smart at business.
I'm an engineer's engineer building out of joy.
I'm over-engineering the shit out of this service because I care.
I care deeply about engineering culture which is why I struggle with product minded 'launch-quickly' folks.

Look, I understand the spirit of moving fast and breaking things, but I'm an old man that demands reliable services.
As I am currently filing for an LLC and intend to start a company around this, the values of that company will manifest because "the fish rots from the head down".
Thus, I'm going to talk about the engineering discipline being put into action for this effort.

## Code quality via unit test coverage
It all starts with unit tests and coverage, so let's take an audit of where I am at today as I build this. The core which contains the language, the embeddable service, and runtime clocks in with 2,781 tests at 100% coverage.

![beyond core has 100% unit tests](/img/20220114-core-unit-tests.png)

Expanding on this, I'm not happy with all the modules, but I'll share where things are at.

| module | coverage % | test count |
| --- | --- | --- |
| <font color="red">apikit</font> | 0% | 0 |
| <font color="red">canary</font> | 0% | 0 |
| <font color="red">cli</font> | 0% | 0 |
| <font color="green">common</font> | 100% | 31 |
| <font color="green">core</font> | 100% | 2781 |
| <font color="green">data-mysql</font> | 100% | 16 |
| <font color="green">errors</font> | 100% | 2 |
| <font color="green">gossip</font> | 100% | 31 |
| <font color="red">grpc</font> | 80%  | 40 |
| <font color="orange">lsp</font> | 97% | 29 |
| <font color="red">overlord</font> | 0% | 0 |
| <font color="red">saas</font> | 22% | 3 |
| <font color="green">web</font> | 100% | 33 |

At this moment, there are 2,966 unit tests for this SaaS that has yet to launch. As a launch requirement, the modules **grpc**, **saas**, **cli**, and **overlord** will require at least 95% test coverage.

I intend to rewrite **apikit** at a future date with testability in mind as well as producing great error messages. Fortunately, its artifacts will be covered via **saas** module.

I'm sad about **lsp**, but I don't use it anymore. Once I get back to the business of building games, I'll give it more love along with other tools focused on the developer experience.

**canary** will get partial testing, but it itself is a test that is just continously running.

Unit testing with fantastic coverage is an important first step, but it isn't everything. At the end of the day, the software needs to be hooked up.

## The staging environment where things get... staged

I intend to have some form of continuous deployment where changes in Github are deployed, so I am building that environment out with EC2. It's currently setup within a VPC, strict security groups, seven nano hosts spread across three availability zones, a small RDS instance, and an ELB connecting users to it. It's alive!

This environment is to validate that the service can be stood up and working providing the first proof of life:

![beyond core has 100% unit tests](/img/20220114-proof-of-life.png)

At this point, the fun has only begun. I'm building out my home server to run Jenkins.

## Load testing with a canary
The next big step which I've kicked off is a canary which will functionally test the server like an acceptance test, measure error rates, and generate various load profiles.

The unit tests validate the code works and is correct with respect to behavior in various scenarios. The canary tool has the job to ensure the code is behaving well during both peace and war.

When continuous deployment is running along side the canary load tool, I'll get a sense of how well-thought-out some of my ideas were.

## Metrics, metrics, metrics

I've started to learn [prometheus](https://prometheus.io), and I've got in running collecting metrics from all seven hosts in EC2.

![I have graphs](/img/20220114-metrics.png)

At this point, I've gone from reckless building to engineering as I'm measuring. I've still got many things to instrument. I also need a dashboard, and I'm wondering if I can just code generate a bunch of prometheus queries.

## Alarms
Once you have metrics, you can then alarm on them.
I intend to monitor every failure code which has the property of being used in only one place.
This strong emphasis on having every single exception tracked enables me to pinpoint what is going on with laser focus.

Problematically, I don't have a clear sense of which errors are my fault (i.e. no capacity for a request) or your fault (i.e. the document doesn't exist). I will have the fun task of whitelisting all errors over time.

The key is that the system should produce alarms when things go bump.

## Logging - Bad Times
I'm not a fan of logging exceptions, but I can't deny how useful they are when things go bump in the night.
I currently have SLF4J in place, but they currently go nowhere.
My next step here is to setup a directory where logs get written, gzipped, rotated, uploaded to S3, and garbage collected.
I'm sure there is a SLF4J tutorial that I just need to read and go forth.
Fortunately, my logging needs are low since I don't depend on too many packages which fail.

If I come to rely on these logs, then I'll probably need to upload them to a service to index them.

## Logging - Good Times
Here is where things get tricky when it comes to regulations. Before I launch, I need to investigation what type of logging I should have for things like access control.

I currently have no access logs. Do I need them? ... maybe.

## Billing
I'm still thinking about the billing model, but I am dumping billing records to disk at the moment which the overlord service will collect and combine into a single hourly row within a database.

This billing data should also be exposed via API, but none of this has been built yet.

## Capacity management
As of now, capacity is a mess (with known bugs). I basically spread load for a single customer over three hosts randomly. I intend to monitor the hosts and react within seconds to add/remove customer capacity.

This is going to be an interesting game to play over the long term, and represents a core challenge to build within the overlord service. Overlord will monitor the CPU/Memory per host, and then use billing information to decide a variety of things
* which customer spaces should be given more or less shared capacity
* which customer spaces should be given more or less dedicated capacity
* how do customer spaces overlap in a multi-tenant world

This influences the business model of what I offer.

## Queues, queues, queues

Bad things will happen. Things will get slow. Things will get stuck. Threads will die. Machines will burn. Children will cry.

Many catastrophes happen because of some unbound queue stuck in the corner absorbing more work than the machine could possibly handle, so it does something really bad.

I'm currently taking control over all the Executors that I use by wrapping them with the SimpleExecutor interface, so the next step here is to ensure that each Executor is bound and has the ability to reject requests up to the enqueuer.

## This is the way

Long story short, I have a lot to do. The short list is:
* figure out billing
* send logs somewhere
* find appropriate regulations around access logging
* finish the canary and start pounding the service
* instrument more things
* alarm on all the things
* bound all the queues
* figure out how to handle heat
* finish up unit tests for critical modules

I have something somewhat working, but I'm not done... just not done. If anyone is interested in playing with it, then shoot me an e-mail.







