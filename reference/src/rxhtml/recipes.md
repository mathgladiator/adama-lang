# Recipes

## Templates

#### templates.rx.html
```html, template.rx.html
<forest>
  <template name="my-template">
    <h1>My template with content</h1>
    <fragment />
  </template>
</forest>
```

#### pages.rx.html
```html, page.rx.html
<forest>
  <page uri="/product">
    <div rx:template="my-template">
      This is the main content of the page
    </div>
  </page>
</forest>
```

#### Output for /product
```html, /product
<div>
  <h1>My template with content</h1>
  This is the main content of the page
</div>
```

## Navigation

#### main.rx.html
```html, nav.rx.html
<forest>
  <template name="my-nav">
    <ul>
      <li>
          <a href="/product" class="[view:current=product]active[#]inactive[/]">Product</a>
      </li>
      <li>
          <a href="/about" class="[view:current=about]active[#]inactive[/]">About</a>
      </li>
    </ul>
    <fragment />
  </template>
  <page uri="/product">
    <div rx:template="my-nav" rx:load="set:current=product">
      <h1>My Product</h1>
    </div>
  </page>
  <page uri="/about">
    <div rx:template="my-nav" rx:load="set:current=about">
      <h1>About</h1>
    </div>
  </page>
</forest>
```

#### Output for /about
```html, /about
<div>
  <ul>
    <li>
      <a href="/product" class=" inactive ">Product</a>
    </li>
    <li>
      <a href="/about" class=" active ">About</a>
    </li>
  </ul>
  <h1>About Page</h1>
</div>
```


## Multi-level navigation
#### main.rx.html
```html, nav.rx.html
<forest>
  <template name="my-nav">
    <ul>
      <li>
          <a href="/product" class="[view:current=product]active[#]inactive[/]">Product</a>
      </li>
      <li>
          <a href="/about" class="[view:current=about]active[#]inactive[/]">About</a>
          <ul rx:if="view:current=about" force-hiding>
            <li>
                <a href="/about/team" class="[view:navsub=team]active[#]inactive[/]">Team</a>
            </li>
          </ul>
      </li>
    </ul>
    <fragment />
  </template>
  <page uri="/product">
    <div rx:template="my-nav" rx:load="set:current=product">
      <h1>My Product</h1>
    </div>
  </page>
  <page uri="/about">
    <div rx:template="my-nav" rx:load="set:current=about">
      <h1>About</h1>
    </div>
  </page>
  <page uri="/about/team">
    <div rx:template="my-nav" rx:load="set:current=about set:navsub=team">
      <h1>About</h1>
    </div>
  </page>
</forest>
```

#### Output for /product
```html, /product
<div>
  <ul>
    <li>
      <a href="/product" class=" active ">Product</a>
    </li>
    <li>
      <a href="/about" class=" inactive ">About</a>
      <ul force-hiding="true" style="display: none;"></ul>
    </li>
  </ul>
  <h1>About Page</h1>
</div>
```

#### Output for /about/team
```html, /about/team
<div>
  <ul>
    <li>
      <a href="/product" class=" inactive ">Product</a>
    </li>
    <li>
      <a href="/about" class=" active ">About</a>
    </li>
    <ul>
      <li>
        <a href="/about/team" class=" active ">Team</a>
      </li>
    </ul>
  </ul>
  <h1>About Page</h1>
</div>
```

## Modals

## Dropdowns

## Bulk editing
Field sets
