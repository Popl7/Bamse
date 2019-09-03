(ns bamse.server.render
  (:require [cljs.nodejs :as nodejs]
            [reagent.dom.server :as r]
            [re-frame.core :as re-frame]
            [bamse.core :as core]
            [bamse.server.templates :as templates]
            [bamse.events :as events]
            [bamse.routes :as routes]
            [bamse.subs :as subs]
            [cljs.core.async :refer [chan >! <! close! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]]))

(defn init-server []
  (re-frame/reg-fx
   :set-cookie
   (fn [[key value]])))

(def colors (nodejs/require "colors/safe"))

(defn renderit [url]
  (let [db (re-frame/subscribe [::subs/db])
        status-code (:status-code @db)]
    {:status-code status-code
     :result (str "<!DOCTYPE html>"
                  (r/render-to-string [templates/index {:body  core/app-view
                                                        :state @db
                                                        :url   url}]))}))

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
  (core/init)
  (init-server)
  (re-frame/dispatch-sync [::events/set-language lang])
  (routes/navigate-to path)

  (let [render-chan  (chan)
        timeout-chan (timeout 1000)]
   (go
      ;; SSR start
     (alt!
       render-chan  (println :result)
       timeout-chan (do
                      (println "timed out")
                      (close! render-chan)))
      ;; SSR end
     (>! ch (renderit url)))
   (done-rendering render-chan)
   ch))
