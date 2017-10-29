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

Values can be stored in immutable variables, or constants, which are denoted with the reserved keyword `val`

    val c = 300000
    
Variables that can be mutated are declared with the reserved keyword `var`

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

`let` y `val` are synonyms when we declare global variables.

# Lists and Vectors

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
                  
    let peters-age = mymap :age
    
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

Like it's done in Lisp, which is useful in contexts where it could be ambiguous otherwise.

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

When in doubt, is good to use parenthesis.

## Calling functions with tuples

Let's assume that there's another function called `max’` which has been defined to receive a two-element tuple as argument.
In this case, to call the function you must use parents and commas to call it.

    max’ (4, 5)

Because this function receives a 2-tuple and returns a value.

The functions that we've seen are declared in Ogú like this:

    def max a b = if a > b then a else b

where the function `max'` is declared like this:

    def max' (a, b) = if a > b then a else b

A function in Ogú is declared with `def`

Despite similar looks, both functions are evaluated differently.

We can do partial application in the following way:

    let from5 = partial max 5

which defines a partial function which returns 5 or a number greater than 5.

With the function above, we'd have the following:

    from5 3 ; returns 5
    from5 8 ; returns 8

## Partial application

In Ogú you can use partial application just like in Clojure. There is no support for Currying.

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
    
The problem is that `double'` is a function without arguments, and these functions need to be called inside parenthesis in Ogú, like this:
    
    def fun = println! "I have no arguments"
    
    (fun)
     
To make this work, we'd need to have the following:

    (double') 10 ; this won't work
    
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

Sometimes a function can be expressed better in terms of several conditions that must be met.

As an example, let's say we want a function that classifies us according to our body mass index (BMI).

    def strBMI bmi
      | bmi <= 18.5 = "you are underweight"
      | bmi <= 25.0 = "normal weight"
      | bmi <= 30.0 = "you are overweight"
      | otherwise   = "you are obese, careful!"

Compared to pattern matching that only allows values or expressions, guards allow boolean expressions.
In this case guards are separated with a vertical bar (`|`) before the body of the function.

Another example to compute the BMI using the height and weight.

    def strBMI’ weight height 
        | weight / height ^ 2 <= 18.5 = "you are underweight"
        | weight / height ^ 2 <= 25.0 = "normal weight"
        | weight / height ^ 2 <= 30.0 = "you are overweight"
        | otherwise = "you are obese, careful!"

## where

The previous function computes over and over the BMI, we can simplify this using the `where` clause:

    def strBMI’ weight height
        | bmi <= 18.5 = "you are underweight"
        | bmi <= 25.0 = "normal weight"
        | bmi <= 30.0 = "you are overweight"
        | otherwise   = "you are obese, careful!"
        where bmi = weight / height ^ 2

If we want to document this function a bit more we can do the following:

    def strBMI’ weight height
        | bmi <= thin = "you are underweight"
        | bmi <= normal = "normal weight"
        | bmi <= fat = "you are overweight"
        | otherwise   = "you are obese, careful!"
        where
          bmi = weight / height ^ 2
          thin = 18.5
          normal = 25.0
          fat = 30.0

A more compact form would be:

    def strBMI’ weight height
      | bmi <= thin = "you are underweight"
      | bmi <= normal = "normal weight"
      | bmi <= fat = "you are overweight"
      | otherwise   = "you are obese, careful!"
      where bmi = weight / height ^ 2
            (thin, normal, fat) = (18.5, 25.0, 30.0)

The `where` clause after a function body allows to define variables or functions.

Note that both the guards and the declarations of the `where` clause must be indented.

Let's see another example:
    
    def calcBMIs pairs = [bmi w h | (w, h) <- pairs]
       where bmi weight height = weight / height ^ 2

This function receives a list of 2-tuples with weights and heights and returns a list of the respective BMIs.

(Note it looks a lot like Haskell)

The notation `[bmi w h | (w, h) <- xs]` means that a comprehension list is built, where each element of the list corresponds to the application of the function `bmi` for each parameter `w` and `h`, where `w` and `h` are the elements of each tuple in `xs`.

The `<-` operator takes each element of the list.

## Function body

So far we've seen only cases in which the function consists of a simple expression.
We've also seen how to use guards and pattern matching.
But what happens when functions are more complex, with several expressions?

Let's consider the `minmax` function which returns a tuple with the maximum and minimum values of a list.
    
    def minmax [] = println! "must contain at least one element"
    
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
    
This is an imperative implementation of this problem.
It's not the best way to implement this solution in Ogú, but it's helpful to introduce several concepts.

First, when there's more than one expression they are put in a block starting with a `begin` and ending with `end`.

Each expression goes into its own line.

In a block corresponding to a function body the value of the function will be the last expression in the block.

The `when` sentence is used in Ogú because an `if` is an expression which always requires an `else`

Using `when` instead allows running a sentence only when its conditional expression is true.

The choice of using `when` and its syntax is to make the code "uglier" to promote a more functional style.

In general, sentences using `do` are imperative and break the functional paradygm.

A `when` form looks like this

    when expression do (expression or expression block)

The `for` loop is simple to understand but we'll explain it in detail later.
It's imperative style too.

Imperative expressions have a `nil` value, and this is important.

The `var` ... `in` structure allows declaring local variables, in this form:

    var
       v1 = expr1
       v2 = expr2
       ...
    in
       expr
       
Variables can be referenced with the `@` operator, and to change its value we use the `=` operator.

You can't use the value of these variables outside of the `in` section (even when they are being declared).


# Boolean values in Ogú


The values `false` and `true` are reserved values to represent booleans.

The `nil` value is also special. In a boolean expression the boolean value of `nil` is equivalent to `false`.


# Recursion

We use `recur` to implement tail recursion:


    def siracuse n
        | n == 1 = 4
        | n == 2 = 1
        | n % 2 == 0 = recur (n / 2)
        | otherwise = recur (n * 3 + 1)


There is a **similar** construction to the one implemented in Clojure:

    def rev num =
        loop  reversed = 0, n = num in
            if zero? then
                reversed
            else
                repeat reversed * 10 + n % 10, int (n / 10)

`loop` initializes variables, when you call `repeat` you're making a recursive call to the loop with the new values for the variables.

There are two differences with Clojure's `loop`:

1. iteration uses `repeat` not `recur`
2. you can name variables again in the `repeat` form, but you can capture their value temporarily


    loop i = 1, exit = 0 in
        if i == 10 then exit
        else repeat i' = inc i, exit = i' * 2
        
    ; returns 20, if we didn't use `i'` the result would be 18
        
        
# Types

There are two types in Ogú, classes and records.

A class is defined with:

    class Circle(x, y, radius)
    
    class Rectangle(x,  y, width, height)

A record is defined with:

    record Car {company, model, year}

The difference is on the keys. A class can have mutable fields too, as we'll see later.

They are used this way:


    let mustang56 = Car {company = "Ford", model = "Mustang", year = 1956}
    
    let cir = Circle(10, 10, 10)

Records are useful to model entities of the business domain.l negocio.
Classes are best used to implement more structured datatypes.

The fields of a record or a class are accessed as functions applied on the instance,
and they have the name of the field with a `.` prefix, like:

    .company mustang56 ; "Ford"
    
There's a special notation to access to a field:

    !mustang56.company 
   
    
# Traits 

A trait is like Clojure's protocols.

    
    trait Shape is

        def area self
        
    trait Vehicle is

        def move this    

Traits define lists of functions which are supported by the trait.

A class or a record may implement a trait


    class Circle (x, y, radius)
         as Shape
         def area self = pi * (radius ^ 2)

    record Car {company, model, year}
      as Vehicle
         def move this = println! "moving car " company model year
         
Note that when we implement a trait's method we can access to the fields of a class,
like in the case of the `move` method.
It is mandatory to have a parameter corresponding to the instance.
We could have rewritten the `Circle` type like the following:

    class Circle (x, y, radius)
         as Shape
         def area self = pi * (!self.radius ^ 2)
         
As the argument which represents the instance of the object, it can be ignored and we can write the `area` method like:
         
    class Circle (x, y, radius)
         as Shape
         def area _ = pi * (radius ^ 2)  
         
The first argument of a trait can be called as the programmer wants but by convention we call it `self` or `this`.
         
Once we have a trait defined we can extend an already existing type like this:

    extend Rectangle
        as Shape
    
        def area self = (.width self) * (.height self)

(Note the indentation)

When we extend a type we cannot access their fields directly, so we use `.width self`.


## Mutable classes

Mutability is not very desirable in Ogú, so that's why records cannot have mutable fields,
their value is kept immutable during the program execution.

However, classes can have mutable parts by declaring their fields with the `var` attribute.

Let's see an example:


    trait Shape is
    
        def area self
    
    trait Widget is
    
         def draw! self
    
         def move! self x y
         
    class Circle (var x,  var y, val radius)

       as Shape

          def area self = pi * (!self.radius ^ 2)

       as Widget

          def draw! self = println! "draw a circle at (" x ", " y ") with radius " radius

          def move! self new-x new-y = begin
            !x = new-x
            !y = new-y
            draw! self
          end

Note that both `x` and `y` are declared as mutable by setting the `var` attribute.
However, since we don't want the `radius` to vary, we declare it immutable by using `val`.

With this we can modify the value of `x` and `y` inside the class definition with this notation

    !x = new-x
    !y = new-y
    
    
The cost for it is that `x` and `y` are not visible outside of the class.

This forces us to define a protocol to access their values, so we must do the following:


    trait Shape is

        def area self

    trait Widget is

        def draw! self

        def move! self x y

    trait Origin is

        def getX self
        def getY self

    class Circle (var x,  var y, val radius)

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


# Polymorfism

We can create polymorphic functions which allows us to operate with different datatypes in the following way:

    def show-area! shape : Rectangle = println! "the area of a rectangle is " (area shape) " and is of type " (typeof shape)

    def show-area! shape : Shape = println! "the area is " (area shape) " and is of type " (typeof shape)


# Dynamic dispatch

Dynamic dispatch is a way to execute a method based on a discriminator function.

Example:


    dispatch greeting on \x -> (x "language")
   
    def greeting "French" ? person = println "Bonjour" (person "name")
   
    def greeting "English" ? person = println "Hello" (person "name")
   
    def greeting "Spanish" ? person = println "Hola" (person "name")
   
    def greeting otherwise ?  _ = println "?????"
   
    greeting {"name" "Michelle", "language" "French"} ; Bonjour Michell
   
    greeting {"name" "Pedro", "language" "Spanish"} ; Hola Pedro
   
    greeting {"name" "Hans", "language" "German"} ; ?????


Here each method is invoked depending on the result of the lamba expression.

## Class inheritance

Ogú doesn't have class inheritance.

# Modules

Classes, types and functions cab be declared inside a module using `module`:


    module Collections 
       

Modules are imported with the reserved keyword `requires` in a manner similar to Clojure:

    module Demo
        require clojure.stacktrace, clojure.java.io as io, clojure.stacktrace refer all, clojure.string refer [upper-case]
        import java.util Date GregorianCalendar


`require` is used to import other modules written in Ogú or Clojure.


`import` allows importing JVM classes.

`import static` is an additional operation to import static definitions from Java code.


    module snake-game
        import java.awt Color Dimension,
         javax.swing JPanel JFrame Timer JOptionPane,
         java.awt.event ActionListener KeyListener

    import static java.awt.event.KeyEvent (VK_LEFT, VK_RIGHT, VK_UP, VK_DOWN)

To disambiguate classes or types you can use the `module.Type` notation.

