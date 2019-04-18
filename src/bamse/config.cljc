(ns bamse.config
  (:require [shadow.resource :as rc]
            ;; [cljs.tools.reader :as treader]
            [cljs.tools.reader.edn :as edn]))

(def default-title "Bamse")
(def default-description "Universal app build using ClojureScript. Uses Re-Frame framework. Renders on server and client")
(def default-image "https://robohash.org/bamse.png?set=set4")

(def debug? ^boolean js/goog.DEBUG)
(def redux? true)

(def client?
  (try
    (if js/document
      true
      false)
    (catch :default _
      false)))

(def server?
  (not client?))

(def ajax-timeout 1000)

(def api-server
  (if client?
   (if debug?
    "http://localhost:3000"
    "") ;; on production browser has same location for api as website
   (str "http://localhost:" (or js/process.env.PORT 3000))))

;; Translations
(def languages {:en "English"
                :nl "Nederlands"
                :fr "Francais"})

(def dictionary
  {:nl (edn/read-string (rc/inline "nl.edn"))
   :fr (edn/read-string (rc/inline "fr.edn"))})

