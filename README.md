# i4

i will finish this mod or god help me

this used to be archloom but it wasn't working for me, and i'm more interested in neoforge anyway

## on gens

the *gen*eral thing that the `dgen` gradle task does:

* at datagen time i find all classes with a `@FindGen` annotation by scanning `build/classes` (lol)
* i construct every `Gen` object and call `gen` on every `Gen` object
* each gen creates a bunch of *facets*, which represent something to do (a json file to write, a lang entry to add, etc)
* collect them all into one big bucket and pass it around to each facet handler. those take care of actually writing the json files or whatever
* (btw i just call `main` on one of my classes, i don't bootstrap minecraft or use a loader datagen api, so it's nice and quick)

and at runtime:

* one of the things i datagenned was a serviceloader file containing all the `@FindGen` classes, so i can use that to track em down again instead of the hacky build-dir stuff
* i call `rt` instead of `gen`, generating different kinds of facets this time
* i pass it around to a different set of facet handlers. those take care of registering blocks and shit

registration is done with a `Latch` system - you specify the registry type and ID up-front, register by associating a supplier with the latch, and when the object is finally registered it automatically appears in all relevant `Latch`es (provided the registry system has been told about the latches). this is a little better than DeferredRegister since you don't have to specify how to constuct the object right away

and then all the `Gen` and `Facet` shit falls away to get garbage collected. the perfect crime.

## in specific

there are many angles to view a stairs block through: it needs a block with corresponding blockstate and blockmodel, it also needs an item with its own item model, it needs a lang entry, it needs a recipe. traditional datagen systems split this up into one phase per "kind of thing" (generate all the blockstates, then all the blockmodels, then all the item models...) but my system cuts the other way and asks you to consider all angles of the content at the same time.

"facet" is the term for "an angle you might look at a piece of content". facets include adding an item model, adding a lang entry, registering a block, and such. concretely, facets are instances of any class annotated with `@Facet` or a subclass thereof. facets can be stored in a `FacetHolder`, which is just a list of facets grouped by type, and the class with the `@Facet` annotation can be used as a key to look up relevant facets in the facetholder.

this is already enough for a pretty robust datagen system. you rephrase datagen from an "adding all blockstates and all blockmodels and all item models" problem into an "adding a bunch of facets into this big `FacetHolder` bucket" problem. you can throw facets into the bucket in any order you want. this allows you to group everything related to your stairs block on one screen, instead of being divided across ten files. i already think this is a big improvement.

i wanted more, though, so there's an additional layer above this. `Gen` is a subclass of `FacetHolder` introducing the notion of *auto-discovery* (with the `@FindGen` annotation), allowing you to spray datagen code across your mod (even as an inner class of the thing you're generating data for), and the notion of *two phases*, `gen` and `rt` which happen at gen and runtime respectively; meaning not only can the datagen code to be co-located, but also lots of the runtime code.

subclasses of `Gen` form an even-more opinionated layer over `Gen`. you can register whatever you want in a generic `Gen`, but `BlockGen` knows it's "about" registering a specific block, so it asks you to supply a `Latch` (≈ a block id) for the block in question and provides convenience methods prefilling it into relevant facets (autogen a language entry with the appropriate `block.xxx.name` key with `enUs("My Block");`)

by convention `@Facet`s are implemented as mutable "builders", so classes like `BlockGen` can add the facet to the facetholder, return it to you, and let you configure it further without needing a billion constructor parameters.

important notes

* `BlockGen` might be primarily "about" registering a specific block, but you can do whatever you want in it including register other blocks. minecraft mods are defined by their exceptions to the rules and i dont want to get in the way of that
* to that end, it never matters *which* facetholder a facet is in. everything needed to resolve a facet is included in the facet class itself
* minecraft is not bootstrapped during the datagen process, which means you can't easily do stuff like "iterate over all the blocks your mod registers".
  * philosophically, this is because the datagen system is now the source of truth, not whatever happened to be registered into the game
  * practically, this is because minecraft boots slow and i hate waiting on it. from a warm cache my `dgen` task takes about five seconds including compilation time.
  * and if you do need to iterate over all blocks, a good alternative is adding the same facet to all your blocks, and handling it by gathering all of them and doing something with the aggregate.

## will this be made a library

probably not, because i think it works best and most ergonomically when it lives directly in your code and you can hack in little exceptions wherever you want

## shortcomings

* conflating "entrypoint into the gen world" with "gens themselves". the "fanout" system feels like a bandaid
* blocks creating and configuring a secret little item gen feels sketch and not very composable. idk

should gentime/runtime stuff even coexist in the same class???? i think it's a good idea because, physically, you can share any initialization code (and stuff like block ids) and mentally there's only one place to look for each block (does it really matter if the color provider comes from a resourcepack or on game startup?). BUT it can cause problems too, arguably wastes memory at runtime, and a lot of the game is classloading bombs during gentime b/c i dont bootstrap

# ideas for the mod itself

oh right, i have a content mod to make after i finish shaving all these datagen yaks

thesis: basically yoink botania corporea and then add an Incorporeal-like computer system ontop of it

the corporea yoink

* remove network colors and stuff. well you can dye your networks to keep them straight but it's just for you.
* logistic networks are set up with the *entwining wand*. shift-click on the *logistic core* (≈ master corporea spark) and then click on the devices you want to link
  * wand doesn't forget that it's linked to that core and you can whack as much stuff as you want with it.
  * the number of connections to one network is limited. my "i love ae2's channels mechanic" shirt is making people ask a lot of questions already answered by the shirt
  * range is "decent". enough so you feel the burn. maybe some way of expanding it, but it's not free
* *logistic ports* are the equivalent of putting a corporea spark on a chest
  * surely the performance will be better if they're blocks and not entities. Clueless
  * itll work with all adjacent inventories so you get a lil more bang for your buck
* *logistic callers* are the corporea funnel equiv
  * instead of item frames i'll use "request cards". you put a request card in the logistic caller to configure it. by "put in", i mean "it's an inventory"
  * can place in six directions
* the *logistic terminal* is how you manually request things from the network
  * i imagine it will have a little gui
  * you can also manufacture request cards with it
  * sidenote: b/c you'd request things by clicking on items instead of using chat, i don't need the "itemstack request / string request" distinction (which i am MORE than happy to get rid of) 
* *logistic relabelers* are the corporea liar from incorporeal
  * aha now we're getting into the sicko shit
  * basically it's like a logistic port, but you give it a request card and it reports items to the logistic network *as if* they're items matching the request card
  * but it's not a dupe hack because when you extract items you get the real items, even if they're not what you asked for
  * it sounds counterintuitive but it's actually very useful and this is 10000x my favorite block from incorporeal lmao

the computer stuff.. i want to describe it as the best parts of 1, 2, and 3 but its been so long since ive used any of those xd

* good parts of 1: having numbers/items being things you work with separately. also making all the program blocks easily accessible with the scroll wheel 
* worst parts of 1: the data stack being a random floating thing over a spark. looked bad, felt bad.
* good parts of 2: rhododendrite dude. sooooo good. having data exist in the world and push/pop operations having a physical direction is awesome
* worst parts of 2: i tried to merge it so 1 stone + 2 wood = 3 "stone or wood", and i dont think it makes any sense. best to keep items and numbers separate
* good parts of 3: uh, upgrading the "corporea tickets" concept to "item representation of whatever" is nice
* worst parts of 3: everything else

i'd like to make things a little bit polymorphic, like if you add a request to a request it ignores the second request, if you add a number to a request it does what you mean and increases the size of the request

i have a particular vision of the... data-storage blocks (retainers?) in incorporeal1 now being things that hold their nbt when broken, break them with a piston + dispense with a dispenser, so now we have the fully-generic data-on-items thing that i wanted without having to add a new item, at the cost of being redundant with the "request card" because that's a card and not a block. awkward
