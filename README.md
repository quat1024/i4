# i4

i will finish this mod or god help me

- archloom in compile-only mode
- no arch api
- neoforge is not implemented at all yet (and `runClient` doesn't work)

## gens

problems with the current system

* conflating "entrypoint into the gen world" with "gens themselves"
  * the "fanout" system is a bad idea
  * for example, 16 colors, you don't want 16 classes
  * for example, blocks creating and configuring an item gen. but this is configurable from the block gen so fanout makes sense there...
* honestly not sure if blocks creating and configuring an item gen is the best idea?
  * bad inheritance story for example.

should gentime/runtime stuff even coexist in the same class???? iiiii think it's a good idea because you can share any initialization code (and stuff like block ids) BUT it can cause problems too, a lot of the game is classloading bombs during gentime (wahey no bootstrap)