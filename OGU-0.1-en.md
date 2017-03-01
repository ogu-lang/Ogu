# The Ogú programming language

## Introduction

This is Ogu version 0.1.x.

This version is also known as Plunke.

## Plunke features

Pluke uses Clojure as infrastructure.

In Plunke, a subset of Ogú is compiled into an intermediate format (EDN) and is interpreted in Clojure.

Plunke uses the Clojure core libraries and its own think layer called ogu.core

The following is a description of what can be done with the subset of Ogú implemented in the Plunke release.

# Comments

In Ogú comments start with a `;` (semicolon) and end with the end of the line.

    ; this is a comment

# Whitespace

In Ogú, whitespace is significant.

For instance,

    1+1 
    
Is an erroneous expression, it must be written
    
    1 + 1
        
This is very important, because

    a+1 
    
is an identifier, but

    a + 1
    
is an expression.

# Constants and Variables

Values can be stored in immutable variables, or constants, which are denoted with the reserved keyword **val**.

    val c = 300000
    
Variables that can be mutated are declared with the reserved keyword **var**.

    var v = 1
    

Examples:

    val maxTries = 10
    var triesSoFar = 0 

# Identifiers

Identifier names can contain letters, the symbols `_`, `-`, `+` and `*`. The can end with the symbols `'`, `!` or `?`.

Examples:

    val lost+found = true
    val max-tres = 10
    val parameters* = 200
    val _is-finalist? = false
    
Their initial value can always be set with the `=` operand.

In Ogu-Plunke there are no types.
    
Dates can be entered with the type `#` followed by the date expressed in a subset of the ISO8601 format.

    val timestamp = #2016-01-19T20:30:01.245
    val zulutime = #2016-01-19T16:40:01.s45Z
    val santiagotime = #2016-01-19T16:40:01.s45-03:00
    
Numbers are expressed in the traditional way, including expoential notation.

    val pi = 3.14.15
    val avogadro = 6.022e23
    val bigint = 20N
    val bigdec = 20M
     
The N suffix designates a BigInteger, that is, an whole number with an arbitrary number of digits.
The M suffix designates a BigDecimal, that is, a number with indefinite precision.

# Tuples

In Ogú tuples are used in various contexts.

For instance, there are functions that return tuples.

In this case, if you want to capture the values returned in the tuple separately the following notation must be used:

    let (p, q) = frac(0.4) ; x = 4, y = 10

(Here we assume `frac(x)` returns a real number as a fraction)

**let** y **val** are synonims when we declare global variables.

# Lists y Vectors

Las listas y vectores, o secuencias en general, se escriben entre corchetes:

    [1, 2, 3]
    
    ["a", "b", "c"]
    
Los rangos corresponden a listas donde se definen los inicios y terminos de una secuencia de números

    [1..100] ; del 1 al 100 inclusive
    [1..<100] ; del 1 al 99
    
Una forma especial de escribir un rango es defininiendo el paso entre los elementos>
    
    [3, 6..999] ; 3, 6, 9, 12, ... 999

Los rangos pueden ser infinitos:
    
    [10...]
    
Las listas se pueden escribir por comprension:
    
    [x * y | x <- [100..<1000], y <- [100..<1000] ]
    
Si tienes un vector puedes acceder al elemento i-esimo del siguiente modo:

    let v = [100, 200, 300]
    
    (v 1) ; 200
    
# Mapas
    
Los mapas se escriben entre {}:
    
    {"nombre" "Pedro", "edad" 15}
    
Existen keywords, que empiezan con : y son utiles para operar con mapa:

    {:nombre "Pedro", :edad 15}
       
    
Si tienes un mapa, puedes acceder a sus elementos como una función
    
    let mapa = {"nombre" "Pedro", "edad" 15}
    
    let edad-de-pedro = mapa "edad"
    
Con un keyword podemos hacer:
    
    let mapa =  {:nombre  "Pedro", :edad  15}
                  
    let edad-de-pedro = mapa :edad
    
o
 
    let edad-de-pedro = :edad mapa
    
    
Un mapa vacio se designa así
   
    {}
    
# Conjuntos
         
Un conjunto se designa asi:
     
     #{elemento1, elemento2}
     
Un conjunto vacio se designa así:

    #{}

# Funciones

Ogú es un lenguaje principalmente funcional, con gran influencia de Haskell. 

Consideremos la función max, que entrega el máximo entre dos número, en Ogú se puede invocar de la siguiente manera:

    max 10 4

El valor de retorno es 10. 

Otra manera de invocar esta función es:

    (max 10 4)

Como se hace en Lisp, esto es útil en contextos donde puede haber ambigüedad.

Consideremos los siguientes casos

    max 4 5 + 2
    2 + max 4 5
    max 4 + 5 2

¿Cómo interpreta esto el compilador Plunke de Ogú?

En el primer caso el resultado es 7. En el segundo caso también es 7. 
En el tercer caso es 9, tal como se puede esperar.

    max 4 5 + 2 ; (max 4 5) + 2 
    2 + max 4 5 ; (2 + (max 4 5))
    max 4 + 5 2 ; (max (4 + 5) 2)
    
    
La opcion -p del compilador permite ver el AST (Abstract Syntax Tree) que corresponde a S-Expressions en Clojure, con lo que puedes depurar si tienes dudas.

Ante la duda es bueno usar parentesis.

## Invocando funciones con tuplas

Supongamos ahora que existe otra función que llamaremos max’ que en este caso ha sido definida para recibir una tupla de dos elementos (dupla). 
En este caso para invocarla se deben usar paréntesis y comas en su invocación.

    max’ (4, 5)

Porque esta función recibe una dupla y retorna un valor.

Las funciones que hemos visto se declaran en Ogú de la siguiente manera:

    def max a b = if a > b then a else b

en cambio la función max’ se declara en Ogú de esta manera

    def max' (a, b) = if a > b then a else b


Una funciónn en Ogú se declara con **def**.

Aunque parecen similares, las dos funciones se evalúan de manera diferente. 

Podemos hacer aplicaciones parciales del siguiente modo:

    let from5 = partial max 5

define una función parcial que retorna 5 o cualquier número mayor que 5.

Con lo anterior tendremos lo siguiente:

    from5 3 ; retorna 5
    from5 8 ; retorna 8

## Aplicaciones parciales

En Ogú se puede usar aplicaciones igual que en Clojure. No hay soporte de Currying.


Ejemplos:

    def multiplicar x y = x * y
    let doblar  = multiplicar 2
    let diez  = doblar 5
    let doce = double 6

El primer caso define una función multiply, que recibe dos números.

En segundo caso define una función que retorna otra función que multiplica por dos sus argumentos.

De este modo diez y doce son funciones que retornan el mismo valor (el compilador debería optimizar esto a valores fijos).

## Funciones sin parametros

Notar que en la seciónn anterior hicimos lo siguiente

    let doblar  = multiplicar 2
    
Acá doblar es un valor, una variable ligada a una función.
    
Si la declararamos como una función sería así:
    
    def doblar' = multiplicar 2
    
El problema es que doblar' es una función sin argumentos, las funciones sin argumentos en Ogú deben ser invocadas entre parentesis, así:
    
    def fun = println! "no tengo argumentos"
    
    (fun)
     
     
Por lo que te tendriamos que hacer

    (doblar) 10 ; esto no funciona
    
    
Y aun asi no funcionaria.

Hay una forma de lograr que funcione, pero la veremos más adelante.

¿Por qué es esto?

Porque en Ogú las clases son objetos de primera clase, es decir, las funciones pueden ser pasadas como argumentos a otras funciones:

    def my-apply f x = f x
    
    my-apply upper "hola" ; "HOLA"
    
## Declaración de funciones

La forma de declarar una función es la siguiente

    def nombreDeLaFuncion args = expresión

Ejemplos:


    def factorial n = if n == 0 then 1 else n * factorial (n-1)

Esto es similar a Haskell, pero agregamos la palabra def antes del nombre de la función.

El def se puede omitir, pero no se recomienda.

El def debe ir en la primera columna de una linea, sin indentación (si se omite el def, el nombre de la función debe ir en la primera columna).


El parámetro puede ser una tupla como en este ejemplo:

    def min' (a, b) = if a < b then a else b

Por supuesto el valor de retorno puede también ser una tupla:

    def swap'(a, b) = (b,a)


El uso de tuplas  permite hacer cosas interesantes como lo siguiente:

    def sumar-vectores (a, b) (c, d) = (a + c, b + d)
    
    sumar-ivectores (10, 10) (20, 20) ; produce (30,30)

Por supuesto lo habitual es declarar las funciones de este modo:

    def sumar a b = a + b ; recordar los espacios
    
Con esto la función sumar se puede invocar:

    sumar 10 20
    
    sumar 1.0 2.0 ; error
    

## Pattern Matching de Funciones

Esta es una característica tomada de Haskell, que permite definir funciones de manera bastante conveniente:

    def factorial 0 = 1
    def factorial 1 = 1
    def factorial n = n * factorial (n - 1)


Otro ejemplo:

    let radioAlfa ‘a’ = “Alfa”
    let radioAlfa ‘b’ = “Bravo”
    let radioAlfa ‘c’ = “Charlie”
    let radioAlfa ‘d’ = “Delta”

En este caso estamos definiendo una función que retorna un string por cada carácter usando el alfabeto radiofónico.


Otros ejemplos:

    
    def first (a, _, _) = a

    def second (_,b,_) = b

el símbolo _ indica que no nos interesa el valor. 

En estos dos ejemplos hemos creado funciones para obtener elementos de una 3-tupla.


## Funciones con listas 

Veamos algunos ejemplos:

    def head’ [] = error! “Lista vacía”
    def head’ [x & _] = x

    def length’ [] = 0
    def length’ [x & xs] = 1 + length’ xs

    def tell [] = “lista vacía”
    def tell [x] = “la lista tiene un elemento “ 
    def tell [x, y] = “la lista tiene dos elementos:“ 
    def tell [x, y & _]= “la lista es larga."


## Guardias

A veces una función se puede expresar mejor en base a varias condiciones que deben cumplirse.

Por ejemplo, supongamos que queremos una función que nos clasifique según nuestro indice de masa corporal (imc).

    def strIMC imc
      | imc <= 18.5 = "estas bajo el peso normal"
      | imc <= 25.0 = "tu peso es normal"
      | imc <= 30.0 = "estas con sobrepeso"
      | otherwise   = "estas obeso, cuidado!"

A diferencia del pattern matching, que sólo permite valores o formas de una expresión, los guardias permiten expresiones booleanas.
En este caso los guardias se separan por una barra vertical | y están antes del cuerpo de la función.

Otro ejemplo, en este caso calculamos el IMC en base a la estatura y el peso.

    def strIMC’ peso altura 
        | peso / altura ^ 2 <= 18.5 = “estas bajo el peso normal”
        | peso / altura ^ 2 <= 25.0 = “tu peso es normal”
        | peso / altura ^ 2 <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”


## **where** 

La función anterior calcula una y otra vez el IMC. Podemos simplificar esto usando  **where** :

    def strIMC’ peso altura 
        | imc <= 18.5 = “estas bajo el peso normal”
        | imc <= 25.0 = “tu peso es normal”
        | imc <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where imc = peso / altura ^ 2

Si queremos documentar un poco más esta función podemos hacer lo siguiente

    def strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where 
          imc = peso / altura ^ 2 
          delgado = 18.5
          normal = 25.0
          gordo = 30.0

Una forma más compacta es:

    def strIMC''' peso altura
      | imc <= delgado = "estas bajo el peso normal"
      | imc <= normal = "tu peso es normal"
      | imc <= gordo = "estas con sobrepeso"
      | otherwise = "estas obeso, cuidado!"
      where imc = peso / altura ^ 2
            (delgado, normal, gordo) = (18.5, 25.0, 30.0)

La cláusula **where**  después del cuerpo de una función permite definir variables o funciones. 

Notar que se deben indentar tanto los guards como las declaraciones en el where.

Veamos otro ejemplo:

    
    def calcIMCs lista = [imc p a | (p, a) <- lista]
       where imc peso altura = peso / altura ^ 2

Esta función recibe una lista de duplas con pesos y alturas retornando una lista de los indices de masa corporal respectivos.

(Notar que se parece mucho a Haskell)

La notación [imc p a | (p, a) <- xs] indica que se debe armar una lista por comprensión, donde cada elemento de la lista corresponde la aplicación de
la función imc para cada parámetro p y a, donde p y a son los elementos de la dupla en xs. 

El operador <- toma cada uno de los elementos de la lista. 

## Cuerpo de la función

Hasta ahora hemos visto sólo casos en que la función consiste en una expresión simple. 
También hemos visto como usar guardias y pattern matching.
Pero, ¿qué pasa cuando las funciones son más complejas, con varias expresiones?

Consideremos la función minmax, que retorna una dupla con los valores máximos y mínimos de una lista.

    
    
    def minmax [] = println! "debe contener al menos un elemento"
    
    def minmax xs =
        var cmin = head xs
            cmax = head xs
        in begin
           for x <- tail xs do
           begin
              when x < @cmin do @cmin = x
              when x > @cmax do @cmax = x
           end
           (@cmin, @cmax)
         end
    
    println! $ minmax [10, 20, 4, 5, 9, 8, 80, 100, 23, 32]
    
Esta es una implementación imperativa de este problema. 
No es la mejor manera de implementar esta solución en Ogú. 
Pero sirve para introducir varios conceptos.

Lo primero, cuando hay más de una expresión se colocan en un bloque, el que se distingue por empezar con **begin** y terminar con **end**.

Cada expresión va en una linea.

Cuando un bloque corresponde al cuerpo de una función entonces el valor de la función será la última expresión del bloque.

La sentencia *when* se usa porque en Ogú un **if** es una expresión que requiere siempre un *else*. 

En cambio when permite ejecutar una sentencia cuando su expresión condicional es verdadera. 

La elección de when y su sintáxis es para hacer el código más "feo", con el fin de impulsar un estilo más funcional.

En general las sentencias que tienen do son imperativas y rompen el paradigma funcional.

La forma de when es 

    when expresión do (expresión o bloque de expresiones)

El loop for es es bastante simple de entender, lo explicaremos en más detalle más adelante.
También es imperativo.

Las expresiones imperativas tienen valor nil, esto es importante.

La estructura **var** **in** permite declarar variables locales. La forma es:

    var
       v1 = expr1
       v2 = expr2
       ...
    in
       expr
       
Las variables deben ser referenciadas con el operador @. Y para cambiar el valor usamos el operador =.

No se puede usar el valor de las variables fuera del in (incluso cuando se están declarando).


# Valores booleanos en Ogú


Los valores false y true son valores reservados para representar booleanos.

El valor **nil** también es especial. En una expresión booleana el valor nil es equivalente a false.


# Recursividad

Para implementar tail recursion usamos recur:


    def siracusa n
        | n == 1 = 4
        | n == 2 = 1
        | n % 2 == 0 = recur (n / 2)
        | otherwise = recur (n * 3 + 1)


Existe una construcción **similar** a la implementada en Clojure:

    def rev num =
        loop  reversed = 0, n = num in
            if zero? then
                reversed
            else
                repeat reversed * 10 + n % 10, int (n / 10)

Loop inicializa las variables, cuando invocas repeat haces una llamada recursiva
al loop con nuevos valores para las variables.

Hay dos diferencias con el loop de Clojure:

1. se itera con repeat, no con recur
2. puedes nombrar a las variables nuevamente en el repeat, peo puedes capturar su valor temporalmente
    
    
    loop i = 1, salida = 0 in
        if i == 10 then salida
        else repeat i' = inc i, salida = i' * 2
        
    ; salida es 20, si no usaramos i' el resultado seria 18
        
        
# Types

Hay dos tipos en Ogú, las clases y los records.

Una clase se define así:

    type Circle (x, y, radius)
    
    type Rectangle( x,  y, width, height)

Un record se define así:

    type Car {company, model, year}

La diferencia son las llaves. Pero una clase puede tener campos mutables, como veremos más adelante.

Se usan de la siguiente manera:


    let mustang56 = Car {company = "Ford", model = "Mustang", year = 1956}
    
    let cir = Circle(10, 10, 10)

Los records son útiles para modelar entidades del dominio del negocio.
Las clases son usadas de manera preferente para implementar tipos de datos más estructurales.

Los campos de un record o de una clase se acceden como funciones aplicadas sobre la instancia, 
llevan el nombre del campo precedido de un punto, por ejemploÑ

    .company mustang56 ; "Ford"
    
Hay una notación especial para acceder a un campo:

    !mustang56.company 
   
    
# Traits 

Un trait es como los protocolos de Clojure.

    
    trait Shape is

        def area self
        
    trait Vehicle is

        def move this    

Los traits definen listas de funciones que son soportadas por el trait.

Una clase o un record pueden implementar un trait 


    type Circle (x, y, radius)
         as Shape
         def area self = pi * (radius ^ 2)

    type Car {company, model, year}
      as Vehicle
         def move this = println! "moving car " company model year
         
Notar que cuando implementamos un metodo de un trait podemos acceder a los campos de la clase, 
como en el caso del metodo self.
Otra cosa que es obligatorio tener un parametro que corresponde al objeto.
Podriamos haber reescrito type Circle del siguiente modo:

    type Circle (x, y, radius)
         as Shape
         def area self = pi * (!self.radius ^ 2)
         
Como el argumento que representa a la instancia del objeto se puede ignorar podemos escribir area del siguiente modo:
         
    type Circle (x, y, radius)
         as Shape
         def area _ = pi * (radius ^ 2)  
         
El primer argumento de un metodo trait puede llamarse como quiera el programador, por convencion se le llama self o this.
         
Una vez que tenemos definido un trait podemos extender un tipo que ya existe del siguiente modo:

    extend Rectangle
        as Shape
    
        def area self = (.width self) * (.height self)

(Notar la indentación)

Cuando extendemos un tipo no podemos acceder a sus campos directamente. Por eso usamos .width self.


## Clases mutables

La mutabilidad es algo no muy deseable en Ogú, es por esto que los records no pueden tener campos mutables, 
su valor se mantiene inmutable durante la ejecución del programa.

Sin embargo, las clases sí pueden tener mutabilidad, declarando los campos con el atributo **var**.

Veamos un ejemplo:


    trait Shape is
    
        def area self
    
    trait Widget is
    
         def draw! self
    
         def move! self x y
         
    type Circle (var x,  var y, val radius)

       as Shape

          def area self = pi * (!self.radius ^ 2)

       as Widget

          def draw! self = println! "draw a circle at (" x ", " y ") with radius " radius

          def move! self new-x new-y = begin
            !x = new-x
            !y = new-y
            draw! self
          end

Notar como x e y son declaradas mutables al colocar el atributo var.
Sin embargo, como no queremos que radius varie, lo declaramos inmutable con val.

Dado esto podemos, dentro de la definición de la clase, modificar el valor de x e y, con la notación

    !x = new-x
    !y = new-y
    
    
Hay un costo para esto, x e y no son visibles furera de la clase.

Esto obliga a definir un protocolo para poder acceder a sus valores, para esto debemos hacer lo siguiente:


    trait Shape is

        def area self

    trait Widget is

        def draw! self

        def move! self x y

    trait Origin is

        def getX self
        def getY self

    type Circle (var x,  var y, val radius)

        as Shape

           def area self = pi * (!self.radius ^ 2)

        as Widget

           def draw! self = println! "draw a circle at (" x ", " y ") with radius " radius

           def move! self new-x new-y = begin
                !x = new-x
                !y = new-y
                draw! self
           end

        as Origin

           def getX self = x

           def getY self = y


# Polimorfismo

Podemos crear funciones polimórficas que nos permiten operar con distintos tipos de la siguiente manera:

    def show-area! shape : Rectangle = println! "el area de un rectangulo es " (area shape) " y es de tipo " (typeof shape )

    def show-area! shape : Shape = println! "el area es " (area shape) " y es de tipo " (typeof shape )


# Despacho dinamico

El dispacho dinámico es una forma de ejecutar un metodo en base a un discriminador, este corresponde a una función.

Por ejemplo,


    dispatch greeting on \x -> (x "language")\
   
    def greeting "French" ? person = println "Bonjour" (person "name")
   
    def greeting "English" ? person = println "Hello" (person "name")
   
    def greeting "Spanish" ? person = println "Hola" (person "name")
   
    def greeting otherwise ?  _ = println "?????"
   
    greeting  {"name" "Michelle", "language" "French"} ; Bonjour Michell
   
    greeting  {"name" "Pedro", "language" "Spanish"} ; Hola Pedro
   
    greeting {"name" "Hans", "language" "German"} ; ?????


Acá cada método se invoca dependiendo del resultado de la expresión lambda.
do sin el parámetro self dentro de la clase.

## Herencia de clases

Ogú no tiene herencia de clases.

# Módulos

Las clases, tipos y funciones se pueden declarar dentro de un módulo,
usando **module**:


    module Collections 
       

Los modulos se importan con la palabra reservada **requires** de una forma 
muy parecida a Clojure:

    module Demo
        require clojure.stacktrace, clojure.java.io as io, clojure.stacktrace refer all, clojure.string refer [upper-case]
        import java.util Date GregorianCalendar


**require** se usa para importar otros modulos escrintos en Ogu o Clojure.


**import** permite importar clases de la JVM.

**import static** es una operación adicional que sirve para importar
definiciones estáticas de la JVM.



    module snake-game
        import java.awt Color Dimension,
         javax.swing JPanel JFrame Timer JOptionPane,
         java.awt.event ActionListener KeyListener

    import static java.awt.event.KeyEvent (VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN)

Para desambiguar clases o tipos uno puede usar la notación modulo.Tipo


     
    

    





