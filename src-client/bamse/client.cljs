(ns bamse.client
  (:require [react-dom]
            [reagent.core :as reagent]
            [cljs.reader]
            [re-frame.core :as re-frame]
            [pushy.core :as pushy]
            [bamse.core :as core]
            [bamse.config :as config]
            [bamse.routes :as routes]))

(enable-console-print!)

;; client routing
(def history
 (pushy/pushy routes/dispatch-route routes/parse-url))

(defn set-pushstate [path]
 (pushy/set-token! history path))

(re-frame/reg-fx
 :navigate-to
 (fn [path]
  (set-pushstate path)))


;; client rendering
(defn get-data [tag]
  (-> (.getElementById js/document "server-data")
      (.getAttribute (str "data-" tag))
      (cljs.reader/read-string)))

(defn render [& [hydrate]]
  ;; Same as render(), but is used to hydrate a container whose HTML contents were rendered by ReactDOMServer.
  ;; React will attempt to attach event listeners to the existing markup.
 (if hydrate
   (react-dom/hydrate (reagent/as-element [core/app-view]) (.getElementById js/document "app"))
   (reagent/render [core/app-view] (.getElementById js/document "app"))))

(defn stop []
  (js/console.log "[App] Stopping..."))

(defn start [& [hydrate]]
  (js/console.log "[App] Starting...")
  (try
    (let [state (get-data "state")]
      (core/init state))
    (catch js/Error _
      (println "[App] Could not load server state")
      (core/init)))
  (pushy/start! history)
  (render hydrate))

(defn reload []
 (stop)
 (start))

(defn ^:export init [& [skip-hydrate]]
  (if skip-hydrate
    (start)
    (start config/ssr?)))
