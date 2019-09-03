(ns bamse.dev-server
  (:require
    [ring.util.response :refer [resource-response content-type]]))

(defn handler [request]
  (some->
    (resource-response "index.html" {:root "public"})
    (content-type "text/html; charset=utf-8")))
