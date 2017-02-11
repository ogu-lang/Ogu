

(def println! println)

(def readln! read-line)

(defn zip [a b] (map vector a b))

(def uniq distinct)


(def length count)

(defn is-digit? [d] (Character/isDigit d))


(defn to-digit
      ([d] (Character/digit d 10))
      ([d b] (Character/digit d b)))

(defn mostrar-reglas [tam] (print "Bienvenido a Toque y Fama.\n==========================\n\n\nEn este juego debes tratar de adivinar una secuencia de " tam "dígitos generadas por el programa.\n\nPara esto ingresas " tam " dígitos distintos con el fin de adivinar la secuencia.\n\nSi has adivinado correctamente la posición de un dígito se produce una Fama.\n\nSi has adivinado uno de los dígitos de la secuencia, pero en una posición distinta se trata de un Toque.\n\n\nEjemplo: Si la secuencia es secuencia: [8, 0, 6, 1, 3] e ingresas 40863, entonces en pantalla aparecerá:\n\ntu ingresaste [4, 0, 8, 6, 3]\n\nresultado: 2 Toques 2 Famas\n\n\n"))
(defn ingresar [tam] (do (println "Ingresa una secuencia de " tam " dígitos distintos (o escribe salir):") (flush) (read-line)))
(defn famas [num xs] (->> (zip num xs) (filter (fn [x] (= (first x) (second x)))) count))
(defn toques [num xs] (->> num (filter (fn [x] (contains? xs x))) count))
(defn validar [n xs] (let [num (->> xs (filter is-digit?) (map to-digit) uniq)] (if (= (length num) n) num [])))
(def tam 5)
(def sec (->> (shuffle (range 0 (inc 9))) (take tam)))
(mostrar-reglas tam)
(loop [intentos 1 accion (ingresar tam)] (if (or (= accion "salir") (nil? accion)) (println! "\ngracias por jugar, adios.") (do (println intentos) (recur (inc intentos) (ingresar tam)))))
