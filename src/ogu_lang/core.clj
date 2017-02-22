(ns ogu-lang.core (:gen-class)
    (:require [clojure.string :as string]
              [clojure.tools.cli :as cli]
              [ogu-lang.parser :refer [parse-module]]))

(def VERSION "Ogu compiler version 0.1.0 (Plunke)")

(def cli-options
  [["-t" "--tree" "Show Visual representation of AST"]
   ["-p" "--print" "Print AST and Clojure code"]
   ["-n" "--no-banner" "Don't Show Ogu Banner" :id :no-banner ]
   ["-e" "--eval" "Evaluate AST"]
   ["-h" "--help" "Shows Usage"]] )

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn usage [options-summary]
  (string/join \newline ["Usage: oguc [options] modules..."
        ""
        "Options:"
        options-summary
        ""]))

(defn akarru []
  (let [msg (com.github.lalyos.jfiglet.FigletFont/convertOneLine "akarrú")]
    (println msg)
    (println VERSION)))

(defn -main [& args]
  "Parse an Ogu Module"
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (when-not (:no-banner options) (akarru))
    (doseq [module arguments]
            (parse-module options module))))