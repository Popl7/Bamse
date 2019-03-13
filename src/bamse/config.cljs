(ns bamse.config)

(def default-title "Bamse")
(def default-description "Universal app build using ClojureScript. Uses Re-Frame framework. Renders on server and client")
(def default-image "https://robohash.org/bamse.png?set=set4")

(def debug? ^boolean js/goog.DEBUG)
(def bm-debug? false)
(def redux? true)
(def ssr? true)

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
