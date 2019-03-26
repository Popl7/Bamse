(ns bamse.render
  (:require [cljs.nodejs :as nodejs]
            [reagent.dom.server :as r]
            [re-frame.core :as re-frame]
            [bamse.core :as core]
            [bamse.config :as config]
            [bamse.events :as events]
            [bamse.routes :as routes]
            [bamse.subs :as subs]
            [cljs.core.async :refer [chan >! <! close! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]]))


(defn init-server []
  (re-frame/reg-fx
   :set-cookie
   (fn [[key value]]))
)


(def colors (nodejs/require "colors/safe"))

(defn template [{:keys [url body state]}]
  (let [new-title       (if (:title state) (str config/default-title " - " (:title state)) config/default-title)
        new-description (if (:description state) (:description state) config/default-description)
        og              (:og state)
        og-title        (or (:title og) new-title)
        og-type         (or (:type og) "article")
        og-url          (or (:url og) url)
        og-image        (or (:image og) config/default-image)
        og-description  (or (:description og) new-description)]
    [:html {:lang "nl"}
     [:head
      [:title new-title]

      [:meta {:name    "description"
              :content new-description}]
      [:meta {:property "og:title"
              :content  og-title}]
      [:meta {:property "og:type"
              :content  og-type}]
      [:meta {:property "og:url"
              :content  og-url}]
      [:meta {:property "og:image"
              :content  og-image}]
      [:meta {:property "og:description"
              :content  og-description}]

      [:meta {:charset "utf-8"}]
      [:meta {:name    "viewport"
              :content "width=device-width, initial-scale=1.0"}]

      [:link {:rel  "stylesheet"
              :href "/css/main.css"}]

      [:link {:rel "manifest"
              :href "manifest.json"}]
      [:link {:rel   "apple-touch-icon"
              :sizes "180x180"
              :href  "/apple-touch-icon.png"}]
      [:link {:rel   "icon"
              :type  "image/png"
              :sizes "32x32"
              :href  "/favicon-32x32.png"}]
      [:link {:rel   "icon"
              :type  "image/png"
              :sizes "16x16"
              :href  "/favicon-16x16.png"}]
      [:link {:rel  "manifest"
              :href "/site.webmanifest"}]
      [:link {:rel   "mask-icon"
              :href  "/safari-pinned-tab.svg"
              :color "#3f6f7d"}]
      [:meta {:name    "msapplication-TileColor"
              :content "#00aba9"}]
      [:meta {:name    "theme-color"
              :content "#ffffff"}]]

     [:body
      [:div#app [body]]
      [:div#server-data {:style      {:display "none"}
                         :data-state (pr-str state)}]
      [:script {:src "/js/main.js"}]
      [:script {:dangerouslySetInnerHTML {:__html "bamse.client.init();"}}]]]))

(defn renderit [url]
  (let [db (re-frame/subscribe [::subs/db])]
    (str "<!DOCTYPE html>"
         (r/render-to-string[template {:url   url
                                       :body  core/app-view
                                       :state @db}]))))

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
