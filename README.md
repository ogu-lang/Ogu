# ogu-lang-compiler

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cf005c7ae37145a2b076c52a647a7957)](https://app.codacy.com/app/ediaz/Ogu?utm_source=github.com&utm_medium=referral&utm_content=ogu-lang/Ogu&utm_campaign=Badge_Grade_Dashboard)

Ogú is a dynamic programming language.

This is the Plunke edition of this language. See below for details.

## Building Ogú

You will need Java 1.8 and [Leiningen](https://leiningen.org).

You can build Ogú this way:

    $ lein uberjar

This will create a .jar file in the `target` folder.

# Usage

You can use Ogu this way

$ java -jar target/ogu-lang-0.1.0-standalone.jar

(After lein uberjar)

To run an Ogu Script you write it on a file with .ogu extension, and then run with the -e option

Inside the demos directory are many samples of ogu scripts, for example, to play a simple snake game you can do this:

$ java -jar target/ogu-lang-0.1.0-standalone.jar -e demos/snake.ogu

You can read about the Ogú syntax on the file OGU-0.1-en.md.

# About the name

Ogú is a comic character created by chilean illustrator [Themo Lobos](https://en.wikipedia.org/wiki/Themo_Lobos).

## Editions

The language will be released in several editions named after a character created by Themo Lobos.

These are the future editions:

- Plunke (0.1): the first edition using Clojure runtime to interpret scripts written in a subset of the language.

- Ferrilo (0.2)

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
