(ns bamse.macros)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- nodejs-target?
  []
  (= :nodejs (get-in @cljs.env/*compiler* [:options :target])))


(defmacro code-for-nodejs
  [& body]
  (when (nodejs-target?)
    `(do ~@body)))

(defmacro code-for-browser
  [& body]
  (when-not (nodejs-target?)
    `(do ~@body)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro load-readme
  "Read README.md"
  []
  (slurp (str "README.md")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro load-translation
  "Read translation file"
  [edn-file]
  (slurp (str "resources/gettext/" edn-file)))

