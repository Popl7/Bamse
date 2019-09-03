(ns bamse.core
  (:require [re-frame.core :as re-frame]
            [re-frame.loggers :as rf.log]
            [bamse.config :as config]
            [bamse.events :as events]
            [re-frame-redux.core :as redux]
            [bamse.subs   :as subs]
            [bamse.views  :as views])
  (:require-macros [bamse.macros :refer [code-for-nodejs code-for-browser]]))


(defn dev-setup []
  (when config/debug?
    (println "[App] Running in development mode on"
             (if config/server? "server" "browser"))

    (code-for-nodejs (println "[App] Compiled for Node.JS"))
    (code-for-browser (println "[App] Compiled for Browser"))

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
