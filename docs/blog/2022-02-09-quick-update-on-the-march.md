---
slug: quick-update-on-the-production-march
title: Quick update on the march towards production
author: Jeffrey M. Barber
author_title: Dark Lord
author_url: https://github.com/mathgladiator
author_image_url: https://github.com/mathgladiator.png?size=96
tags: [infrastructure, saas]
---

Progress is being made, and things are looking good. Wanted to post an update on the [march towards production](2022-01-14-march-towards-production.md)

## Code quality via unit test coverage

| module | coverage % | test count |
| --- | --- | --- |
| <font color="green">apikit</font> | 95% | 2 |
| <font color="orange">canary</font> | 0% | 0 |
| <font color="orange">cli</font> | 21% | 2 |
| <font color="green">common</font> | 100% | 40 |
| <font color="green">core</font> | 100% | 2865 |
| <font color="green">data-mysql</font> | 100% | 22 |
| <font color="green">errors</font> | 100% | 2 |
| <font color="green">gossip</font> | 100% | 33 |
| <font color="green">grpc</font> | 100% | 62 |
| <font color="green">lsp</font> | 97% | 29 |
| <font color="orange">overlord</font> | 28% | 1 |
| <font color="green">saas</font> | 96% | 36 |
| <font color="green">web</font> | 98% | 37 |

Went from 2966 tests to 3131 tests, and big improvements in both grpc and saas since last update.
I also have Jenkins running every couple of minutes, and the last two days had 500+ test runs all successful.
I've decided that CLI is going to be primarily manually tested by since as it is a thin shell over a websocket client.
Overlord is going to be an interesting test task, but I'm dropping it as a requirement since it's basically a bunch of cron jobs.
With all these tests, my manual testing, the new saas tests, and using the canary give me a feeling that the software is ready for production.

## Staging environment and load testing
I've got a personal staging environment for myself, and I'm able to use it and deploy regularly. This staging environment is complete with dashboards, and I'm only missing alerts telling me bad things.

I've run the new canary multiple times, and I found a great many bugs. This produced a virtuous cycle of adding metrics, fixing problems, and adding tests. As of now, the software feels very stable and the graphs reflect that.

A big issue however with my staging environment is that I'm running on nanos which burn CPU credits if cpu goes above 5%. This was problematic during some canary loads as stability became a loss, but I was able to see traffic shifts when throttling happened. This helped debug a number of issues, but I eventually ran out of capacity.

## Metrics, metrics, and alarms

I've learned a decent amount about [prometheus](https://prometheus.io), and I've got dashboards up with all my metrics. I'm using a metrics factory to build various *Metrics classes which then allow me to generate both consoles/dashboards and alarms.

A key step for production is to use alertsmanager and have them wake me up.

I'm still in the boat of having no clear idea of which errors are my problem versus the customer problem, so I intend to monitor the situation and build a list of error codes which are "customer errors" and then treat the request as a success with a "user error counter".

## Logging - Bad and Good Times
I've got Logback setup for both exceptions and for access logs. The apikit was setup to filter which parts of a request to log, so I can study usage without violating privacy.

I'm happy that I'm producing logs, but they are sitting on disk right now. After a few production issues, I need to clean them up or find a partner to offload indexing them. As an operator, I generally prefer just diving onto hosts, so I may also just optimize my tooling for that.

## Billing
I've got metering in place with a poorly designed rates table. First thing, I need to build production environment and load test it to come up with a reasonable rates.

Once I have a good rates structure, I need to enforce usage limits, set up stripe api, and then connect the dots. Since I intend to start with indies, I'm going to just have a simple "recharge your account manually option" and then setup of recurring billing on customer request.

## This is the way

I still have a lot to do!

The short list is:
* automate the canary to run within Jenkins
* identify which problems are "user problems" (i.e. file not found) versus "system problems" (i.e. no capacity for this request)
* the ever present task of more instrumentation, more alarms
* bound more queues, but not as high priority
* continue to think about capacity management and heat; I can launch now with beefier machines and also introduce capacity before getting too smart.
* connecting billing to stripe and send bills
* start the public website with some limited visibility to accept credit cards
* finish the new documentation site (which uses mdbook)



