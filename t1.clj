
(def println! println)

(def readln! read-line)

(defn zip [a b] (map vector a b))

(def uniq distinct)


(def length count)

(defn is-digit? [d] (Character/isDigit d))


(defn to-digit
      ([d] (Character/digit d 10))
      ([d b] (Character/digit d b)))

