# ogu-lang

Ogú is a dynamic programming language.

This is the Plunke edition of this language. See below for details.

## Usage

You will need [Leiningen](https://leiningen.org)

You can run an Ogú script (script.ogu) this way:

    $ lein run -- -e script.ogu
    
You can read about Ogú syntax on the file OGU-0.1-en.md.

## About Name

Ogú is a dynamic programming language.

This is the Plunke edition of this language. See below for details.

## Building Ogu

You will need Java 1.8 and [Leiningen](https://leiningen.org).

You can build Ogú this way:

    $ lein uberjar
    
# Usage
    
You can use Ogu this way
    
    $ java -jar target/ogu-lang-0.1.0-standalone.jar
    
(After lein uberjar)
    
To run an Ogu Script you write it on a file with .ogu extension, and then run with the -e

Inside demos directory are many samples of ogu scripts, for example, to play a simple snake game you can do this:
   
    $ java -jar target/ogu-lang-0.1.0-standalone.jar -e demos/snake.ogu
    
You can read about the Ogú syntax on the file OGU-0.1-en.md.

# About Name

Ogú is a comic character created by chilean illustrator [Themo Lobos](https://en.wikipedia.org/wiki/Themo_Lobos).

## Editions

The language will be released in editions.

These are the future editions:

- Plunke (0.1): first edition using Clojure runtime to interpret scripts written in a subset of the language.

- Ferrilo (0.2)

- Ñeclito (0.3)

- Lokán (0.4)

- Cucufato (0.5)

- Cucalón (0.6)

- Alaraco (0.7)

- Tinalin (0.8)

- Agú (0.9)

- Ogú (1.0)


## License

Copyright © 2011, 2017 Eduardo Díaz Cortés

Distributed under the BSD License, see LICENSE for details.
