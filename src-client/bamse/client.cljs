(ns bamse.client
  (:require [react-dom]
            [reagent.core :as reagent]
            [cljs.reader]
            [re-frame.core :as re-frame]
            [goog.net.cookies]
            [pushy.core :as pushy]
            [bamse.core :as core]
            [bamse.config :as config]
            [bamse.routes :as routes]))

(enable-console-print!)

;; client routing
(def history
 (pushy/pushy routes/dispatch-route routes/parse-url))

(defn set-pushstate! [path]
 (pushy/set-token! history path))

(defn set-cookie! [k v]
  (.set goog.net.cookies k v))

(defn client-init []
  (pushy/start! history)

  (re-frame/reg-fx
   :navigate-to
   (fn [path]
     (set-pushstate! path)))

  (re-frame/reg-fx
   :set-cookie
   (fn [[key value]]
   (set-cookie! key (name value))))
)


;; client rendering
(defn get-data [tag]
  (-> (.getElementById js/document "server-data")
      (.getAttribute (str "data-" tag))
      (cljs.reader/read-string)))

(def app-dom-element (.getElementById js/document "app"))

(defn render [& [hydrate]]
 (if hydrate
   (react-dom/hydrate (reagent/as-element [core/app-view])
                      app-dom-element)
   (reagent/render    [core/app-view]
                      app-dom-element)))


(defn start [& [hydrate]]
  (js/console.log "[App] Starting...")
  ;; TODO move getting state from page to fx
  (try
    (let [state (get-data "state")]
      (println "[App] Loaded server state")
      (core/init state))
    (catch js/Error _
      (println "[App] Could not load server state")
      (core/init)))
  (client-init)
  (render hydrate))

(defn reload []
  (js/console.log "[App] Reloading...")
  (re-frame/clear-subscription-cache!)
  (render))

(defn ^:export init [& [skip-hydrate]]
  (if skip-hydrate
    (start)
    (start true)))
