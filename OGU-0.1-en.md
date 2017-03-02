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

Lists and vectors, or sequences in general, are written between brackets:

    [1, 2, 3]
    
    ["a", "b", "c"]
    
Ranges are lists where both ends are defined:

    [1..100] ; 1 to 100, both inclusive
    [1..<100] ; 1 to 99
    
A special way to write a range is to define the step between the elements:
    
    [3, 6..999] ; 3, 6, 9, 12, ... 999

Ranges can be infinite:
    
    [10...]
    
Lists can be written using comprehensions:
    
    [x * y | x <- [100..<1000], y <- [100..<1000] ]
    
If you have a vector, you can access the i-th element like this:

    let v = [100, 200, 300]
    
    (v 1) ; 200
    
# Maps
    
Maps are written between `{}`:
    
    {"nombre" "Peter", "age" 15}
    
There are keywords, that start with `:` and are useful to operate with maps:

    {:name "Peter", :age 15}
       
    
If you have a map, you can access its elements as a function:
    
    let mymap = {"name" "Peter", "age" 15}
    
    let peters-age = mymap "age"
    
Where with a keyword we can do:
    
    let mymap =  {:name  "Pedro", :age  15}
                  
    let peters-age = mymap :edad
    
or
 
    let peters-age = :age mymap
    
The following is an empty map:
   
    {}
    
# Sets
         
A set can be written like the following:
     
     #{element1, element2}
     
The following is an empty set:

    #{}

# Functions

Ogú is mainly a functional language, with a great influence from Haskell.

Let's consider the `max` function, which returns the maximum between two numbers. In Ogú, it can be invoked like this:

    max 10 4

The value returned is 10.

Another way to invoke this function is:

    (max 10 4)

Like is done in Lisp, whihc is useful in contexts where it could be ambiguous.

Let's consider the following cases:

    max 4 5 + 2
    2 + max 4 5
    max 4 + 5 2

How does the Ogú Plunke compiler interpret it?

In the first case, the result is 7. In the second case, it's also 7.
In the third case is 9, as you can expect.

    max 4 5 + 2 ; (max 4 5) + 2 
    2 + max 4 5 ; (2 + (max 4 5))
    max 4 + 5 2 ; (max (4 + 5) 2)
    
The `-p` compiler flag allows you to see the AST (Abstract Syntax Tree) that corresponds to S-Expressions in Clojure, which you can use for debugging.

When in doubt, is good to use parens.

## Calling functions with tuples

Let's assume that there's another function called `max’` which has been defined to receive a two-element tuple as argument.
In this case, to call the function you must use parents and commas to call it.

    max’ (4, 5)

Because this function receives a 2-tuple and returns a value.

The functions that we've seen are declared in Ogú like this:

    def max a b = if a > b then a else b

where the function `max'` is declared like this:

    def max' (a, b) = if a > b then a else b

A function in Ogú is declared with **def**.

Despite similar looks, both functions are evaluated differently.

We can do partial application in the following way:

    let from5 = partial max 5

which defines a partial function which returns 5 or a number greater than 5.

With the function above, we'd have the following:

    from5 3 ; returns 5
    from5 8 ; returns 8

## Partial application

In Ogú you can use application just like in Clojure. There is no support for Currying.

Examples:

    def multiply x y = x * y
    let double  = multiply 2
    let ten  = double 5
    let twelve = double 6

The first case defines a function `multiply` which takes two numbers.

The second case defines a function which returns another function which multiplies its arguments.

In this case, `ten` and `twelve` are functions which always return the same value (the compiler should optimize this to constant values).

## Functions with no arguments

Note that in the previous section we did

    let double = multiply 2
    
Here `double` is a value, a variable bound to a function.
    
If we declared it as a function it would be:
    
    def double' = multiply 2
    
The problem is that `double'` is a function without arguments, and these functions need to be called in parens in Ogú, like this:
    
    def fun = println! "I have no arguments"
    
    (fun)
     
To make this work, we'd need to have the following:

    (doblar) 10 ; this won't work
    
And it still wouldn't work.

There's a way to make this work, but we'll see it later.

Why is it?

It's because in Ogú functions are first class, which means, functions can be passed as arguments to other functions:

    def my-apply f x = f x
    
    my-apply upper "hola" ; "HOLA"

    
## Function declaration

The way to declare a function is the following:

    def nameOfTheFunction args = expression


Examples:

    def factorial n = if n == 0 then 1 else n * factorial (n-1)


This is similar to Haskell, but we add the `def` keyword before the name of the function.

The `def` keyword can be omitted but it's not recommended.

The `def` keyword must be in the first column of a line, with no indentation (if `def` is omitted, the name of the function must go in the first column).

The parameter can be a tuple like in this example:

    def min' (a, b) = if a < b then a else b


Of course, the returned value can also be a tuple:

    def swap'(a, b) = (b,a)


Using tuples allows to do interesting things, like:

    def sum-vectors (a, b) (c, d) = (a + c, b + d)
    
    sum-vectors (10, 10) (20, 20) ; returns (30,30)


Of course, the usual is to declare functions in this way:

    def sum a b = a + b ; remember spaces are significant
    
Then the `sum` function could be inkoked:

    sum 10 20
    
    sum 1.0 2.0 ; error
    
## Functions Pattern Matching

This is a feature borrowed from Haskell, which allows to define functions in a convenient form:

    def factorial 0 = 1
    def factorial 1 = 1
    def factorial n = n * factorial (n - 1)


Another example

    let radioAlfa 'a' = "Alfa"
    let radioAlfa 'b' = "Bravo"
    let radioAlfa 'c' = "Charlie"
    let radioAlfa 'd' = "Delta"

In this case we're defining a function which returns a string for each character using the radiophonic alphabet.

Other examples:
    
    def first (a, _, _) = a

    def second (_,b,_) = b

the symbol `_` denotes that we're not interested in this value.

In these two examples we're created functions to obtain elements out of a 3-tuple.


## Functions on lists

Let's see some examples

    def head' [] = error! "Empty list"
    def head' [x & _] = x

    def length' [] = 0
    def length' [x & xs] = 1 + length' xs

    def tell [] = "empty list"
    def tell [x] = "the list contains one element"
    def tell [x, y] = "the list contains two elements"
    def tell [x, y & _] = "the list is long"


## Guards

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


     
    

    





