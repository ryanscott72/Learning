# Abstract Factory

## Benefits

* Ensures product compatibility (all Windows or all Mac)
* Isolates concrete classes from client code
* Easy to swap entire product families
* Supports Open/Closed Principle
* Memory efficient (only one factory instance needed)
* Global access point for factories

## When to use

* System needs to be independent of how products are created
* System should work with multiple families of related products
* You want to enforce that products from one family are used together
* You want to hide product implementations from clients
* Factories are stateless and don't need multiple instances