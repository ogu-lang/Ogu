# ogu-lang-compiler

Ogú es un lenguage de programación dinámico.

Esta es la edición Plunke del lenguaje (ver más abajo.)

## Compilar Ogú

Necesitarás Java 1.8 y [Leiningen](https://leiningen.org).

Para generar el compilador debes hacer:

    $ lein uberjar

Esto dejará un archivo .jar en el directorio `target`.

# Uso

Ogú se ejecuta de la siguiente manera:

    $ java -jar target/ogu-lang-0.1.0-standalone.jar

(Después de compilar con lein uberjar)

Para ejecutar un script Ogú debes escribir un archivo con la extensión .ogu, y luego se ejecuta con la opción -e

Dentro del directorio demos hay varios ejemplos de scripts Ogú, por ejemplo, para jugar un juego simple puedes hacer lo siguiente:

    $ java -jar target/ogu-lang-0.1.0-standalone.jar -e demos/snake.ogu

Puedes leer algunas notas sobre la sintáxis de Ogú en el archivo OGU-0.1-es.md.

# Sobre el nombre

Ogú es un personaje de creado por el ilustarador chileno [Themo Lobos](https://en.wikipedia.org/wiki/Themo_Lobos).

## Ediciones

El lenguaje será liberado en diversas ediciones. Cada edición corresponde al nombre de un personaje creado por Themo Lobos.

Estas son las versiones futuras (esto está sujeto a cambio).

- Plunke (0.1): primera edición usando Clojure y su runtime para interpretar scripts, en un subconjunto del lenguaje.

- Ferrilo (0.2)

- Ñeclito (0.3)

- Bromisnar (0.4)

- Cucufato (0.5)

- Cucalón (0.6)

- Alaraco (0.7)

- Guigá (0.8)

- Agú (0.9)

- Ogú (1.0)

## Licencia

Copyright © 2011, 2017 Eduardo Díaz Cortés

Distribuido bajo licencia BSD, ver el archivo LICENSE para los dealles.
