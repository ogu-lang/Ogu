# ogu-lang-compiler


Ogú is a dynamic programming language.

## Current Edition

This is the Ferrilo edition of this language. See below for details.

## Building Ogú

You will need Java 1.8, Scala 2.12 and SBT 1.2.8.

You can build Ogú this way:

    $ sbt assembly

This will create the file `target/scala-2.12/ogu-ferrilo-assembly-0.2.5.jar`.

# Usage

You can use Ogu this way

    $ java -jar  target/scala-2.12/ogu-ferrilo-assembly-0.2.5.jar

(After `sbt assembly`)

To run an Ogu Script you write it on a file with .ogu extension, and then passing the name to the .jar.

Inside the demos directory are many samples of ogu scripts, for example, to play a simple snake game you can do this:

    $ java -jar  target/scala-2.12/ogu-ferrilo-assembly-0.2.5.jar demos/snake.ogu
    
## Syntax

You can read about the Ogú syntax on the file OGU-0.2-en.md.

# About the name

Ogú is a comic character created by chilean illustrator [Themo Lobos](https://en.wikipedia.org/wiki/Themo_Lobos).

## Editions

The language will be released in several editions named after a character created by Themo Lobos.

These are the future editions:

- Plunke (0.1): (Deprecated) The first edition using Clojure runtime to interpret scripts written in a subset of the language.

- Ferrilo (0.2): Second edition. It's a rewirte of the parser in Scala. There are many importante changes in syntax. 
This release still depends on clojure runtime 1.10.0.

- Ñeclito (0.3)

- Bromisnar (0.4)

- Cucufato (0.5)

- Cucalón (0.6)

- Alaraco (0.7)

- Guigá (0.8)

- Agú (0.9)

- Ogú (1.0)

## License

Copyright © 2011, 2017 Eduardo Díaz Cortés

Distributed under the BSD License, see LICENSE for details.
