# Notas sobre el lenguaje Ogu.

Ideas para el diseño del lenguaje

Estos son apuntes, no constituyen una guia para el lenguaje, muchas cosas pueden cambiar.

Autor: Eduardo Díaz

# Comentarios

En Ogú los comentarios empiezan con ';' (punto y coma) y terminan con el fin de linea

    ; este es un comentario


# Constantes y Variables

Los valores se pueden almacenar en variables inmutables, o constantes, que se indican con la palabra reservada **let** .
Las variables mutables se declaran con la palabra reservada **var**. 
La palabra reservada **val** es sinónimo de let, pero sólo para valores inmutables (no se usa para definir funciones)

Ejemplos:

    let  maxIntentos : Int = 10 ; una constante de tipo Int
    var intentosHastaAhora : Int = 0 ; una variable
    var contador = 0 ; una variable mutable, es igual que var. Es probable que deprequemos var
    val inmutable = 1 ; no se puede cambiar el valor 

Cuando se declara una variable se debe definir su tipo, usando dos puntos y el nombre del tipo. 

Los tipos en Ogú siempre empiezan con mayúsculas (Los tipos privados empiezan por _ y una mayúscula). 

Los nombres de variables siempre empiezan en minúsculas. 

Además siempre se debe colocar su valor inicial usando el operador =.

Ogú aplica inferencia de tipos, basta con omitir el ':' más el nombre del tipo.

Ejemplos:

    let maxIntentos : Int = 10
    var intentosHastaAhora = 0
    let pi : Float= 3.141516
    let alpha = 1231.543
    var e = 2.71828
    

La biblioteca estándar de Ogú definirá los tipos básicos (como Int, Float, String, Bool), esto aún no está claro.

Se pueden crear tipos vectoriales como tuplas y listas usando la siguiente notación:

[T] : Declara una lista de elementos de tipo T.
(T1,T2,T3) : declara una dupla de elementos de tipos T1, T2, T3.
{K->V} : declara un mapa con llave K y valores V
{id:T,id2:T2,...} : declara una estructura.
Ejemplos:

    var vector : (Float, Float, Float) = (2.0, 3.0, 10.0)
    var perfil : (String,Char,Int,Date) = (“Juan”, “M”, 30, #1985-01-01)
    var nombres : [String] = [“Pedro”, “Juan”, “Diego”]
    var edades : {String->Int} = {"Pedro" -> 25, "Juan" -> 30, "Diego" -> 40}
    var perfil2 : {nombre:String, sexo:String, grado:Int, cumpleaños:Date} = {nombre="Juan", sexo="M", grado=30, cumpleaños=#1985-01-01}
    
Las fechas se pueden ingresar usando el símbolo #  seguido de la fecha expresada en un subconjunto del formato ISO8601.

    let timestamp = #2016-01-19T20:30:01.245
    let horazulu = #2016-01-19T16:40:01.s45Z
    let horasantiago = #2016-01-19T16:40:01.s45-03:00
    
Los números se expresan de la manera tradicional, incluyendo notación exponencial.

    let pi = 3.14.15
    let avogadro = 6.022e23
    let un_millon = 1_000_000.00
     
Se puede usar _ para separar miles (en los numeros normales también). 

En Ogú las tuplas son usadas en varios contextos. 
Por ejemplo, hay funciones que retornan tuplas. 
En ese caso si se quiere rescatar los valores de retorno de la tupla en forma separada se debe usar la siguiente notación:

    let (p:Int, q:Int) = frac(0.4) ; x = 4, y = 10
    let (p,q) = frac(0.4)
    let (p,q) : (Int,Int) = frac(0.4)

(Acá suponemos que frac(x) retorna un número real como una fracción)

Por supuesto se pudo hacer lo siguiente:

    let f : (Int, Int) = frac(0.4) // f = (4,10)


# Funciones

Ogú es un lenguaje principalmente funcional, con gran influencia de Haskell. 

Una función en Ogú sólo recibe un parámetro. Esto parece bien extraño, pero permite usar Currying, una técnica del paradigma funcional muy útil.

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

¿Cómo interpreta esto el compilador de Ogú?

En el primer caso el resultado es 7. En el segundo caso también es 7. Sin embargo en el tercer caso se produce un error.

    max 4 5 + 2 // ((max 4 5) + 2)
    2 + max 4 5 // (+ 2 (max 4 5)
    max 4 + 5 2 // error

El tercer caso es un error porque se interpreta como ((max 4) + 5 2), (max 4) es una función parcial cuyo segundo argumento debería ser otro número o una función, 
pero el argumento recibido es + 5 2, esa es una expresión sin sentido en Ogú.

Sin embargo se puede hacer lo siguiente:

    max 4 (+ 5 2) ; (max 4 (+ 5 2)) el resultado sería 7.
    max (4 + 5) 2 ; max 9 2 el resultado sería 9

Al principio de esta sección dijimos que una función en Ogú recibe sólo un parámetro, pero en los ejemplos hemos visto dos argumentos.

La razón es que en rigor cuando hacemos:

    max 4 5

en realidad se evalúa de la siguiente manera

    ((max 4) 5)

Esto es importante por eso que en Ogú a veces preferimos usar tuplas como parámetros de las funciones, como se explica a continuación.

## Invocando funciones con tuplas

Supongamos ahora que existe otra función que llamaremos max’ que en este caso ha sido definida para recibir una tupla de dos elementos (dupla). 
En este caso para invocarla se deben usar paréntesis y comas en su invocación.

    max’ (4, 5)

Porque esta función recibe una dupla y retorna un valor.

La función max se declara en Ogú de la siguiente manera:

    def max : Num -> Num -> Num 
    let max a b = if a > b then a else b

en cambio la función max’ se declara en Ogú de esta manera

    def max’ : (Num, Num) -> Num 
    let max' (a,b) = if a > b then a else b

Aunque parecen similares, las dos funciones se evalúan de manera diferente. La primera función max permite hacer currying.

Por ejemplo:

    def from5 : Num -> Num
    let from5 = max 5

define una función que retorna 5 o cualquier número mayor que 5.

Con lo anterior tendremos lo siguiente:

    from5 3 ; retorna 5
    from5 8 ; retorna 8

## Currying

En Ogú se puede usar Currying igual que en Haskell.

Ejemplos:

    let multiplicar x y = x * y
    let doblar  = multiplicar 2
    let diez  = doblar 5
    let doce = double 6

El primer caso define una función multiply, que recibe un número, *se aplica* sobre otro número para retornar un tercer número.

En segundo caso define una función que retorna otra función que multiplica por dos sus argumentos.

De este modo diez y doce son funciones que retornan el mismo valor (el compilador debería optimizar esto a valores fijos).

## Declaración de funciones

La forma de declarar una función es la siguiente

    def nombreDeLaFuncion : Tipo -> Tipo -> ... -> TipoRetorno 
    let arg arg arg .. = cuerpoDeLaFuncion

Ejemplos:

    def factorial : Num -> Num 
    let factorial n = if n == 0 then 1 else n * factorial (n-1)

Esto es similar a Haskell, pero agregamos las palabras def y let.

Con def definimos el "prototipo" o "firma" de la función.
Con let definimos la igualdad o ecuación que define como calcular la función.

El parámetro puede ser una tupla como en este ejemplo:

    def min’(Num, Num) -> Num 
    let min' (a,b) = if a < b then a else b

Por supuesto el valor de retorno puede también ser una tupla:

    def swap’(Num, Num) -> (Num, Num) 
    let swap'(a,b) = (b,a)


El uso de tuplas y currying permite hacer cosas interesantes como lo siguiente:

    def sumarVectores (Num,Num) (Num,Num) -> (Num,Num)
    let sumarVectores (a,b) (c,d) = (a+c, b+d)

    sumarVectores (10,10) (20,20) // produce (30,30)

Podemos omitir el prototipo "casi siempre" y el compilador tratará de inferir los tipos:

    let min a b = if a < b then a else b
    
(Lo que hace en este caso es buscar un tipo que tenga definido el operador <, normalmente existe un Trait que implementa < que se llama Ord, eso lo veremos más adelante).

    let suma a b = a + b
    
(Acá lo mismo, el trait Num permite sumar dos números.

El prototipo es necesario si queremos explicitar el tipo de dato de los argumentos:

    def suma : Int -> Int -> Int
    let suma a b = a + b
    
Con esto la función suma se puede invocar:

    suma 10 20
    
Pero no con enteros:
    
    suma 1.0 2.0 ; <- es un error


## Pattern Matching de Funciones

Esta es una característica tomada de Haskell, que permite definir funciones de manera bastante conveniente:

    let factorial 0 = 1
    let factorial 1 = 1
    let factorial n = n * factorial(n-1)

El compilador infiere el tipo de los argumentos de acuerdo al pattern matching (en este caso usará Num).

Otro ejemplo:

    let radioAlfa ‘a’ = “Alfa”
    let radioAlfa ‘b’ = “Bravo”
    let radioAlfa ‘c’ = “Charlie”
    let radioAlfa ‘d’ = “Delta”
    let radioAlfa ‘c’ = “Charlie”

En este caso estamos definiendo una función que retorna un string por cada carácter usando el alfabeto radiofónico.


El operador -> es asociativo por la derecha, así que se debe considerar usar paréntesis cuando se usan tipos lambda. 

Por ejemplo:

    def applyTwice : (Num -> Num) -> Num -> Num
    let applyTwice fn x = fn (fn x)

La función applyTwice aplica una función que recibe un numero y retorna un numero dos veces.

Ejemplo:

    def add5 : Num -> Num
    let add5 x = x + 5
    
    applyTwice add5 10 ;= 20

## Funciones genéricas

Si en un prototipo usamos identificadores en minúsculas, estamos declarando un patrón, o función genérica.

Por ejemplo:


    def first : (a,b,c) -> a
    
    let first (a,_,_) = a

    def second : (a,b,c) -> b
    
    let second (_,b,_) = b

el símbolo _ indica que no nos interesa el valor. 
En estos dos ejemplos hemos creado funciones para obtener elementos de una 3-tupla.


Podemos restringir el tipo genérico del siguiente modo:

    def compare : (Num a) => a -> a -> a

    let compare a b = a - b
    
La variable usada en el prototipo (def) representa un tipo restringido a que debe implementar el trait Num.
En cambio la variable a en el let es una variable de tipo Num, lo mismo que b. No deben confundirse.

## Funciones con listas 

Veamos algunos ejemplos:

    def head’ : [x] -> x
    let head’ [] = error! “Lista vacía”
    let head’ (x::_) = x

    def length’ : (l: Num) => [x] -> l 
    let length’ [] = 0
    let length’ (x::xs) = 1 + length’ xs

    def tell : (a:Show) => [a] -> String 
    let tell [] = “lista vacía”
    let tell [x] = “la lista tiene un elemento “ ++ show x
    let tell [x,y] = “la lista tiene dos elementos: “ ++ show x ++ show y
    let tell (x::y::_) = “la lista es larga. Los primeros dos elementos son:” ++ show x ++ show y

En estos ejemplos vemos como el prototipo puede restringir los tipos de las variables genéricas. 

En el caso de length’ restringimos a que l es de tipo Num.
En el caso de tell, restringimos a que a pertenezca a la clase Show.

## Guardias

A veces una función se puede expresar mejor en base a varias condiciones que deben cumplirse.

Por ejemplo, supongamos que queremos una función que nos clasifique según nuestro indice de masa corporal (imc).

    def strIMC : (a:Float) => a -> String
    let strIMC imc
        | imc <= 18.5 = “estas bajo el peso normal”
        | imc <= 25.0 = “tu peso es normal”
        | imc <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”

A diferencia del pattern matching, que sólo permite valores o formas de una expresión, los guardias permiten expresiones booleanas.
En este caso los guardias se separan por una barra vertical | y están antes del cuerpo de la función.

Otro ejemplo, en este caso calculamos el IMC en base a la estatura y el peso.

    def strIMC’ : (a:Float) => a -> a -> String
    let strIMC’ peso altura 
        | peso / altura ^ 2 <= 18.5 = “estas bajo el peso normal”
        | peso / altura ^ 2 <= 25.0 = “tu peso es normal”
        | peso / altura ^ 2 <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”


## **where** 

La función anterior calcula una y otra vez el IMC. Podemos simplificar esto usando  **where** :

    def strIMC’ : (a:Float) => a -> a -> String
    
    let strIMC’ peso altura 
        | imc <= 18.5 = “estas bajo el peso normal”
        | imc <= 25.0 = “tu peso es normal”
        | imc <= 30.0 = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where imc = peso / altura ^ 2

Si queremos documentar un poco más esta función podemos hacer lo siguiente

    def strIMC’ : (a:Float) => a -> a -> String
    
    let strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where 
          imc = peso / altura ^ 2 
          delgado = 18.5
          normal = 25.0
          gordo = 30.0

Una forma más simplificada es:

    def strIMC’ : (a:Float) => a -> a -> String
    let strIMC’ peso altura 
        | imc <= delgado = “estas bajo el peso normal”
        | imc <= normal = “tu peso es normal”
        | imc <= gordo = “estas con sobrepeso”
        | otherwise = “estas obeso, cuidado!”
        where  imc = peso / altura ^ 2
               (delgado,normal,gordo) = (18.5, 25,0, 30.0)

La cláusula **where**  despues del cuerpo de una función permite definir variables o funciones. 

Notar que se deben identar tanto los guards como las declaraciones en el where.

Veamos otro ejemplo:

    def calcIMCs : (a:Float)=> [(a, a)] -> [a]  
    
    let calcIMCs lista = [imc p a | (p, a) <- lista]  
        where  imc peso altura = peso / altura ^ 2

Esta función recibe una lista de duplas con pesos y alturas retornando una lista de los indices de masa corporal respectivos.

(Notar que se parece mucho a Haskell)

La notación [imc p a | (p,a) <- xs] indica que se debe armar una lista por comprensión, donde cada elemento de la lista corresponde la aplicación de
la función imc para cada parámetro p y a, donde p y a son los elementos de la dupla en xs. 

El operador <- toma cada uno de los elementos de la lista. 

## Cuerpo de la función

Hasta ahora hemos visto sólo casos en que la función consiste en una expresión simple. 
También hemos visto como usar guardias y pattern matching.
Pero, ¿qué pasa cuando las funciones son más complejas, con varias expresiones?

Consideremos la función minmax, que retorna una dupla con los valores máximos y mínimos de una lista.

    def minmax : (x:Ord) => [x] -> (x,x)
    
    let minmax [] = error! “debe contener al menos un elemento”
    
    let minmax xs = 
        var cmin = head xs
        var cmax = cmin
        for x <- tail xs do 
            when x < cmin do cmin <- x
            when x > cmax do cmax <- x
        (cmin, cmax)

Esta es una implementación imperativa de este problema. 
No es la mejor manera de implementar esta solución en Ogú. 
Pero sirve para introducir varios conceptos.

Lo primero, cuando hay más de una expresión se colocan en un bloque, el que se distingue por la indentación (4 espacios en este caso)
Cada expresión va en una linea.

Cuando un bloque corresponde al cuerpo de una función entonces el valor de la función será la última expresión del bloque.

La sentencia *when* se usa porque en Ogú un **if** es una expresión que requiere siempre un else. 
En cambio when permite ejecutar una sentencia cuando su expresión condicional es verdadera. 

La elección de when y su sintáxis es para hacer el código más "feo", con el fin de impulsar un estilo más funcional.

En general las sentencias que tienen do son imperativas y rompen el paradigma funcional.

La forma de when es 

    when expresión do (expresión o bloque de expresiones)

El loop for es es bastante simple de entender, lo explicaremos en más detalle más adelante.

Notar que la asignación de las variables cmin y cmax se hace con el operador <-. El signo '=' está reservado
para las variables inmutable o para el valor inicial de una variable mutable.

Esta es otra manera de definir esta función

    def minmax : (x:Ord) => [x] -> (x,x)
    let minmax [] = error! “debe contener al menos un elemento”
    let minmax xs = (minimun xs, maximun xs)
        where  maximun [x] = x
               maximun (x::xs) = max x (maximun xs)
               minimun [x] = x
               minimum (x::xs) = min x (minimum xs) 


En general usar loops y when en Ogú no es buen estilo.

# Tipos en Ogú

En Ogú un existen los enums:

    enum Bool = false | true

Esto introduce un tipo enumerado Bool con dos valores posibles, false o true.

Del mismo modo podemos pensar que Int está definido del siguiente modo:

    enum Int = -2147483648 | -2147483647 | … | -1 | 0 | 1 | 2 | … | 2147483647

Los valores false y true son valores atomicos, o simplemente átomos.

Otra forma de declarar un enum, es usando tipos algebraicos, como en Haskell:

    data BasicColor = red | green | blue


Se pueden declara tipos nuevos en base a tipos existentes, por ejemplo, tipos vectoriales,
que son agregaciones de tipos, para esto usamos type

    type String = [Char]

O por ejemplo

    type IntVector = (Int,Int) ; un vector es una tupla de 2 elementos

Tambien se pueden definir mapas del siguiente modo:

    type StrMap v = {String -> v}
    

Los nombres de los tipos en Ogú empiezan con mayúsculas

De este modo podemos declarar así:

    val b : Bool = false
    val color : BasicColor = red
    val s : String = ""
    val iv = (10,10)
    val edades : (StrMap Int) = {"Pedro"->25, "Juan"->30, "Diego"->40}


## Traits 

Un trait es como los type class de Haskell

    trait YesNo a where
        def yesno :: a -> Bool


Una vez que tenemos definida un trait podemos  instanciarlo en otros tipos del siguiente modo:

    instance YesNo Int where
        let yesno 0 = false
        let yesno _ = true

    instance YesNo [x] where
        let yesno [] = false
        let yesno _  = true

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
     
     
    

    





