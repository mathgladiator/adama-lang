# JSON Delta Format

The core algorithm used for deltas is JSON under the merge operation as defined by [RFC 7386](https://datatracker.ietf.org/doc/html/rfc7386) with one big exception around arrays.

Arrays are special because [RFC 7386](https://datatracker.ietf.org/doc/html/rfc7386) does not have a great way to merge arrays when small changes occur within an element. Adama works around this by converting arrays to objects, and then reconstruct the arrays with two hidden 

During the merge, we detect that an object is an array if:
* the prior version is an array
* the object contains an '@o' field
* the object contains an '@s' field.

Beyond arrays, the delta format also supports document based operational transforms which support collaborative text editing (For example, [using Code Mirror 6](https://codemirror.net/)).
The text documents are converted to objects with three special fields: '$s', '$g', and '$i'.

## Handling '@o' field (ordering operational transform)
The '@o' is an instruction to build the array from keys within the object. For instance, if the '@o' contains

```json
['1', 'x', 'y', '2']
```

then the array formed is

```json
  [
    obj['1'],
    obj['x'],
    obj['y'],
    obj['2']
  ] 
```

This allows small changes within elements to occur as '@o' is only transmitted when elements are added, removed, or re-ordered within the array.
However, for small insertions within an array, then the format is rather excessive as it transmit keys.
The '@o' array can leverage a prior version of the object. For example, if a delta contains an '@' with value:

```json
  [[0,3],'z']
```

then the array is formed as
```json
  [
    prior[0],
    prior[1],
    prior[2],
    prior[3],
    obj['z']
  ] 
```

This operational transform allows minimal reconstruction of the array such that insertions happen at any frequency in any place without much cost beyond a full client side reconstruction.

## Handling '@s' (size setter transform)

This is a simpler array construction signal which assumes the keys are integers starting at 0. A '@s' value of 5 would construct an array of

```json
  [
    obj[0],
    obj[1],
    obj[2],
    obj[3],
    obj[4]
  ] 
```

## Handling '$s', '$g', and '$i' keys for operational transform

| field | meaning |
| --- | --- |
| $s | sequencer |
| $g | generation |
| $i | initial value |

The generation of a text field represent a unique lifetime for the document, and changes with '$g' imply a complete change in both '$i' and '$s' signalling a different document.

The initial value is used to initialize the collaborative editor at construction, and the sequencer indicates that changes are available within the document. For example, if the value of '$s' changed from 3 to 5, then the following updates can be handed over to the editor.

```json
changes = [obj[3], obj[4], obj[5]].
```

For more information, please see [code mirror's collaborative editing sample](https://codemirror.net/examples/collab/)
