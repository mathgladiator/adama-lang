# RxHTML

## The forest of root elements

With RxHTML, the root element is no longer &lt;html&gt;. Instead, it is a &lt;forest&gt; with multiple pages (via &lt;page&gt; element) and templates (via &lt;template&gt; element).
While there are a few custom elements like &lt;connection&gt; (connect the children html to an Adama document) and &lt;lookup&gt; (pull data from the Adama document into a text node), the bulk of RxHTML rests with special attributes like *rx:if*, *rx:ifnot*, *rx:iterate*, *rx:switch*, *rx:template*, and more.
These attributes reactively bind the HTML document to an Adama tree such that changes in the Adama tree manifest in DOM changes.

### The root elements within a &lt;forest&gt;

There are three core elements under a &lt;forest&gt;: pages, templates, and the one true shell.

#### &lt;page uri="$uri"&gt;

A page is a full-page document that is routable via a uri.

```html
<forest>
    <page uri="/">
        <h1>Hello World</h1>
    </page>
</forest>
```

The uri is broken up into components by splitting via the '/' character and the uri must alway start with '.'. 
For example, the uri "/foo/page/doctor" breaks down into three components

| index | component |
|-------|-----------|
| 0     | foo       |
| 1     | page      |
| 2     | doctor    |

These components are all fixed constants, but we can introduce both numeric and string variances by prefixing the component with the '$' character.


Beyond the uri, a page may also require authentication. This is denoted by the valueless attribute **authenticate**. When there is no default identity available, requests to that URI will forward to the page marked with the valueless attribute  **default-redirect-source**. For example:

```html
<forest>
    <page uri="/product" authenticate>
        The secure product
    </page>
    <page uri="/signin" default-redirect-source>
</forest>
```

#### &lt;template name="$name"&gt;

A template is fundamentally a way to wrap a shared bit of RxHTML within a name. At core, it's a function with no parameters.
Templates are how RxHTML achieve user interface re-use, and the philosophy is akin to [duck typing](https://en.wikipedia.org/wiki/Duck_typing) where if the data behaves like a duck, then a template will make the duck pretty.

```html
<forest>
  <template name="template_name">
    <nav>
      It's common to use template for components
      that are repeated heavily like headers, 
      components, widgets, etc...
    </nav>
  </template>
</forest>
```

However, there are parameters via the &lt;fragment&gt; element and the *rx:case* attribute. See below for more details.

```html
<forest>
  <template name="template_name_that_switches">
    <nav>
      It's common to use template for components
      that are repeated heavily like headers, 
      components, widgets, etc...
    </nav>
    <fragment case="x" />
    <fragment case="y" />
    <fragment case="x" />
  </template>
</forest>
```

Templates can be invoked in one of two ways: as a child or inline. The above template can be invoked via the **rx:template** attribute as a child.

```html
<forest>
  <page uri="/use-template-child">
    <div rx:template="template_name_that_switches" class="clazz">
        <div rx:case="y">
            [Y]
        </div>
        <div rx:case="x">
            [X]
        </div>
    </div>
  </page>
</forest>
```

This will has the effect of generating DOM like:
```html
<div class="clazz">
  <nav>
    It's common to use template for components
    that are repeated heavily like headers, 
    components, widgets, etc...
  </nav>
  <div>[X]</div>
  <div>[Y]</div>
  <div>[X]</div>
</div>
```

Now, if the parent div is not desired, then &lt;inline-template&gt; is available as a pseudo-node within RxHTML.

```html
<forest>
  <page uri="/use-template-child">
    <inline-template name="template_name_that_switches">
        <div rx:case="y">
            [Y]
        </div>
        <div rx:case="x">
            [X]
        </div>
    </inline-template>
  </page>
</forest>
```

This will act as if inline-template doesn't exist and merge the tree into the parent. This requires care as some elements or attributes will inject a div when needed.

### The shell

An RxHTML forest can only have one &lt;shell&gt; element which is used to configure the generated application shell.


## Data binding with &lt;connection space="$space" key="$key" &gt;

Data can be pulled into HTML via a connection to an Adama document given the document coordinates (space and key).

```html
<forest>
  <page uri="/">
    <connection space="my-space" key="my-key">
      ... use data from the document ...
    </connection>
  </page>
</forest>
```

Alternatively, connections can be established via the domain for use in a [multi-tenant product](/web/multi-tenant.md).

```html
<forest>
  <page uri="/">
    <connection use-domain>
      ... connect to the domain referenced by the domain ...
    </connection>
  </page>
</forest>
```

### Pathing; understanding what $path values

The connection is ultimately providing an object, and various elements and attributes will utilize a value as a path.
We see this directly in the &lt;lookup&gt; text element.

```html
  ...
    <lookup path="my_field" />
  ...

```

This will find the value the value at my_field within the document and convert it to a text node.

The mental model being played out is that a connection is providing a document that is a hierarchical in nature.
We use pathing similar to how file systems provide a current working directory.
At the start of a connection, we start in the root of the document.
Various attributes will manipulate the pathing, but we can also explicitily navigate the directory for lookup.

This path may be a simple field within the current object, or it can be a complex expression to navigate the object structure.

| rule    | what                                       | example                           |
|---------|--------------------------------------------|-----------------------------------|
| /$      | navigate to the document's root            | &gt;lookup path="/title" /&gt;    |
| ../$    | navigate up to the parent's (if it exists) | &gt;lookup path="../name" /&gt;   |
| child/$ | navigate within a child object             | &gt;lookup path="info/name" /&gt; |

### Viewstate versus Data

At any time, there are two sources of data. There is the data channel which comes from adama, and there is the view channel which is the view state.

The view state is information that is controlled by the view to define the focus of the viewer, and it is sent to Adama asynchronously.

You can prefix a path with "view:" or "data:" to pull either source, and in most situations, the default is "data:".

### Using data: pulling in a text node via &lt;lookup path="$path" &gt;

```html
<forest>
    <page uri="/">
        <connection space="my-space" key="my-key">
            <h1><lookup path="title" /></h1>
            <h2><lookup path="byline" /></h2>
            <p>
                <lookup path="intro" /><
            </p>
        </connection>
    </page>
</forest>
```

#### Lookup's transforms

The lookup pseudo-element has a transform attribute that runs a function to transform the input into a nicer looking output.

| transform value     | behavior                                                                                 |
|---------------------|------------------------------------------------------------------------------------------|
| principal.agent     | pull out the agent from a principal                                                      |
| principal.authority | pull out the authority from a principal                                                  |
| trim                | trim the string                                                                          |
| upper               | convert the string to upper case                                                         |
| lower               | convert the string to lower case                                                         |
| is_empty_str        | returns true/false if the string is empty                                                |
| is_not_empty_str    | returns true/false if the string is not empty                                            |
| jsonify             | convert the lookup value to a string via JSON                                            |
| time_now            | get the current time now                                                                 |
| size_bytes          | convert a number into a size with a suffix of B, KB, MB, GB                              |
| vulgar_fraction     | converts a double into a integer part with the closest unicode vulgar fraction (eighths) |
| time_ago            | convert a datetime into a time ago                                                       |
| time                | convert a datetime or time from military time to                                         

### Using data: connecting data to attributes

Attributes have a mini-language for building up attribute values using variables pulled from the document or conditions which control the output.

| syntax                           | what                                                                                       |
|----------------------------------|--------------------------------------------------------------------------------------------|
| {var}                            | embed the text behind the variable into the string                                         |
| [b]other[/]                      | embed the stuff between the brackets if the evaluation of the variable b is true           |
| [b]true branch[#]false branch[/] | embed the stuff between the brackets based of the evaluation of b                          |
| [v=$val]other[/]                 | embed the stuff between the brackets if the evaluation of the value v is the given $val    |
| [v=$val]true[#]false[/]          | embed the stuff between the brackets if the evaluation of the value v being the given $val |

```html
<forest>
    <page uri="/">
        <connection space="my-space" key="my-key">
            <a class="[path-to-boolean]active[#]inactive[/]" href="#blah">
            </a>
        </connection>
    </page>
</forest>
```

## Attribute extensions to existing HTML elements

A Guiding philosophy of RxHTML is to minimally extend existing HTML elements with new attributes which bind the HTML tree to a JSON tree

### &lt;tag ... rx:iterate="$path" ... &gt;

The ```rx:iterate``` will iterate the elements of a list/array and scope into each element.
This attribute works best when there is a single HTML child of the tag placed, and it will insert a div if there isn't a single child.
In terms of path, the resulting path is two levels deep: first added level is the list and second added level is the item.

```html
<table>
    <tbody rx:iterate="employees">
        <tr>
            <td><lookup path="name" /></td>
            <td><lookup path="level" /></td>
            <td><lookup path="email" /></td>
        </tr>
    </tbody>
</table>
```

**rx:iterate** respects **rx:expand-view-state**. **rx:expand-view-state** will force the view path to change to mirror the iteration.
This allows each element in the iteration to have a unique view state.

### &lt;tag ... rx:if="$path" ... &gt;

The ```rx:if``` will test a path for true or the presence of an object. If there is an object, then it will scope into it. 

```html
<div rx:if="active">
    Show this if active.
</div>
```

Note: this only renders anything if the value active is present

### &lt;tag ... rx:ifnot="$path" ... &gt;

Similar to ```rx:if```, ```rx:ifnot``` will test the absense of an object or if the value is false.
```html
<div rx:ifnot="active">
    Show this if not active.
</div>
```

Note: this only renders anything if the value active is present

### &lt;tag ... rx:else ... &gt;

Within a tag that has *rx:if* or *rx:ifnot*, the *rx:else* indicates that this element should be within the child if the condition on the parent is the opposite specified.
```html
<div rx:if="active">
    Show this if active.
    <span rx:else>Show this if not active</span>
</div>
```

### force-hiding

For **rx:if** and **rx:ifnot**, the behavior controls the children of the node.
This is required because the node must be stable, and we can ameliorate with the valueless attribute **force-hiding** which will synchronize the result with the node's style.display as this is a common workaround. However, this then means that the associated **rx:else** will never render.

```html
<div rx:ifnot="active" force-hiding>
    Show this ONLY if active is true
</div>
```

### &lt;tag ... rx:switch="$path" ... &gt;

A wee bit more complicated than rx:if, but instead of testing if a value is true will instead select cases based on a string value.
Children the element are selected based on their ```rx:case``` attribute.

```html
<div rx:switch="type">
    Your card is a
    <div rx:case="face_card">
         face card named <lookup path="name"/>
    </div>
    <div rx:case="digit">
         numbered card with value of <lookup path="value"/>
    </div>
</div>
```

Note: this only renders anything if the value is present

### &lt;tag ... rx:case="$value" ... &gt;

Part of ```rx:switch``` and ```rx:template```, this attribute identifies the case that the element belongs too. See ```rx::switch``` for an example.

If a dom element within a child with **rx:switch** doesn't have an **rx:case** then it is rendered for every case, and this is true for both **rx:switch** and templates.

### &lt;tag ... rx:template="$name" ... &gt;

The children of the element with ```rx:template``` are stored as a fragment and then replaced the children from the ```<template name=$name>...</template>```.
The original children be used within the template via ```<fragment />```

```html
<template name="header">
  <header>
  </header>
  <div class="blah-header">
    <h1><fragment /></h1>
  </div>
</template>
<page ur="/">
  <div rx:template="header">
    Home
  </div>
<page>
```

### &lt;fragment&gt;
Fragment is a way for a template to gain access to the children of the invoking element.

Furthermore, fragments support case attribute to filter out children.

### &lt;signout&gt;

Once this element is seen, the identities are destroyed

### &lt;tag ... rx:scope="$path" ... &gt;

Enter an object assuming it is present.

### &lt;form ... rx:action="$action" ... &gt;

Forms that talk to Adama can use a variety of built-in actions like

| rx:action              | behavior                                                                               | requirements                                              |
|------------------------|----------------------------------------------------------------------------------------|-----------------------------------------------------------|
| domain:sign-in         | execute an authorize against the document pointed to by the domain                     | form inputs: username, password                           |
| domain:sign-in-reset   | execute an authorize and password change against the document pointed to by the domain | form inputs: username, password, new_password             |
| domain:put             | execute a @web put against a document pointed to by the domain                         | form element has path attribute                           |
| domain:upload-asset    | upload an asset (and maybe execute a send)                                             | form inputs: files                                        |
| document:sign-in       | sign in to the document                                                                | form inputs: username, password, space, key, remember     |
| document:sign-in-reset | sign in to the document and reset the password                                         | form inputs: username, password, space, key, new_password |
| document:put           | execute a @web put against a document                                                  | form element has attributes: path, space, key             |
| document:upload-asset  | upload assets to the indicated document                                                | form inputs: files, space, key                            |
| adama:sign-in          | sign in as an adama developer                                                          | form inputs: email, password, remember                    |
| adama:sign-up          | sign up as an adama developer                                                          | form inputs: email                                        |
| adama:set-password     | change your adama developer password                                                   | form inputs: email, password                              | 
| send:$channel          | send a message                                                                         | form inputs should confirm to the channel's message type  |
| copy-from:$formId      | copy the form with id $formId into the view state                                      | -                                                         |
| copy:$path             | copy the current form into the view state                                              | -                                                         |
| custom:$verb           | run custom logic                                                                       | -                                                         |


### &lt;custom- ... rx:link="$value" ... &gt;

TODO

### &lt;tag ... rx:wrap="$value" ... &gt;

TODO

### &lt;input ... rx:sync="$path" ... &gt;, &lt;textarea ... rx:sync="$path" ... &gt;, &lt;select ... rx:sync="$path" ... &gt;, oh my

Synchronize the input's value to the view state at the given path.
This will propagate to the server such that filters, searches, auto completes happen.

```html
<input type="text" rx:sync="search_filter" />
```

### &lt;exit-guard guard="$path" set="$path" &gt;

The &lt;exit-guard&gt; will protect against data loss by preventing transition to a new page if the guard path is set to true. If the guard path is true while a page transition happens, then the set path is raised to true.

### &lt;monitor path="$path" delay="10" rise="..." fall="..." /&gt;

The &lt;monitor&gt; element will watch a numeric variable and then fire events if the value rises or falls mirroring [signal edge transitions](https://en.wikipedia.org/wiki/Signal_edge).

### &lt;todotask&gt;

For the sheer joy of task management, &lt;todotask&gt; formats a TODO item in the HTML and aggregates the task into a file within the devbox.

### &lt;title&gt;

Unlike traditional title where the title is placed within the element, the title has a value attribute that can be reactively bound to the document.

```html
  <title value="Messages for {person_name}" />
```

## Events (rx:click, etc...)

Adama supports running a very restrictive command language on various events. The semantics is that a command string evaluates left to right and is delimited by spaces.

```html
  ...
    <input type="text" value="{view:up} / {view:down}">
    <button rx:click="inc:up dec:down">Click Me</button>
  ...
```

Sinces spaces delineate commands, the single quote character is used to open a string

```html
  <button rx:click="set:title='The Big Thing'">Change Title</button>
  <button rx:click="set:title='The Next Thing'">Change Title Again</button>
```

### Command language

| command                  | behavior                                                                                                     |
|--------------------------|--------------------------------------------------------------------------------------------------------------|
| toggle:$path             | toggle a boolean within the viewstate at the given path                                                      |
| inc:$path                | increase a numeric value within the viewstate at the given path                                              |
| dec:$path                | decrease a numeric value within the viewstate at the given path                                              |
| custom:$verb             | run a custom verb                                                                                            |
| set:$path=$value         | set a string to a value within the viewstate at the given path                                               |
| raise:$path              | set a boolean to true within the viewstate at the given path                                                 |
| lower:$path              | set a boolean to true within the viewstate at the given path                                                 |
| decide:$channel          | response with a decision on the given channel pulling (see [decisions](#decisions))                          |
| goto:$uri                | redirect to a given uri                                                                                      |
| decide:$channel          | respond to a decision for a given (TODO)                                                                     |
| choose:$channel          | add a decision aspect for a given channel (TODO)                                                             |
| finalize                 | if there are multiple things to choose, then finalize will commit to a selection                             |
| force-auth:identity=key  | inject an identity token into the system                                                                     |
| fire:$channel            | send an empty message to the given channel                                                                   |
| ot:$path=$val            | the value at the path is a special order string used by [order_dyn](/guide/tables-linq.md#order_dyn)         |
| te:$path                 | transfer the event's message into the view state at the given path                                           |
| tm:$path&#124;$x&#124;$y | transfer the mouse coordinate's X and Y into the view state                                                  |
| reset                    | reset the form                                                                                               |
| submit                   | submit the form                                                                                              |
| resume                   | when an exit guard is place, this will resume the transition                                                 |
| nuke                     | finds the &lt;nuclear&gt; element containing the element throwing the event and then removes it from the DOM |

### Standard Events

| rx:$event  | behavior                     |
|------------|------------------------------|
| click      | the element was clicked      |
| mouseenter | the mouse entered            |
| mouseleave | the mouse left               |  
| change     | the input field changed      |
| blur       | the input field lost focus   |
| focus      | the input field gained focus |


### Custom Events
| rx:$event | behavior                           |
|-----------|------------------------------------|
| load      | runs when the DOM element is bound |
| success   | the form was success               |
| failure   | the form was a failure             |
| submit    | the form was submitted             |
| aftersync | the rx:sync just synchronized      |

## Todo
* customdata
* wrapping
* decisions