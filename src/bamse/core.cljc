(ns bamse.core
  (:require [re-frame.core :as re-frame]
            [re-frame.loggers :as rf.log]
            [bamse.config :as config]
            [bamse.events :as events]
            [re-frame-redux.core :as redux]
            [bamse.subs   :as subs]
            [bamse.views  :as views]))

(defn dev-setup []
  (when config/debug?
    (println "[App] Running in development mode")
    (when config/client?
      (enable-console-print!))
    (when config/redux?
      (redux/setup))))

(defn init [& [state]]
  (dev-setup)
  (events/register)
  (subs/register)
  (if state
    (re-frame/dispatch-sync [::events/set-db state])
    (re-frame/dispatch-sync [::events/initialize-db])))

(defn app-view []
  [views/page])
