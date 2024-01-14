# Future of Adama Documentation

I want to revamp both the [main site](https://www.adama-platform.com/) and [this book](https://book.adama-platform.com/) in one cohensive product rather than two distinct entities. Eventually, the book will simply be a set of redirects to the main site as they achieve glorious unity.

However, it's easy to get into the mode of building yet another engine blah-blah-blah, and instead I want to try something rather radical.
Well, it's not that radical, but I want a great experience for developers.

Since I'm the inventor or RxHTML, [I can extend it to do documentation and then make RxHTML better in one go as well.](#engine-improvements-for-rxhtml)
This document then is a plan for the engineering investments since I'm also trying to move away from quip.
I want all my design documents in one place, and I want design documents linkable with documentation.

[I also need to improve the content which I'll start here with a glorious outline.](#content-refactored)

## Engine Improvements for RxHTML

The first thing is that instead of markdown, all content is going to be .rx.html and this summons many problems like: navigation, bandwidth, search, markdown. Finally, I want to support Adama as a first class citizen.

All this means I need a few higher level primitives and some new stuff, so let's talk about the pain points of using RxHTML for a large amount of content.

### Navigation

Currently, navigation is a PITA in RxHTML because it's very low level at the moment which works for complex systems, but with a large volume of content...

```html
<forest>
    <template name="some-template-make-it-look-good">
        <div class="some common css [group=g1]active[#]inactive[/]">
            <b>Group 1</b>
            <div rx:if="view:show_g1">
                <a href="/some/path/to/content"
                   class="link clazz [current=this-content]active[#]inactive[/]">
                    Link to that content</a>
            </div>
        </div>
    </template>
    <page uri="/some/path/to/content">
        <div rx:template="some-template-make-it-look-good"
             rx:load="set:group=g1 set:current=this-content raise:show_g1">
            ... content ...
        </div>
    </page>
</forest>
```

At core, the above is how to build products that are very customizable, but it does get repetitive and I do like this core idea of content describing itself.
So, if I can provide some new higher level primitives to simplify the above, then that's great.

**Idea #1.** Common templates by uri

```html
<forest>
    <template name="some-template-make-it-look-good">
        <div class="some common css [group=g1]active[#]inactive[/]">
            <b>Group 1</b>
            <div rx:if="view:show_g1">
                <a href="/some/path/to/more/content"
                   class="link clazz [current=this-content]active[#]inactive[/]">
                    Link to that content</a>
                <a href="/some/path/to/content"
                   class="link clazz [current=more-content]active[#]inactive[/]">
                    Link to more content</a>
            </div>
        </div>
    </template>
    <common-page uri:prefix="/some/path"
                 template:use="some-template-make-it-look-good"
                 template:tag="div"
                 init:show_g1="true"
                 init:group="g1" />
    <page uri="/some/path/to/content"
          init:current="this-content">
        ... content ...
    </page>
    <page uri="/some/path/to/more/content"
          init:current="more-content">
        ... more content ...
    </page>
</forest>
```

This allows a way for the page to describe just the content, save a level of tabbing, and let the repetitive nature of group variables be made depend entirely on the uri. I like this, and it's a good feature!
The emergent engineering work is:
* task: introduce init:$var="$constant" as part of the &lt;page&gt; element
* task: introduce &lt;common-page&gt; as a root element, update &lt;page&gt; to (1) wrap the children with a template (if template:use is present) with a div (or whatever template:tag is), (2) merge the init:$var="$constant"

**Idea #2.** Runtime explicit static objects

As of now, RxHTML has two "stream" of information:
* "data:" to pull information from the server
* "view:" to pull information from the view state

The repetitive nature is the current navigation structure is good for fine-tuning behavior, but it is a recipe for small mistakes and duplicate code.
Instead, what if I introduce a third channel called static which contains a giant object of globally static data (which, maybe mutated?).
Or, I could use the view channel to have a pre-populated object "view:static" which is then never sent to the server.
The pros of introducing a new "stream" is a clean slate which can then be optimized around differently, but it's a lot of work which is con and will require touching a bunch of code.
I'll think upon this matter more!

* Task: think about "new stream" versus "abusing view stream".

**Idea #3.** Compile time static objects with static templates

So, here, I add a preprocessor to the entire forest.

```html
<forest>
    <template name="some-template-make-it-look-good">
        <div class="some common css [group=g1]active[#]inactive[/]">
            <b>Group 1</b>
            <div rx:if="view:show_g1" static:iterate="nav/g1/children">
                <a href="%%{uri}%%" 
                   class="link clazz [current=%%{id}%%]active[#]inactive[/]">
                    %%{name}%%</a>
            </div>
        </div>
    </template>
    <common-page uri:prefix="/some/path"
                 template:use="some-template-make-it-look-good"
                 template:tag="div"
                 init:show_g1="true"
                 init:group="g1" />
    <page uri="/some/path/to/content"
          static:path="nav/g1/children+"
          static:set:name="Link to some content"
          static:ordering="1"
          static:invent:id
          static:copy:uri          
    >
        ... content ...
    </page>
    <page uri="/some/path/to/more/content"
          static:path="nav/g1/children+"
          static:set:name="Link to more content"
          static:ordering="2"
          static:invent:id="current"
          static:copy:uri
    >
        ... more content ...
    </page>
</forest>
```

Here, the goal is that each page describes itself and the construction of the object is done by pre-processing all the pages to build the static object which can be mutated by various attributes.
I like this as it reduces the repetitiveness of building the navigation tree and also provides a better control over the entire produce experience.
Now, at this time, navigation is the primary PITA where this repetitive behavior manifests, so I'm happy to strike a balance between niche and generic functionality.

This body of work has:
* introduce static:path=$object-path for single object introduction
* consider static[$label]:* for multi-object introduction
* introduce static:set:$name=$value for adding data into an object
* introduce static:copy:$name for copying an existing attribute into an object
* introduce static:invent:id=$name for inventing a viewstate id that is then set on rx:load
* introduce static:iterate for iterating over a path
* introduce static:scope for scoping into an object
* introduce static:if for testing a static value is true/false

The core body of work here is the idea of introducing a preprocessor which sounds fun!

### Bandwidth

Since ultimately the documentation is a bunch of static content, expecting users to download ALL content in one bundle is insane!
(Or an amazing benefit since it would work offline especially if all images were data:base64 encoded... food for thought)
Worse yet, the way RxHTML constructs the DOM assumes a lot of reactivity, so it's downloading mostly static content to then construct the DOM in Javascript.
It works, but it's not great.

The body of work here is to start optimizing for bandwidth ruthlessly!

**Idea 4.**

For existing applications, there is a body of work to identify static chunks that the DOM never touches, and then marks the children as static. This requires a preprocessor to see

```html
<forest>
    <page uri="/blah">
        <div> BUNCH of HTML </div>
    </page>
</forest>
```

and then annotate the div with

```html
<forest>
    <page uri="/blah">
        <div static:content> BUNCH of HTML </div>
    </page>
</forest>
```

such that once the div is created to use innerHTML. As an isolated experiment, this is useful to build an test with current products to measure bandwidth reduction.
In the context of a tremendous volume of static context, this isn't sufficient.

**Idea 5.**
Ultimately, a major refactor is to make page loading dynamic. Fortunately, this was designed by nature of having templates and pages as the only top level primitives with static names.

The core algorithm is to do a dependency analysis on pages and templates to identify which templates are unique to a page and which templates are shared between pages.
A naive approach would be to send all common templates on initial page and then hydrate pages as needed.
Alternatively, pages could be bundled together in various ways for performance reasons.
The core task however is to delineate between an initial page load versus a patch update, and then the initial page load would have an index of which pages are immediately available and indexes versus pages that need a request.

Now, care must be taken such that a version change in the forest (due to a deployment) would trigger an appropriate refresh of the entire page.

Thus, the initial page refresh would load the system, and then a special call (GET /~rxhtml) would have a special API such that the system would send (a) the uri to resolve, (b) the version of the system, (c) the relevant templates to the page that are already loaded. This new fetch would then check the version, and if a deployment happened would immediately return

This body of work has task working such as:
* build a dependency closure of all templates per page
* introduce a new way to pull a partial set of pages and templates on the fly
* allow developers to mark pages as together to bundle together (manual/nieve bundling)
* update rxhtml.js to support deferred pages to invoke the new pull methodology

**Idea 6.**
Instead of having fancy-pants SPA, I simply disable page linking entirely and use RxHTML as a static generator.
Frankly, this is the best short term option and the easiest to pull off.

This body of work has the task work:
* introduce a way to generate all pages statically similar **Idea 5**'s dynamic initial page.
* write tooling in the CLI to spit it out
* add a way to annotate assets to have special Content-Type

### Search

At core, I need to understand how [Elasticlunr](http://elasticlunr.com/) works and how to precompile indexing information.

### Markdown

As part of the emergent pre-processing, I want to support a &lt;static:markdown&gt; tag which converts the markdown into HTML.

```html
<forest>
    <page uri="/blah">
        <static:markdown>
            # Title
            ## Subtitle
        </static:markdown>
    </page>
</forest>
```

Now, this poses some interesting thoughts about how this can feed the metadata for a side nav or the title of the document.
We can leverage existing template behavior such the body is the default case and we expose rx:cases for title and outline. 
This is a fruitful experiment!

The body of work here is:
* introduce static:markdown to compile markdown into HTML during pre-process phase
* play with rx:case to allow extraction of a simple nav scheme, title, and maybe some tags?
* think about how to convert some jekyll aspects of the blog like header image, etc...

### Adama Support

Now, the piece de resistance of why I want to improve RxHTML to power my documentation (beyond the potential of making it interactive), I want to leverage the Adama parser to type check my documentation so nothing is ever a mistake.
Furthermore, I want to have documentation produce test cases for the test generator.

The trivial thing at play is to simply introduce a &lt;static:adama&gt; and then validate that it parsers.

```html
<forest>
    <page uri="/blah">
        <static:adama>
            #state {
              // blah
            }
        </static:adama>
    </page>
</forest>
```

However, an alternative mode is to ensure that all &lt;static:adama&gt; combine together to parse and type collectively.
This allow a linear narrative, but narrative can be deception as I may also want to provide an outline and update code to tell a story and encourage patching.
This is where the job becomes more intense, so I think about introducing a &lt;fragment&gt; tag with a name attribute.

```html
<forest>
    <page uri="/blah">
        <static:adama>
            #state {
              <fragment name="foo">// blah</fragment>
            }
        </static:adama>
    </page>
</forest>
```

This requires thought, but the spirit is that later chunk could fill that in

```html
<forest>
    <page uri="/blah">
        <static:adama>
            #state {
              <fragment name="foo">// blah</fragment>
            }
        </static:adama>

        <static:adama replace="foo">
            t++;
        </static:adama>
    </page>
</forest>
```

This allows me to parse and type the entire assuming I linearize the &lt;static:adama&gt; into a stream A[0], A[1], ..., A[N].

I can then confirm that parse and type A[0], then A[0] + A[1] (where A[1] either appends or replaces a fragment), and so on.
This would give me clarity on the user experience and if there are stupid errors.

## Content Refactored

Now, beyond improving the engine, I also want to improve the documentation to give the best developer experience I can.

### Major Sections and new outline (Draft)

- Documentation
  - Introduction
    - Start here
    - How does Adama work?
    - Core concepts
    - Commercial licensing
  - Starting on the cloud
    - Download the tooling
    - Setting up an account
    - Creating a sample application
    - Deploying and making changes
  - Engineering team integration
    - The developer sandbox 
    - #yolo mode
    - Change management
  - Executive team integration
    - Metrics
    - Access control policies
    - Approvals
    - Back up auditing
- Language Reference
  - Access control
    - Static policies
    - Document access control
    - Authentication
  - Data, types, and privacy
    - Privacy Policy
    - Types
      - (all types)
    - Formulas
    - Bubbles
  - Message handling
    - Defining
    - Channels
    - Internal Queue
    - Workflow
  - State Machine
    - Single Workflow
    - Cron Jobs
  - Code to compute
    - Control
    - Assignment
    - Native tables
    - Procedures, functions
    - Methods
    - Language Integrated Query
  - (more)
- Standard Library
  - Strings
  - Math
  - Statistics
  - Principals
  - Dates and times
- Platform API & SDK Usage
  - Authentication
    - Identities
    - Default Policies
  - Documents
  - Web server
  - Domain hosting
  - Security
  - Operations
  - API
    - (each method)
- Guides
- RxHTML
  - Elements
  - Attributes
  - Recipes
- Examples and Recipes
  - Showcase app
  - Games
  - Internet of Things
  - Web sites recipes
    - Pagination