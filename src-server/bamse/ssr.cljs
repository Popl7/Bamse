(ns bamse.ssr
  (:require [cljs.nodejs :as nodejs]
            [reagent.dom.server :as r]
            [re-frame.core :as re-frame]
            [bamse.core :as core]
            [bamse.subs :as subs]
            [bamse.templates :as templates]
            [cljs.core.async :refer [chan >! <! close! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]]))

(def colors (nodejs/require "colors/safe"))



(defn renderit [url]
  (let [db (re-frame/subscribe [::subs/db])]
    (str "<!DOCTYPE html>"
         (r/render-to-string[templates/index {:body  core/app-view
                                              :state @db
                                              :url   url}]))))

(defn done-rendering [render-chan]
  (let [waits  (re-frame/subscribe [::subs/ssr-waits])
        ms-inc 10]
    (go-loop []
      (if @waits
        (>! render-chan :done)
        (do
          (print (.green colors "waiting..."))
          (<! (timeout ms-inc))
          (recur))))))


(defn ^:export render-page [ch url path lang]
  (println "------- new request ------------------")
  (core/init {:path path :lang lang})

  (let [render-chan  (chan)
        timeout-chan (timeout 1000)]
    (done-rendering render-chan)
    (go
      (alt!
        render-chan  (println :result)
        timeout-chan (do
                       (println "timed out")
                       (close! render-chan)))
      (>! ch (renderit url)))
    ch))
