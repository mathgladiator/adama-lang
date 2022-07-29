# Deployment Plan

Since the configuration behind a space is fundamentally code, it is valuable to change it slowly for a variety reasons.
For instance, it is wise to gate a deployment to a small population to test and gather metrics.
It may also be wise to go slow to avoid an expensive bill.

The deployment plan has three root fields: 
* *versions* providing a mapping of id to versions.
* *default* is an id to use when the plan fails to pick an id
* *plan* is a list of rules to pick a version id

## The versions mapping and the default id.

The versions object is a mapping of ids to versions of the script to use. The range of the mapping is either a string or an object. For example,

```json
{
  "versions": {
    "x": "public int x;"
  },
  "default": "x",
  "plan": []
}
```

is a minimal deployment plan with a single version containing a single source script.
The "x" field within "versions" is a string representing adama code to compile, and it is the default version to use.
However, there are more features to building an Adama script, and the "x" field may also be an object.

```json
{
  "versions": {
    "x": {
      "main": "public int x; @include xyz;",
      "includes": {
        "xyz" : "public int y;"
      },
      "rxhtml": "<forest>...</forest>"
    }
  },
  "default": "x",
  "plan": []
}
```

Now the "x" field has an object with a main which is the primary Adama script which may include other scripts which are stored within the "includes" object. Furthermore, [RxHTML](/rxhtml/ref.md) can be included to augment the [web](/guide/web.md) capabilities of Adama.

## Safety happens with planning

Since change is a source of engineering pain, the plan field within a deployment plan is a simple rule engine. Simply, it's a list of objects where each object represents the parameters to a decision to route to a specific version.

For example,

```json
{
  "versions": {
    "x": "public int x;",
    "y": "public int x; public int y;"
  },
  "default": "x",
  "plan": [
    {
      "version": "y",
      "keys": ["1", "2"],
      "percent": 1,
      "prefix": "new",
      "seed": "xyz"
    }
  ]
}
```

The *version* field within a plan's object is simply a key within the *versions* object in the deployment plan. The optional *keys* field is a hard-coded list of which keys must go to the indicated version. If the *percent* is 100 or more and the key shares the key shares the *prefix*, then that key will go to the indicated version. If the *percent* is less than 100 and the key shares the *prefix*, then the given key is hashed with the *seed* as a value between 0 and 100. If the value is less than *percent*, then the key will used the indicated version. The first rule to pick a version wins, and if no rule matches then the default is used. 

The above prose represents the below algorithm

```java
public String pickVersion(String key) {
  for (Stage stage : stages) {
    if (stage.keys != null) {
      if (stage.keys.contains(key)) {
        return stage.version;
      }
    }
    if (key.startsWith(stage.prefix)) {
      if (stage.percent >= 100) {
        return stage.version;
      }
      if (hash(stage.seed, key) <= stage.percent) {
        return stage.version;
      }
    }
  }
  return defaultVersion;
}
```

By fully utilizing the deployment plan, changes can be made exceptionally safe.

