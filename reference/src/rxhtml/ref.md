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

### &lt;tag ... rx:if="$path" ... &gt;

### &lt;tag ... rx:ifnot="$path" ... &gt;

### &lt;tag ... rx:else ... &gt;

Within a tag that has *rx:if* or *rx:ifnot*, the *rx:else* indicates that this element should be within the child if the condition on the parent is the opposite specified.

### &lt;tag ... rx:switch="$path" ... &gt;

### &lt;tag ... rx:case="$value" ... &gt;

### &lt;tag ... rx:template="$path" ... &gt;

### &lt;tag ... rx:scope="$path" ... &gt;

### &lt;form ... rx:action="$action" ... &gt;

### &lt;form ... rx:$event="$commands" ... &gt;

| command | description | example |
| --- | --- | --- |
| toggle:$var | - | - |

### &lt;form ... rx:link="$value" ... &gt;

### &lt;input ... rx:sync="$path" ... &gt;, &lt;textarea ... rx:sync="$path" ... &gt;, &lt;select ... rx:sync="$path" ... &gt;, oh my

| rx:action value | description | requirements |
| --- | --- | --- |
| adama:sign-in | - | - |
| adama:sign-up | - | - |
| adama:set-password | - | - |
| send:$channel | - | - |
| copy:$path | Copy data from the form into the view state | - |
| custom: | - | - |

## Todo
* fragment within templates
* customdata
* pick
* cases
* transforms
* wrapping