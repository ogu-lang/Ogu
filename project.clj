(defproject ogu-lang "0.1.2-PLUNKE"
  :description "Ogu Language Bootstrap Compiler"
  :main ^:skip-aot ogu-lang.core
  :profiles {:uberjar {:aot :all}}
  :url "http://ogu-lang.org"
  :license {:name "BSD"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [com.github.lalyos/jfiglet "0.0.8"]
                 [instaparse "1.4.5"]
                 [rhizome "0.2.7"]])
