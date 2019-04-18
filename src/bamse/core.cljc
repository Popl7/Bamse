(ns bamse.core
  (:require [re-frame.core :as re-frame]
            [bamse.config :as config]
            [bamse.events :as events]
            #?@(:cljs/ssr [[bamse.routes :as routes]]
                :cljs/browser [[re-frame-redux.core :as redux]])
            [bamse.subs   :as subs]
            [bamse.views  :as views]))

(defn dev-setup []
  (when config/debug?
    #?(:cljs/ssr (println "[App] Node in development mode")
       :cljs/browser (do
                       (println "[App] Browser in development mode")
                       (enable-console-print!)
                       (when config/redux?
                         (redux/setup))))))

(defn init [{:keys [path state lang]}]
  (dev-setup)
  (events/register)
  (subs/register)
  #?(:cljs/ssr (do
                 (println "START FOR SERVER")
                 (re-frame/dispatch-sync [::events/initialize-db])
                 (routes/navigate-to path))
     :cljs/browser (do
                (println "START FOR BROWSER")
                (if state
                  (re-frame/dispatch-sync [::events/set-db state])
                  (re-frame/dispatch-sync [::events/initialize-db]))))
  (when lang
    (re-frame/dispatch-sync [::events/set-language lang])))

(defn app-view []
  [views/page])
