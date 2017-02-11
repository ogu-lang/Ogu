(defn zip [a b] (map vector a b))
(def uniq distinct)
(defn is-digit? [d] (Character/isDigit d))
(def length count)
(def readln! read-line)


(defn to-digit
      ([d] (Character/digit d 10))
      ([d b] (Character/digit d b)))


(defn mostrar-reglas [tam] (println  "Bienvenido a Toque y Fama.\n==========================\n\n\nEn este juego debes tratar de adivinar una secuencia de " tam "dígitos generadas por el programa.\n\nPara esto ingresas " tam " dígitos distintos con el fin de adivinar la secuencia.\n\nSi has adivinado correctamente la posición de un dígito se produce una Fama.\n\nSi has adivinado uno de los dígitos de la secuencia, pero en una posición distinta se trata de un Toque.\n\n\nEjemplo: Si la secuencia es secuencia: [8, 0, 6, 1, 3] e ingresas 40863, entonces en pantalla aparecerá:\n\ntu ingresaste [4, 0, 8, 6, 3]\n\nresultado: 2 Toques 2 Famas\n\n\n"))
(defn toques [num xs] (->> (filter (fn [x] (contains? xs x)) num) count))
(defn famas [num xs] (->> (zip num xs) (filter (fn [x] (= (first xs) (second xs)))) count))
(defn validar [n xs] (let [num (->> (filter (fn [x] (is-digit? x)) xs) (map to-digit) uniq)] (if (= (length num) n) num [])))
(defn ingresar-accion [tam] (do (println "Ingresa una secuencia de " tam " dígitos distintos (o escribe salir):") readln!))

(def tam 5)

(def sec (->> (shuffle (range 0 (inc 9))) (take tam)))

(mostrar-reglas tam)

(loop [accion (ingresar-accion tam)] (if (= accion "salir") (println "\ngracias por jugar, adios.") (let [num (validar tam accion)] (if (nil? num) (do (println "error!\n") (recur (ingresar-accion tam))) (do (println (concat "tu ingresaste " num)) (def [toques famas] [(toques num sec) (famas num sec)]) (println (co:ncat (concat "resultado: " (- toques famas)) (concat (concat " Toques " famas) "Famas"))) (if (= famas tam) (println (concat "Ganaste! Acertaste al intento " (concat (concat intentos "! La secuencia era ") sec))) (recur (ingresar-accion tam))))))))



