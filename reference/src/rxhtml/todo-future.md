# TODO and Future

So, The documentation for RxHTML and the reality have drifted a bunch. This document serves as a refresher of the entire code base for all the caveats that I need to document.

## Attribute Command Language
* Choose
* Custom
* Decide
* Decrement
* Finalize
* Fire
* ForceAuth
* Goto
* Increment
* Manifest*
* Nuke
* Order Toggle
* Reload
* Reset
* Resume
* Set
* Submit
* SubmitById
* Toggle
* TogglePassword
* TransferError
* TransferMouse
* Uncheck (to deprecate)

## Attribute Template Language

* autovar
* concat
* condition
* empty
* lookup
* negate
* operate
* text
* transform

 ## Pre-processing

* static:content
* element: common-page (uri-prefix, static: template, init:, authenticate )
* page's attribute template:use and template:tag

## Pathing
* dive into "path0/path1/path2"
* root "/root"
* parent "../parpath"
* pick/switch "view:" "data:"

## Attributes
* branching: force-hiding
* branching: rx:if, rx:ifnot
* source: boolean mode
* source: decide:
* source: choose:
* source: chosen:
* source: finalize: 
* source: compare mode ($pathL=$pathR)
* future source: eval:
* branching: rx:else / rx:disconnected / rx:failed
* rx:monitor
* rx:behavior
* rx:repeat (solo child)
* rx:iterate (solo child) (rx:expand-view-state)
* rx:switch, rx:case
* rx:wrap (to deprecate)
* rx:custom (big one)
* rx:template
* feature: "merge"

## Scoping
* rx:scope
* rx:expand-view-state

## Attribute Setting
* boolean inputs
* href
* class / src (more to come)
* value (input, input, select, option)
* value (boolean input) OR button + disabled
* option + label

## Events / Commands
* rx:click,
* rx:load
* rx:mouseenter, rx:mouseleave
* rx:blur, rx:focus
* rx:change
* rx:delay:$ms
* rx:rise, rx:fall
* rx:check, rx:uncheck
* rx:keyup, rx:keydown
* rx:settle, rx:settle-once
* rx:ordered
* rx:success, rx:failure
* rx:submit, rx:submitted
* rx:aftersync

## Forms
* rx:identity (should this be just identity)
* rx:action
* send:$channel
* document:authorize
* domain:authorize
* document:sign-in, domain:sign-in, document:sign-in-reset, domain:sign-in-reset (deprecated)
* document:put
* domain:put
* adama:sign-in
* adama:sign-up
* adama:set-password
* dynamic:send
* custom:
* adama:upload-asset (to remove)
* document:upload-asset
* domain:upload-asset
* copy-form:$ID (to deprecate?)
* copy:$path (to deprecate)

## Form Behavior
* rx:forward
* rx:success
* rx:failed
* rx:submit
* rx:submitted
* rx:failure-variable (to deprecate)
* default rx:success, rx:failure (to deprecate)

# Unknown
* rx:link

# Root elements
* template
* page
* shell

# Elements
* fragment / fragment &amp case
* monitor
* view-write
* lookup
* lookup transforms
* lookup refresh
* trustedhtml (like lookup but for HTML)
* exit-gate (guard, set)
* todo-task
* title
* view-state-params
* view-sync (deprecated?)
* connection-status *A*
* connection *A*
* local-storage-poll
* document-get (TODO)
* domain-get
* pick *A*
* custom-data
* input/text-area/select (rx:sync) (TODO, write to multiple places)
* input/text-area/select (rx:debounce)
* sign-out
* inline-template

# RxObject
* parameter:
* search: