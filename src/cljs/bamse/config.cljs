(ns bamse.config
  (:require [cljs.tools.reader.edn :as edn])
  (:require-macros [bamse.macros :refer [load-translation]]))

(def default-title "Bamse")
(def default-description "Universal app build using ClojureScript. Uses Re-Frame framework. Renders on server and client")
(def default-image "https://robohash.org/bamse.png?set=set4")

(def debug? ^boolean js/goog.DEBUG)
(def redux? true)

(def server?
  (try
    (and
     (exists? js/process)
     (exists? js/process.versions)
     (exists? js/process.versions.node))
    (catch :default _
      false)))

(def client?
  (not server?))

(def ajax-timeout 2000)

(def api-server
  (if client?
   (if debug?
    "http://localhost:3000"
    "") ;; on production browser has same location for api as website
   (str "http://localhost:" (or js/process.env.PORT 3000))))

;; Translations
(def languages {:en "English"
                :nl "Nederlands"
                :fr "Français"})

(def dictionary
  {:nl (edn/read-string (load-translation "nl.edn"))
   :fr (edn/read-string (load-translation "fr.edn"))})
