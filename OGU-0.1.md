Notas sobre el lenguaje Ogu.
Ideas para el diseño del lenguaje
Estos son apuntes, no constituyen una guia para el lenguaje, muchas cosas pueden cambiar.

Autor: Eduardo Díaz


# Constantes y Variables

Los valores se pueden almacenar en variables inmutables, o constantes, que se indican con la palabra reservada **val** .
Las variables mutables se declaran con la palabra reservada **var**.

Ejemplos:

   |val  maxIntentos : Int = 10
    var intentosHastaAhora : Int = 0

Cuando se declara una variable se debe **def**inir su tipo, usando dos puntos y el nombre del tipo. Los tipos en Ogú siempre empiezan con mayúsculas (Los tipos privados empiezan por _ y una mayúscula). Los nombres de variables siempre empiezan en minúsculas. 

Además siempre se debe colocar su valor inicial usando el operador =.

Ogú aplica inferencia de tipos, basta con omitir el tipo entre el : y la asignación.

Ejemplos:

    val maxIntentos := 10
    var intentosHastaAhora := 0
    val pi = 3.141516
    var e = 2.71828
    

(Pueden escribir := ó = cuando se hace inferencia de tipos, es cuestión de gustos)
(Por convención colocamos el operador = pegado al :, pero pueden haber espacios entre ellos, es decir, en Ogú no existe el operador “:=“).

Hay varios tipos escalares, como Int, Float, String, Bool.

Pero se pueden crear tipos vectoriales como tuplas y listas usando la siguiente notación:

[T] : Declara una lista de elementos de tipo T.
(T1,T2,T3) : declara una dupla de elementos de tipos T1, T2, T3.


Ejemplos:

    var vector : (Float, Float, Float) = (2.0, 3.0, 10.0)
    var perfil : (String,Char,Int,Date) = (“Juan”, “M”, 30, #19850101)
    var nombres : [String] = [“Pedro”, “Juan”, “Diego”]

En Ogú las tuples son usadas en varios contextos. Por ejemplo, hay funciones que retornan tuplas. En ese caso si se quiere rescatar los valores de retorno de la tupla en forma separada se debe usar la siguiente notación:

    var (p:Int, q:Int) = frac(0.4) // x = 4, y = 10
    var (p,q) := frac(0.4)
    var (p,q) : (Int,Int) = frac(0.4)

(Acá suponemos que frac(x) retorna un número real como una fracción)

Por supuesto se pudo hacer lo siguiente:

    var f : (Int, Int) = frac(0.4) // f = (4,10)


# Funciones

Ogú es un lenguaje con una fuerte influencia del paradigma funcional y del lenguaje Haskell en particular. 

Una función en Ogú sólo recibe un parámetro. Esto parece bien extraño, pero permite usar Currying, una técnica del paradigma funcional muy útil.

Consideremos la función max, que entrega el máximo entre dos número, en Ogú se puede invocar de la siguiente manera:

    max 10 4

Con esto Ogú retornará 10.

Otra manera de invocar esta función es:

    (max 10 4)

Como se hace en Lisp, esto es útil en contextos donde puede haber ambigüedad.

Consideremos los siguientes casos

    max 4 5 + 2
    2 + max 4 5
    max 4 + 5 2

¿Cómo interpreta esto el compilador de Ogú?

En el primer caso el resultado es 7. En el segundo caso también es 7. Sin embargo en el tercer caso se produce un error.

    max 4 5 + 2 // ((max 4 5) + 2)
    2 + max 4 5 // (+ 2 (max 4 5)
    max 4 + 5 2 // error

El tercer caso es un error porque se interpreta como ((max 4) + 5 2), (max 4) es una función parcial cuyo segundo argumento debería ser otro número o una función, pero el argumento recibido es + 5 2, esa es una expresión sin sentido en Ogú.

Sin embargo se puede hacer lo siguiente:

    max 4 (+ 5 2) // (max 4 (+ 5 2)) el resultado sería 7.

Al principio de esta sección dijimos que una función en Ogú recibe sólo un parámetro, pero en los ejemplos hemos visto dos argumentos.

La razón es que en rigor cuando hacemos:

    max 4 5

en realidad se evalúa de la siguiente manera

    ((max 4) 5)

Esto es importante por eso que en Ogú a veces preferimos usar tuplas como parámetros de las funciones, como se explica a continuación.

## Invocando funciones con tuplas

Supongamos ahora que existe otra función que llamaremos max’ que en este caso ha sido **def**inida para recibir una tupla de dos elementos (dupla). En este caso para invocarla se deben usar paréntesis y comas en su invocación.

    max’ (4, 5)

Porque esta función recibe una dupla y retorna un valor.

La función max se declara en Ogú de la siguiente manera:

    def max a:Num b:Num : Num = **if** a > b **then** a **else** b

en cambio la función max’ se declara en Ogú de esta manera

    def max’ (a:Num, b:Num) : Num = if a > b then a else b

Aunque son similares, las dos funciones se evalúan de manera diferente. La primera función max permite hacer currying.

Por ejemplo:

    def from5 : Num -> Num = max 5

define una función que retorna 5 o cualquier número mayor que 5.

La notación Num -> Num define un tipo lambda (ver más adelante).

Con lo anterior tendremos lo siguiente:

    from5 3 // retorna 5
    from5 8 // retorna 8

## Currying

En Ogú se puede usar Currying igual que en Haskell.

Ejemplos:

    def multiplicar x: Num y: Num : Num = x * y
    def doblar : Num -> Num = multiplicar 2
    def diez : Num = doblar 5
    val doce = double 6

El primer caso **def**ine una función multiply, que recibe un número, *se aplica* sobre otro número para retornar un tercer número.

En segundo caso **def**ine una función que retorna otra función que multiplica por dos sus argumentos.

La función diez es una función que retorna siempre el mismo valor. En estos casos es mejor declarar una variable inmutable como hacemos con doce.

## Declaración de funciones

La forma de declarar una función es la siguiente

    def nombreDeLaFuncion parametros_curry : TipoRetorno = cuerpoDeLaFuncion

Ejemplos:

    def factorial n:Num : Num = if n == 0 then 1 else n * factorial (n-1)

El parámetro debe tener un nombre y un tipo, como en el caso anterior, n es el parámetro de tipo Num.

El parámetro puede ser una tupla como en este ejemplo:

    def min’(a:Num, b:Num) : Num = if a < b then a else b

Por supuesto el valor de retorno puede también ser una tupla:

    def swap’(a:Num, b:Num) : (Num, Num) = (b,a)

El tipo de retorno de la función se puede omitir y Ogú lo deduce del cuerpo de la función, siguiendo una notación a la inferencia de tipos de las variables:

    def min(a:Num, b:Num) := if a < b then a else b

    def swap (a:Num, b:Num) = (b, a) // también se puede omitir el : antes del signo =

    def multiplicar x: Num y: Num = x * y
    def doblar = multiplicar 2
    doblar 10 // retorna 20
    def diez = doblar 5

    def max’ (a:Num, b:Num) := if a > b then a else b


El uso de tuplas y currying permite hacer cosas interesantes como lo siguiente:

    def sumarVectores (a:Num,b:Num) (c:Num,c:Num) = (a+c, b+d)

    sumarVectores (10,10) (20,20) // produce (30,30)


Otra forma simplificada de declarar parámetros con tuplas es la siguiente:

    def sumarVectores (a,b:Num) (c,d:Num) = (a+c, b+d)

El compilador es suficientemente inteligente para permitir esta abreviación.


## Pattern Matching de Funciones

Esta es una característica tomada de Haskell, que permite definir funciones de manera bastante conveniente:

    def factorial 0 = 1
    def factorial 1 = 1
    def factorial n = n * factorial(n-1)

El compilador infiere el tipo de los argumentos de acuerdo al pattern matching (en este caso usará Int).

Otro ejemplo:

    def radioAlfa ‘a’ = “Alfa”
    def radioAlfa ‘b’ = “Bravo”
    def radioAlfa ‘c’ = “Charlie”
    def radioAlfa ‘d’ = “Delta”
    def radioAlfa ‘c’ = “Charlie”

En este caso estamos definiendo una función que retorna un string por cada carácter usando el alfabeto radiofónico.

## Prototipos

Si queremos usar otro tipo debemos declarar previamente el tipo de la función.

    def factorial :: Num -> Num
    def factorial 0 = 1
    def factorial 1 = 1
    def factorial n = n * factorial(n-1)

Cuando declaramos un prototipo, podemos colocar las demás definiciones de este modo:

    def factorial :: Num -> Num
        factorial 0 = 1
        factorial 1 = 1
        factorial n = n * factorial(n-1)

En este caso las definiciones deben ir una tras otra después del prototipo. La indentación es opcional, pero es un estilo usado en Ogú.



Podemos re declarar sumarVectores así:

    def sumarVectores :: (Num,Num) -> (Num,Num) -> (Num,Num)
        sumarVectores (a,b) (c,d) = (a+c, b+d)

Esto es muy similar a Haskell. La ventaja es que le indicamos al compilador que es lo que necesitamos precisamente.


    **def** radioAlfa :: Char -> String
            radioAlfa ‘a’ = “Alfa”
            radioAlfa ‘b’ = “Bravo”
            .... etc...

El formato de una predefinición de tipo de una función es:

    def nombreDelaFuncion :: Tipo -> Tipo

A esto lo llamamos prototipo de función.

Para declarar un prototipo de una función con currying usamos el operador flecha tantas veces como sea necesario:

    **def** nombreDeLafuncion :: Tipo -> Tipo -> Tipo …

Ejemplos:

    def multiplicar :: Num -> Num -> Num
        multiplicar x y = x*y

El operador -> es asociativo por la derecha, así que se debe considerar usar paréntesis cuando se usan tipos lambda. 

Por ejemplo:

    def applyTwice :: (Num -> Num) -> Num -> Num
        applyTwice fn x = fn (fn x)

La función applyTwice aplica una función que recibe un numero y retorna un numero dos veces.

Ejemplo:

    def add5 x:Num = x + 5
    applyTwice add5 10 // = 20

## Funciones genéricas

Si en un prototipo usamos identificadores en minúsculas, estamos declarando un patrón, o función genérica.

Por ejemplo:


    def first :: (a,b,c) -> a
        first (a,_,_) = a

    def second :: (a,b,c) -> b
        second (_,b,_) = b

el símbolo _ indica que no nos interesa el valor. 
En estos dos ejemplos hemos creado funciones para obtener elementos de una 3-tupla.

## Funciones con listas 

Veamos algunos ejemplos:

    def head’ :: [x] -> x
        head’ [] = error “Lista vacía”
        head’ [x::_] = x

    def length’ :: (l: Num) => [x] -> l
        length’ [] := 0
        length’ [x::xs] := 1 + length’ xs

    def tell :: (a:Show) => [a] -> String
        tell [] = “lista vacía”
        tell [x] = “la lista tiene un elemento “ ++ show x
        tell [x,y] = “la lista tiene dos elementos: “ ++ show x ++ show y
        tell [x,y,…] = “la lista es larga. Los primeros dos elementos son:” ++ show x ++ show y

En estos ejemplos vemos como el prototipo puede restringir los tipos de las variables genéricas. 

En el caso de length’ restringimos a que l es de tipo Num.
En el caso de tell, restringimos a que a pertenezca a la clase Show.

## Guardias

A veces una función se puede expresar mejor en función de varias condiciones que deben cumplirse.

Por ejemplo, supongamos que queremos una función que nos clasifique según nuestro indice de masa corporal (imc).

    def strIMC :: (a:Float) => a -> String
        strIMC imc
        | imc <= 18.5 = “estas bajo el peso normal”
        | imc <= 25.0 = “tu peso es normal”
        | imc <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”

A diferencia del pattern matching, que sólo permite valores o formas de una expresión, los guardias permiten expresiones booleanas.
En este caso los guardias se separan por una barra vertical | y están antes del cuerpo de la función.

Otro ejemplo, en este caso calculamos el IMC en base a la estatura y el peso.

    def strIMC’ :: (a:Float) => a -> a -> String
        strIMC’ peso altura 
        | peso / altura ^ 2 <= 18.5 = “estas bajo el peso normal”
        | peso / altura ^ 2 <= 25.0 = “tu peso es normal”
        | peso / altura ^ 2 <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”


## **where** 

La función anterior calcula una y otra vez el IMC. Podemos simplificar esto usando el operador **where** :

    def strIMC’ :: (a:Float) => a -> a -> String
        strIMC’ peso altura 
        | imc <= 18.5 = “estas bajo el peso normal”
        | imc <= 25.0 = “tu peso es normal”
        | imc <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where val imc := peso / altura ^ 2

Si queremos documentar un poco más esta función podemos hacer lo siguiente

    def strIMC’ :: (a:Float) => a -> a -> String
        strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where imc = peso / altura ^ 2 
          and delgado = 18.5
           and normal = 25,0
           and gordo = 30.0.

Una forma más simplificada es:
    def strIMC’ :: (a:Float) => a -> a -> String
        strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where  imc = peso / altura ^ 2
        and (delgado,normal,gordo) = (18.5, 25,0, 30.0)

La cláusula **where**  permite definir variables o funciones. Las variables declaradas on inmutables. 

Una función se puede definir del siguiente modo:

    def calcIMCs :: (a:Float)=> [(a, a)] -> [a]  
        calcIMCs lista = [imc p a | (p, a) <- lista]  
        where  imc peso altura = peso / altura ^ 2

Esta función recibe una lista de duplas con pesos y alturas retornando una lista de los indices de masa corporal respectivos.
(Notar que se parece mucho a Haskell)

La notación [imc p a | (p,a) <- xs] indica que se debe armar una lista por comprensión, donde cada elemento de la lista corresponde a aplicar la función imc para cada parámetro p y a, donde p y a son los elementos de la dupla en xs. 
El operador <- toma cada uno de los elementos de la lista. 

## Cuerpo de la función

Hasta ahora hemos visto sólo casos en que la función consiste en una expresión simple. También hemos visto como usar guardias y pattern matching.

¿Pero que pasa cuando las funciones son más complejas, con varias expresiones?

Consideremos la función minmax, que retorna una dupla con los valores máximos y mínimos de una lista.

    def minmax :: (x:Ord) => [x] -> (x,x)
        minmax [] = error “debe contener al menos un elemento”
        minmax xs = {
          var cmin := head xs
          var cmax := cmin
          for x <- tail xs do { 
            when x < cmin do cmin = x
            when x > cmax do cmax = x
         }
        (cmin, cmax)
    }

Esta es una implementación imperativa de este problema. No es la mejor manera de implementar esta solución en Ogú. Pero sirve para introducir varios conceptos.

Lo primero, cuando hay más de una expresión se deben agrupar entre llaves {}. Cada expresión va en una linea.
Cuando un bloque corresponde al cuerpo de una función entonces el valor de la función será la última expresión del bloque.

La sentencia when se usa porque en Ogú un **if** es una expresión que requiere siempre un else. En cambio when permite ejecutar una sentencia cuando su expresión condicional es verdadera. 

Si se necesitan dos expresiones en una línea se pueden separar por punto y coma.

La elección de when y su sintáxis está elegida para hacer el código más "feo", con el fin de impulsar un estilo más funcional.
En general las sentencias que tienen do son imperativas y rompen el paradigma funcional.

La forma de when es 

    when expresión do (expresión o bloque de expresiones)

El loop for es es bastante simple de entender, lo explicaremos en más detalle más adelante.

Esta es otra manera de definir esta función

    def minmax :: (x:Ord) => [x] -> (x,x)
        minmax [] = error “debe contener al menos un elemento”
        minmax xs = (minimun xs, maximun xs)
        where  maximun [x] = x
          and  maximun [x,x] = max x (maximun xs)
          and  minimun [x] = x
          and  minimum [x,xs…] = min x (minimum xs) 

No es la forma más eficiente, pero refleja el espíritu de Ogú. 
En general usar loops, when en Ogú no es buen estilo.

## Let

Consideremos la función areaCilindro, que calcula el área de un cilindro:

    def areaCilindro :: a:Float => a -> a -> a
        areaCilindro r h = 
         let areaLateral = 2 * pi * r * h
         and areaBase = pi * r ^ 2
         in areaLateral + 2 * areaBase 

Let permite definir funciones o variables (inmutables) que son usadas en la expresión que viene después de in.

La forma general es let binding (and binding)* in expresión.

A diferencia de **where** , Let es una expresión, **where**  es una construcción sintáctica para simplificar la elaboración de algunas funciones.


Es decir, se puede usar en expresiones como:

    4 * (let a = 9 in a + 1) + 2 // = 42

Un uso es permitir introducir funciones en expresiones de listas por comprensión:

    [let square = x * x in (square 5, square 3, square 2)]
    // [(25,9,4)]

En vez de and, se puede usar ‘;’ y en ese caso se pueden colocar las declaraciones en la misma linea:

    (let a = 100; b = 200; c = 300 in a*b*c)


Nota: lo mismo se puede hacer con **where** . El compilador nota que hay un punto y coma y espera que venga otra declaración en la misma línea o en la linea siguiente:

    def strIMC’ :: (a:Float) => a -> a -> String
        strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where imc = peso / altura ^ 2;
              (delgado,normal,gordo) = (18.5, 25,0, 30.0)

Pero hay que tener cuidado, no se debe colocar un ';' después de la última definición.

* (TODO LO ANTERIOR CON RESPECTO A WHERE e IN PUEDE CAMBIAR)

# clases y tipos en Ogú

En Ogú un tipo se introduce con la keyword type

    type Bool = false | true

Esto introduce un tipo enumerado Bool con dos valores posibles, false o true.

Los valores false y true son valores atomicos, o simplemente átomos.

Otros ejemplos

    type BasicColor = red | green | blue


Del mismo modo podemos pensar que Int está definido del siguiente modo:

    type Int = -2147483648 | -2147483647 | … | -1 | 0 | 1 | 2 | … | 2147483647


Hay varios tipos predefinidos en Ogú. Como Int, Float, Long, Double, Char.

Todos estos son tipos escalare, porque sólo tienen un valor.

Se pueden definir tipos vectoriales que son agregaciones de tipos, por ejemplo:

    type String’ = [Char]

O por ejemplo

    type IntVector = (Int,Int)

Los nombres de los tipos en Ogú empiezan con mayúsculas

## Clases


En Ogú se pueden crear clases

    class Persona (val nombre : String; var edad : Int)

Esto define una clase con dos variables de instancia, nombre y edad.

Otros ejemplos:

    class Circulo(val x, y: Int; val radius : Float)

    class Rectangulo(val x,y, alto, ancho:Int)

Para crear un elemento de estas clases se invoca su constructor

    var circulo := Circulo(0,0, 100.0)
    var cuadro = Rectangulo(0, 0, 50, 50)
    var box : Circulo = Circulo(0, 0, 50, 50)


La sintaxis presentada permite crear una clase con variables de instancia que estarán en su constructor.
Se pueden agregar más atributos a una clase que no necesariamente son inicializados en el constructor

    class Empresa (val rut:String, val razonSocial:String) {
        var cantidadEmpleados : Int = 0
        var patrimonioInicial : Money = 1000000
    }

Una clase puede tener varios constructores, estos se declaran del siguiente modo:

    class Auto(val modelo:String; año:Int) = {
        var bencina := 0

        constructor Auto(modelo:String) = Auto(modelo, 0)
        constructor Auto() = Auto(“Sin modelo”, 0)
    }

Los constructores  pueden tener código asociado, el que se invoca dentro de un bloque de código.

    class Auto(val modelo:String; val año:Int) =  {
        var bencina = 0
        constructor Auto(modelo:String) = Auto(modelo, 0) {
            bencina = 0
        }
    }

Dentro de una clase puede ejecutarse código que se invocará cada vez que se cree una nueva instancia de la clase.

    class AutoInc() = {
        var cont = 0
        cont = cont + 1

        def getCont self := cont
    }
    
    val a := AutoInc()
    val b := AutoInc()
    
    getCont a // <— retorna 1
    getCont b // <— retorna 1


Acá acabamos de ver como definir una función de clase, o método.

En Ogú los métodos son funciones que se restringen a la clase y que siempre reciben un parámetro (self).
Se invocan como cualquier función, en Ogú no existe la notación objeto . metodo.

Veamos un ejemplo:

    class Stack() = {
        var _data : [Int] = []

        def push! self x:Int := {
            _data = cons x _data
        }

        def pop! self := {
            val result := head _data
            _data = tail _data
            result
        }

        def empty? self := {
            empty _data
        }
    }
    
    
     var stack : Stack()
     push! stack 10
     push! stack 20
     pop! stack /// <- retorna 20


Esta es una definición de una clase mutable (con estado).
Es una convención en Ogú que las funciones que mutan una clase deben llevar el signo ! al final del nombre, de este modo se sabe que mutan el estado del objeto.
Por eso se las llama mutadores.

Otra convención es que los métodos que permiten consultar el estado de una clase llevan el signo ? al final del nombre.

Los metodos de clase deben tener siempre el parámetro self, es opcional colocar el tipo, porque en realidad siempre será del tipo de la clase.

Es un error declarar un método sin el parámetro self dentro de la clase.

Para obtener un atributo de un objeto usamos la notación punto

    val auto : Auto()
    auto.bencina

Notar que no se puede hacer objeto.metodo (….) !!


## Herencia de clases

Ogú soporta herencia simple.

    class Persona(rut:String, nombre:String, edad:Int)
    class Empleado(rut:String, nombre:String, edad:Int, empresa:String) > Persona(rut, nombre, edad)

## Extensión de clases

Una clase puede ser extendida usando la notación +=

Ejemplo:

    class Persona += {
        def show self { println “nombre: $nombre, edad: $edad" }
    }

# Módulos

Las clases se pueden declarar dentro de un módulo.


    module Collections {
        class Stack = {….}
        class List = { …. }
    }


Si module aparece al principio del archivo se pueden omitir las llaves y se considera todo el archivo como parte del módulo.


Los modulos se importan con la palabra reservada **uses**.


    module ModA {
        class A() = { … def foo … }
    }
    
    module ModB {
        class B() = { … }
    }
    
    module ExtModA {
      uses ModA
      class A += { … def bar … }
    }
    
    module ModC {
        uses ModA
        val obj := A()
        foo obj // ok
        bar obj // errror
    }
    
    module ModD {
        uses ModA, ExtModA
        val obj := A()
        foo obj // ok
        bar obj // ok
    }

Para desambiguar clases uno puede usar la notación modulo.Tipo

    module ModA {
        class A() = {…}
    }
    
    module ModB {
        class A() = { …}
    }
    
    uses ModA, ModB
    val x := A() // <- error, ambiguo
    val y := ModA.A() // ok
    val z := ModB.A() // ok


Los nombres de módulo también empiezan en mayúsculas.

Se puede definir alias usando type, o alias

    uses ModA, ModB
    type AA = ModA.A
    type AB = ModB.A


    uses ModA, ModB
    alias AA = ModA.A
    alias AB = ModB.A

La diferencia es que alias se puede usar con funciones y type solo con tipos.
Además alias no introduce un tipo nuevo, por lo tanto no se puede aplicar en expresiones de algebra de tipos.


# Traits

Un trait es similar al concepto en Scala, es decir, es como una clase más limitada que permite definir un protocolo para una clase.
Los traits son similares a las clases pero:
1. no pueden ser instanciados (es decir, no tienen constructores).
2. pueden tener sólo prototipos de funciones (una clase puede tener prototipos de funciones, pero también debe tener la implementación de la función)

    trait Figura = {
        def area :: self -> Float
        def perimetro :: self -> Perimetro
    }
    

Con esto podemos crear clases que implementan el trait Figura

    class Circulo(val x,y:Int; val radio:Int) ~ Figura = {
        def area self := pi * radio ^ 2
        def perimetro := 2 * pi * radio
    }
    
    class Rectangulo(val x,y, ancho,alto:Int) ~ Figura = {
        def area self := ancho * alto
        def perimetro := 2 * (ancho + alto)
    }

# Tipos de datos algebraicos

En Ogú se puede hacer lo siguiente:

    class Circulo(…) =…
    class Rectangulo(…) = …
    class Cuadrado(…) > Rectangulo(…) = …
    class Triangulo(…) = …
    
    type Figura = Circulo | Rectangulo | Cuadrado | Triangulo
    
    def area :: Figura -> Int
    def area c:Circulo : Int = …
    def area r:Rectangulo : Int = …
    def area t:Triangulo : Int = …
    // como Cuadrado deriva de Rectangulo no es necesario implementarlo, salvo que se quiera realizar una implementación más eficiente.


También podemos hacer esto:

    class Leaf(val data:Int)
    
    type Tree = Empty | Leaf | Node(Tree,Tree)
    
    def depth :: Tree -> Int
    def depth Empty := 0
    def depth Leaf := 1
    def depth Node(l,r) := 1 + max (depth l) (depth r)

Otro ejemplo:

    class Number(val value:Int)
    
    type Expression = Number
                    | Add(Expression, Expression)
                    | Minus(Expression, Expression)
                    | Mult (Expression, Expression)
                    | Divide(Expression, Expression)
    
    
    def evaluate :: Expression -> Int
    def evaluate a:Number := a.value
    def evaluate Add (e1, e2) := evaluate e1 + evaluate e2
    def evaluate Minus(e1, e2) := evaluate e1 - evaluate e2
    def evaluate Mult(e1, e2) := evaluate e1 * evaluate e2
    def evaluate Divide(e1, e2) := evaluate e1 / evaluate e2

    val e : Expression = Add(Number(3), Number(4))
    evaluate e // = 7
    val es := Multiply( Add(Number(3),Number(4)), Divide(Number(8), Number(2)) )
    evaluate es // =


    def evaluate :: Expression -> Int
    def evaluate a:Number := a.value
    def evaluate Add (e1, e2) := evaluate e1 + evaluate e2
    def evaluate Minus(e1, e2) := evaluate e1 - evaluate e2
    def evaluate Mult(e1, e2) := evaluate e1 * evaluate e2
    def evaluate Divide(e1, e2) := evaluate e1 / evaluate e2

# Tipos Paramétricos

Las clases pueden ser paramétricas (como los templates en C++)

    class Stack{T}() = {
        var _data : [T] = []
    
        def push! self x:T := {
            _data = cons x _data
        }

        def pop! self := {
            val result := head _data
            _data = tail _data
            result
        }
    
        def empty? self := {
            empty _data
        }
    }
    

Entonces podemos definir cosas como

    type IntStack = Stack{Int}

También se pueden presentar tipos paramétricos algebraicos

    type Maybe{T} = Nothing | Some(T)
    
    val any : Maybe{String} = Nothing
    val some  : Maybe{String} = Some(“algo”)

A los tipos paramétricos se les puede exigir que cumplan ciertas restricciones


    class Stack{T ~ Ord => T }() ….
    
    class Sorter{T > Num => T}()….





