![Skat Game Logo](https://raw.githubusercontent.com/quinscape/skat/master/src/main/webapp/media/logo.png)
# Skat Cards Game

Another domainQL example application.

Interesting Aspects:

## SVG graphics

The game is implemented with SVG graphics created by react. 

### Symbol-loader

The [loadSymbols module](https://github.com/quinscape/skat/blob/master/src/main/js/util/loadSymbols.js) loads a map 
of symbol objects from a [SVG created in Inkscape](https://raw.githubusercontent.com/quinscape/skat/master/src/media/deck4.svg).

The inkscape produced with the help of the [apply transforms plugin](https://inkscape.org/de/~Klowner/%E2%98%85apply-transforms) is cleaned
up and the symbol snippets are identified by their special id attribute in the format `symbol:<name>` each named symbol has
its own symbol entry in the result map. The map entries contain cleaned-up SVG-snippets that are directly
fed to dangerouslySetInnerHTML and wrapped in a group that takes into account the original position in the symbol document so that
the symbol is centered around origin.

```js
    const exampleResult = {
        symbolName: {
            // SVG snippet readdy for dangerouslySetInnerHTML
            snippet: { __html : "<g>...</g>"  },
            // Axis-aligned bounding box of the original symbol
            aabb: { x, y, width, height},
            // parent transform chain
            transform: "..."
        }
    };

```

### SVG layout

The SVG graphics are drawn into a fixed 1000 x 1000 sized square table area that gets centered in the current view-port.
The SVG layout is automatically scaled to the complete screen height if both columns are on screen.

Otherwise it just uses a square playing field in device screen width and the sidebar below that, very boostrappy.

### SVG animation

The cards are JavaScript-animated due to the complications of combining SVG and css transformations and the 
time-staggered display etc.

The [GameCards component](https://github.com/quinscape/skat/blob/master/src/main/js/components/game/GameCards.js) acts 
as animation container.
It enters animation mode on a detecting the "dealer starts dealing" condition. It remembers the start time and calling 
requestAnimationFrame() to refresh the container component drawing the current state of the animation at current time 
delta. 

The cards are following quadratic bezier curves with a common starting point, the final spread-out position and a random
control point. The position along those paths is eased with a 
[common easing function](https://github.com/quinscape/skat/blob/master/src/main/js/util/easing.js).

## Hybrid DomainQL / Spring websocket comm

The game actions happen via GraphQL mutations, but they don't return any results. Instead each mutation causes a delta-update
of the current channel with websocket ( redux state push via websocket implementation )

## Mostly in-memory DomainQL game/logic

Main game mechanisms just happen in memory. Game synchronizes on the in-memory channel objects and provides delta-copies of the 
current channel. There is a early-JSON generation feature to make the JSON generation happen within the synchronized block but 
to release the lock before sending out the responses.   

This also demonstrates how to have different JSON views on a common secret in-memory store. The current game contains data in-memory
that is only available to all users if the game is finished. Otherwise they receive the public parts of the game round data and
a user-specfic [SkatHand via custom fetcher](https://github.com/quinscape/skat/blob/master/src/main/java/de/quinscape/domainql/skat/runtime/game/HandFetcher.java)).
 
