# RxHTML

## The forest of root elements

With RxHTML, the root element is no longer &lt;html&gt;. Instead, it is a &lt;forest&gt; with multiple pages (via &lt;page&gt; element) and templates (via &lt;template&gt; element).
While there are a few custom elements like &lt;connection&gt; (connect the children html to an Adama document) and &lt;lookup&gt; (pull data from the Adama document into a text node), the bulk of RxHTML rests with special attributes like *rx:if*, *rx:ifnot*, *rx:iterate*, *rx:switch*, *rx:template*, and more.
These attributes reactively bind the HTML document to an Adama tree such that changes in the Adama tree manifest in DOM changes.

### &lt;template name="$name"&gt;

A template is fundamentally a way to wrap a shared bit of RxHTML within a name. At core, it's a function with no parameters.

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

### &lt;page uri="$uri"&gt;

A page is a full-page document that is routable via a uri.

```html
<forest>
    <page uri="/">
        <h1>Hello World</h1>
    </page>
</forest>
```

Beyond the uri, a page may also require authentication.

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

### Pathing; understanding what $path values

The connection is ultimately providing an object, and various elements and attributes will utilize a value as a path.
This path may be a simple field within the current object, or it can be a complex expression to navigate the object structure.

| rule | what | example |
| --- | --- | --- |
| /$ | navigate to the document's root | &gt;lookup path="/title" /&gt; |
| ../$ | navigate up to the parent's (if it exists) | &gt;lookup path="../name" /&gt; |
| child/$ | navigate within a child object | &gt;lookup path="info/name" /&gt; |

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

### Using data: connecting data to attributes

Attributes have a mini-language for building up attribute values using variables pulled from the document or conditions which control the output.
| syntax | what |
| --- | --- |
| {var} | embed the text behind the variable into the string |
| [b]other[/b] | embed the stuff between the brackets if the evaluation of the variable b is true |
| [b]true branch[#b]false branch[/b] | embed the stuff between the brackets based of the evaluation of b |

<forest>
    <page uri="/">
        <connection space="my-space" key="my-key">
            <a class="[path-to-boolean]active[#path-to-boolean]inactive[/path-to-boolean]" href="#blah">
            </a>
        </connection>
    </page>
</forest>
```

## Attribute extensions to existing HTML elements

A Guiding philosophy of RxHTML is to minimally extend existing HTML elements with new attributes which bind the HTML tree to a JSON tree

### &lt;tag ... rx:iterate="$path" ... &gt;

The ```rx:iterate``` will iterate the elements of a list/array and scope into each element. This attribute works best when there is a single HTML child of the tag placed, and it will insert a div if there isn't a single child.
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

### &lt;tag ... rx:if="$path" ... &gt;

The ```rx:if``` will test a path for true or the presence of an object. If there is an object, then it will scope into it. 

```html
<div rx:if="active">
    Show this if active.
</div>
```

### &lt;tag ... rx:ifnot="$path" ... &gt;

Similar to ```rx:if```, ```rx:ifnot``` will test the absense of an object or if the value is false.
```html
<div rx:ifnot="active">
    Show this if not active.
</div>
```

### &lt;tag ... rx:else ... &gt;

Within a tag that has *rx:if* or *rx:ifnot*, the *rx:else* indicates that this element should be within the child if the condition on the parent is the opposite specified.
```html
<div rx:if="active">
    Show this if active.
    <span rx:else>Show this if not active</span>
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

### &lt;tag ... rx:case="$value" ... &gt;

Part of ```rx:switch```, this attribute identifies the case that the element belongs too. See ```rx::switch``` for an example.

### &lt;tag ... rx:template="$name" ... &gt;

The children of the element with ```rx:template``` are stored as a fragment and then replaced the the children from the ```<template name=$name>...</template>```. The original children be used within the template via ```<fragment />```

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

### &lt;tag ... rx:scope="$path" ... &gt;

Enter an object assuming it is present. This is a much more efficient, yet risky, ```rx:if```

### &lt;form ... rx:action="$action" ... &gt;

Forms that talk to Adama can use a variety of built-in actions like

| rx:action          | behavior                             |
|--------------------|--------------------------------------|
| adama:sign-in      | sign in as an adama developer        |
| adama:sign-up      | sign up as an adama developer        |
| adama:set-password | change your adama developer password | 
| send:$channel      | send a message                       |
| copy:$path         | merge the form into the viewstate    |
| custom:$verb       | run custom logic                     | 

### &lt;form ... rx:$event="$commands" ... &gt;

| command         | behavior                                                                             |
|-----------------|--------------------------------------------------------------------------------------|
| toggle:$path    | toggle a boolean within the viewstate at the given path                              |
| inc:$path       | increase a numeric value within the viewstate at the given path                      |
| dec:$path       | decrease a numeric value within the viewstate at the given path                      |
| custom:$verb    | run a custom verb                                                                    |
| set:$path=value | set a string to a value within the viewstate at the given path                       |
| raise:$path     | set a boolean to true within the viewstate at the given path                         |
| lower:$path     | set a boolean to true within the viewstate at the given path                         |
| decide:channel  | response with a decision on the given channel pulling (see [decisions](#decisions))  |

### &lt;custom- ... rx:link="$value" ... &gt;

### &lt;tag ... rx:wrap="$value" ... &gt;

### &lt;input ... rx:sync="$path" ... &gt;, &lt;textarea ... rx:sync="$path" ... &gt;, &lt;select ... rx:sync="$path" ... &gt;, oh my

Synchronize the input's value to the view state at the given path.

```html
<input type="text" rx:sync="search_filter" />
```

## Decisions

## Todo
* customdata
* pick
* cases
* transforms
* wrapping
* decisions