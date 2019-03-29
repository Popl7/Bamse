(ns gettext.core
   (:gen-class)
   (:require [pottery.core :as pottery]
             [clojure.java.io :as io]))

(defn export-pot []
  (pottery/scan-codebase!
   {:extract-fn2 (pottery/make-extractor ;; We use the lang as first argument to tr and trn.
                 ['tr _ (s :guard string?) & _] s
                 ['trn _ [(s1 :guard string?) (s2 :guard string?)] & _] [s1 s2]
                 [(:or 'tr 'trn) & _] (pottery.scan/extraction-warning
                                       "Could not extract strings for the form:"))}))

(def lang-path "resources/gettext/")

(defn import-pot [lang]
  (spit (str lang-path lang ".edn")
        (pottery/read-po-file (io/file (str lang-path lang ".po")))))

(defn -main []
  (export-pot)
  (import-pot "nl")
  (import-pot "fr"))

