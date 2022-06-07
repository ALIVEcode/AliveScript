## AliveScript

Welcome to the AliveScript Programming Language, where dreams come true in all sort of dialect!

## Getting started

1. Get Intellij Idea
2. Clone this repo
3. In File/Project Structure ...
    1. Link src to /src
    2. Link resource to src/interpreteur/regle_et_grammaire
    3. Link dependencies
    4. Set the sdk to Java17
4. Execute the main function in src/Main.java
5. Have fun with the language 🥳🍾

## Now what?

* Go see the [/docs](https://github.com/ALIVEcode/AliveScript/blob/bb82c689f288802a6f7b803d9b80d0c36871f3dd/docs)
  folder to see all the documentation related to AliveScript!

## Folder Structure

The workspace contains three folders by default, where:

- `src`: the folder where alivescript code is written
- `lib`: the folder where you can find the dependencies of alivescript
- `ServerAS`: the module where you can find the Api for communicating with alivescript

## Change Log

* 2.0.0:
    * **ADDED**:
        * `structure`: structure as complex data types (similar to `struct` in C)
        * `structure's related syntax`: structure's related syntax (accessors, constructors, etc.)
        * `custom type`: custom types with the new `type` keyword
    * **REMOVED**:
        * `structure`: structure as namespaces
    * **CHANGED**:
    * **NOT INCLUDED**:
        * `Generic Type`: generic types are not supported yet (they may be implemented in a future version of
          AliveScript)
* 1.3.0:
    * Currently, the latest version of AliveScript used in the ALIVEcode project
    * **ADDED**:
        * Support for dictionaries as a somewhat different type of list (made of pairs)

* 1.0.0:
    * Consolidation of the core features of AliveScript into a stable, usable language.

* <1.0.0:
    * Work in progress
    * Before the release of AliveScript, experimental features were added to the language. 
