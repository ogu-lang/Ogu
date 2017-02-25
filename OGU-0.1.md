# El lenguaje de programación Ogu

## Introducción

Esta es la versión 0.1.x de Ogú. 
Esta versión se conoce también como Plunke.

## Características de Plunke

Plunke usa Clojure como infraestructura.

En Plunke un subconjunto de Ogú es compilado a un formato intermedio (EDN) y es interpretado por Clojure.

Plunke usa la biblioteca core de Clojure y una delgada capa propia llamada ogu.core.

A continuación una descripción de lo que se puede hacer con el subconjunto de Ogú implementado por Plunke.

# Comentarios

En Ogú los comentarios empiezan con '--' (doblre guión) y terminan con el fin de linea

    -- este es un comentario

# Espacios en blanco

En Ogú los espacios en blanco son relevantes.

Por ejemplo, 

    1+1 
    
Es una expresión errónea, debe escribirse
    
    1 + 1
        
Esto es muy importante, porque 

    a+1 
    
Es un identificador, en cambio

    a + 1
    
Es una expressión.

# Constantes y Variables

Los valores se pueden almacenar en variables inmutables, o constantes, que se indican con la palabra reservada **val**.

    val c = 300000
    
Las variables mutables se declaran con la palabra reservada **var**. 

    var v = 1
    

Ejemplos:

    val  maxIntentos = 10
    var intentosHastaAhora  = 0 

# Identificadores

Los nombres de los identificacdores pueden contener letras, los simbolos _,-,+ y *. Pueden finalizar con los simbolos ', ! o ?.

Ejemplos:

    val lost+found = true
    val max-intentos = 10
    val parametros* = 200
    val _es-finalista? = false
    
Siempre se debe colocar su valor inicial usando el operador =.

En Ogu-Plunke no hay tipos.
    
Las fechas se pueden ingresar usando el símbolo #  seguido de la fecha expresada en un subconjunto del formato ISO8601.

    val timestamp = #2016-01-19T20:30:01.245
    val horazulu = #2016-01-19T16:40:01.s45Z
    val horasantiago = #2016-01-19T16:40:01.s45-03:00
    
Los números se expresan de la manera tradicional, incluyendo notación exponencial.

    val pi = 3.14.15
    val avogadro = 6.022e23
    val bigint = 20N
    val bigdec = 20M
     
El sufijo N indica un BigInteger, es decir, un numero entero con cantidad arbitraria de dígitos.
El sufijo M indica un BigDecimal, es decir, un numero de precision indefinida.



En Ogú las tuplas son usadas en varios contextos. 

Por ejemplo, hay funciones que retornan tuplas. 

En ese caso si se quiere rescatar los valores de retorno de la tupla en forma separada se debe usar la siguiente notación:

    let (p, q) = frac(0.4) -- x = 4, y = 10

(Acá suponemos que frac(x) retorna un número real como una fracción)

**let** y **val** son sinonimos cuando declaramos variables globales.



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

    max 4 5 + 2 -- (max 4 5) + 2 
    2 + max 4 5 -- (2 + (max 4 5))
    max 4 + 5 2 -- (max (4 + 5) 2)
    
    
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

    from5 3 -- retorna 5
    from5 8 -- retorna 8

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

    (doblar) 10 -- esto no funciona
    
    
Y aun asi no funcionaria.

Hay una forma de lograr que funcione, pero la veremos más adelante.

¿Por qué es esto?

Porque en Ogú las clases son objetos de primera clase, es decir, las funciones pueden ser pasadas como argumentos a otras funciones:

    def my-apply f x = f x
    
    my-apply upper "hola" -- "HOLA"
    
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
    
    sumar-ivectores (10, 10) (20, 20) -- produce (30,30)

Por supuesto lo habitual es declarar las funciones de este modo:

    def sumar a b = a + b -- recordar los espacios
    
Con esto la función sumar se puede invocar:

    sumar 10 20
    
    sumar 1.0 2.0 -- error
    

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



# Types

Hay dos tipos en Ogú, las clases y los records.

Una clase se define así:

    type Circle (x, y, radius)
    
    type Rectangle( x,  y, width, height)

Un record se define así:

    type Car {company, model, year}

La diferencia son las llaves. Pero una clase puede tener campos mutables, como veremos más adelante.



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
         
         
Una vez que tenemos definido un trait podemos extender un tipo que ya existe del siguiente modo:

    extend Rectangle
        as Shape
    
        def area self = (.width self) * (.height self)

(Notar la indentación)

Esto permite definir "protocolo" para tipos, es una forma de herencia más flexible que la Interfaces de java o que los 
traits de Scala.

# Tipos algebraicos


    data Shape = Circle Float Float Float Float | Rectangle Float Float Float Float

Esto define una clase algebraica Shape.
La clase Shape puede ser un Circle o un Rectangle.
(Esto es conoce como sum types).

Con esto podemos hacer

    val circle = Circle 10 20 5
    val rect   = Rectangle 50 230 60 90



## Clases


En Ogú se pueden crear clases

    mut class Persona (val nombre : String, var edad : Int)

Esto define una clase mutable con dos variables de instancia, nombre y edad.

Si la clase declara uno de sus campos como var, debe declararse como mut class.

Una clase inmutable se declara así:

    class Persona(nombre:String, edad:Int)

Si declaras una clase como mutable, la palabra reservada 'var' es redundante:

    mut class Persona (nombre:String, var edad:Int) ;; var edad es redundante
    
Si declara una clase como inmutable, la palabra reservada 'val' es redundante:

    class Persona(nombre:String, val edad:Int) ;; val edad es redundante
    
Pero **val** es útil si queremos que un atributo sea inmutable dentro de una clase mutable:

    mut class Persona(val nombre:String, edad:Int) ;; no se puede cambiar el nombre
    

Otros ejemplos:

    class Circulo(val x, y: Int; val radius : Float)

    class Rectangulo(val x,y, alto, ancho:Int)

Para crear un elemento de estas clases se invoca su constructor

    var circulo := Circulo(0,0, 100.0)
    var cuadro = Rectangulo(0, 0, 50, 50)
    var box : Circulo = Circulo(0, 0, 50, 50)


La sintaxis presentada permite crear una clase con variables de instancia que estarán en su constructor.

Se pueden agregar más atributos a una clase que no necesariamente son inicializados en el constructor

    mut class Empresa (val rut:String, val razonSocial:String) where 
        var cantidadEmpleados : Int = 0
        var patrimonioInicial : Money = 1000000

Una clase puede tener métodos, los que se declaran del siguiente modo:

    mut class Auto(val modelo:String, año:Int) where 
        var bencina = 0
        var kilometraje = 0
        
        let kilometrosPorLitro = 12
        
        let avanzar! self kms = 
           set kilometraje self = (kilometraje self)  + kms
           $bencina <- $bencina - (kms/$kilometrosPorLitro) 

           
Los métodos se invocan del siguiente modo:

    var auto = Auto("Ford", 2015)
    
    avanzar! auto 100
    
    set kilometraje auto = (kilometraje auto) + 200
    
    println "el kilometrake de tu auto es: " ++ (kilometraje auto) ;; imprime 300
    
    
Las variables de instancia de una clase se pueden obtener así:
        
     variable objeto
     
Para modificarlas (sólo si son mutables):
     
     set variable objeto = valor
     
Un método se invoca así:
     
     metodo objeto args...
     

La variable especial self puede ser usada cuando se define un método.

No es necesario definir métodos como los vimos recién, puesto que Ogú es funcional, podríamos haber
definido avanzar del siguiente modo:

    def avanzar! : Auto -> ()
    
    let avanzar! auto kms =
       set kilometraje auto = (kilometraje auto) + kms
       set bencina auto = (bencina auto) - (kms/(kilometrosPorLitro auto)) 


La ventaja es que al definirlo en clase podemos usar la notación $variable.

    $variable es igual a decir (variable self)
    
Sólo se puede usar cuando estamos definiendo un método.

En vez de hacer
    set variable = valor
    set variable objeto = valor
    
Podemos usar la notación <-

    variable <- valor
    variable objeto <- valor
    
Esto sólo sirve para variables mutables.
    
   
Una clase en Ogú es una forma conveniente de combinar un tipo record (declarado con data), un trait y una instance.  
    
En Ogú los métodos son funciones que se restringen a la clase y que siempre reciben un parámetro (self).
Se invocan como cualquier función, en Ogú no existe la notación objeto . metodo.

Veamos otro ejemplo:

    class Stack() where
    
        var _data : [Int] = []

        let push! self x:Int = 
            $_data <- x :: $_data

        let pop! self = 
            val result = head $_data
            $_data <- tail $_data
            result

        let empty? self = 
            empty? $_data
    
    
     var stack = Stack()
     push! stack 10
     push! stack 20
     pop! stack ;; <- retorna 20


Sin clases tendríamos que haber hecho lo siguiente:

    data Stack = StackData { _data : [Int] }
    
    trait Stackable t where
    
        def push! : t -> Int -> ()
        def pop! : t -> Int
        def empty? : t -> Boolean
        
    instance Stackable Stack where
    
        val _self : Stack
        
        let push! _self x:Int = set _data _self = x :: _data _self
        
        let pop! _self = 
           val result = head (_data _self)
           set _data _self = tail (_data _self)
           result
           
        let empty? self = empty? (_data _self)

        
    var stack = Stack { _data = [] }
    push! stack 10
    push! stack 20
    pop! stack ;; <- retorna 20
    

Esta es una definición de una clase mutable (con estado).
Es una convención en Ogú que las funciones que mutan una clase deben llevar el signo ! al final del nombre, 
de este modo se sabe que mutan el estado del objeto.
Por eso se las llama mutadores.

Otra convención es que los métodos que permiten consultar el estado de una clase llevan el signo ? al final del nombre.

Los metodos de clase deben tener siempre el parámetro self, es opcional colocar el tipo, porque en realidad siempre será del tipo de la clase.

Es un error declarar un método sin el parámetro self dentro de la clase.

## Herencia de clases

Ogú no tiene herencia de clases.

Sin embargo, existe una característica que permite simularla:

    class Rectangulo(x,y,w,h:Int)
    
    class Cuadrado(x,y,l:Int) = Rectangulo(x,y,l)

Con esto decimos que en realidad Cuadrado es un caso especial de rectángulo.

(En realidad es un constructor con otro nombre).

En este caso Cuadrado no puede tener una sentencia where para definir nuevos métodos ni sobre escribirlos.

Esto es util para crear nuevos constructores:

    mut class Auto(modelo:String) = Auto(modelo, 0)
    mut class Auto() = Auto(“Sin modelo”, 0)

# Módulos

Las clases, tipos y funciones se pueden declarar dentro de un módulo.


    module Collections 
        class Stack where...
        class List where...

Si module aparece al principio del archivo se pueden omitir las llaves y se considera todo el archivo como parte del módulo.


Los modulos se importan con la palabra reservada **uses**.


    module ModA 
    
        class A() where
         … def foo … 
    
    module ModB 
        class B()
    
    
    module ModC 
        
    uses ModA
    val obj = A()
    foo obj ; ok
    bar obj ;; errror


Para desambiguar clases o tipos uno puede usar la notación modulo.Tipo

    module ModA 
    
    class A() ...
    
    module ModB 
    
    class A() ...
    
    uses ModA, ModB
    val x := A() // <- error, ambiguo
    val y := ModA.A() // ok
    val z := ModB.A() // ok


Los nombres de módulo también empiezan en mayúsculas.

Se puede definir alias usando type y alias

    uses ModA, ModB
    type AA = ModA.A
    type AB = ModB.A

    uses ModA, ModB
    alias AA = ModA.A
    alias AB = ModB.A
    alias mA = ModA.m

La diferencia es que alias se puede usar con funciones y type solo con tipos.

Además alias no introduce un tipo nuevo, por lo tanto no se puede aplicar en expresiones de algebra de tipos.


# Traits

Un trait es similar al concepto de type clases en Haskell
Los traits:

    1. no pueden ser instanciados (es decir, no tienen constructores).
    2. pueden tener sólo prototipos de funciones (una clase puede tener prototipos de funciones, pero también debe tener la implementación de la función)
    3. Pueden tener variables de instancia (en este caso deben llevar el prefijo mut):


Por ejemplo:

   trait Figura self where
    
        def area :: self -> Float
        def perimetro :: self -> Perimetro
    
   mut trait Container e where
        
        var _data : [e] = []
        

El argumento que recibe un trait es el tipo al que se le implementarán las funciones del trait.
        

Con esto podemos crear clases que implementan el trait Figura

    class Circulo(x,y:Int, radio:Int)
    
    instance Figura Circulo where
    
        let area circ = pi * (radio circ) ^ 2
        
        let perimetro c = 2 * pi * (radio c)
        
    class Rectangulo(x,y, ancho,alto:Int) 
    
    
    instance Figura Rectangulo where
    
        let area self = (ancho self) * (alto self)
    
        let perimetro = 2 * ((ancho self) + (alto self))


# Tipos de datos algebraicos

En Ogú se puede hacer lo siguiente:

    class Circulo(…)
    class Rectangulo(…)
    class Cuadrado(…) = Rectangulo(…) 
    class Triangulo(…) 
    
    data Figura = Circulo | Rectangulo | Cuadrado | Triangulo
    
    def area :: Figura -> Int
    def area c:Circulo : Int = …
    def area r:Rectangulo : Int = …
    def area t:Triangulo : Int = …
    ;; como Cuadrado es un Rectangulo no es necesario implementarlo, salvo que se quiera realizar una implementación más eficiente.


También podemos hacer esto:

    class Leaf(value:Int)
    
    type Tree = Empty | Leaf | Node(Tree,Tree)
    
    def depth : Tree -> Int
    def depth Empty = 0
    def depth Leaf = 1
    def depth Node(l,r) = 1 + max (depth l) (depth r)

Otro ejemplo:

    class Number(value:Int)
    
    type Expression = Number
                    | Add(Expression, Expression)
                    | Minus(Expression, Expression)
                    | Mult (Expression, Expression)
                    | Divide(Expression, Expression)
    
    
    def evaluate :: Expression -> Int
    let evaluate a:Number = value a
    let evaluate Add (e1, e2) = evaluate e1 + evaluate e2
    let evaluate Minus(e1, e2) = evaluate e1 - evaluate e2
    let evaluate Mult(e1, e2) = evaluate e1 * evaluate e2
    let evaluate Divide(e1, e2) = evaluate e1 / evaluate e2

    val e : Expression = Add(Number(3), Number(4))
    evaluate e ;; = 7
    val es := Multiply( Add(Number(3),Number(4)), Divide(Number(8), Number(2)) )
    evaluate es ;; = 28



# Clases Paramétricas

Las clases pueden ser paramétricas (como los templates en C++):

    mut class Stack t () where
    
        var _data : [t] = []
    
        let push! self x:T = 
            $_data <- x :: $_data

        let pop! self = 
            val result = head $_data
            $_data <- tail $_data
            result
    
        let empty? self =
            empty? $_data
    

Entonces podemos definir cosas como

    type IntStack = Stack Int

Y usarlo:

    var stack = IntStack()
    push! stack 10
    push! stack 20
    pop! stack ; <- retorna 20
    

Sin esta característica escribir una clase Stack genérica sería así de tortuoso:
    
    
    trait Stackable s e where
    
        def push!  : s -> e -> !
        def pop!   : s -> e
        def empty? : s -> Bool
        
    
    ;; Un contenedor de elementos tipo e
    mut trait MutContainer e where
    
        var _data : [e] = []   ;; los traits pueden tener variables mutables
    
    ;; acá reemplazamos s por (MutContainer e)
    instance Stackable (MutContainer e) e where
    
        let push! stack x = ;;; stack es de tipo MutContainer e, x es de tipo e
           set _data s = x :: _data
        
        let pop! stack =
           let result = head (_data stack) ;; fallará si _data en stack es [])
           set _data stack = tail (_data stack)
           result
        
        let empty? stack = null? (_data stack)
        
    
    type Stack x = Stackable (MutContainer x) x
    
    type IntStack = Stack Int
    
    var stack = IntStack() ;; las instancias de un trait tienen el contructor ()
    
    push! stack 10
    push! stack 20
    pop! stack ; <- retorna 20


# Tipos Paramétricos

También se pueden presentar tipos paramétricos algebraicos

    data Maybe t = Nothing | Some t
    
    val any : Maybe String = Nothing
    val some  : Maybe String = Some “algo”

A los tipos y clases paramétricos se les puede exigir que cumplan ciertas restricciones


    class (t:Ord) => Stack t () where …
    
    data (t:Ord) => Maybe t = Nothing | Some t
     
     
    

    





