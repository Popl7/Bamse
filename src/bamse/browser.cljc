(ns bamse.browser
  (:require #?@(:cljs/ssr [[]]
                :cljs/browser [[react-dom]
                               [reagent.core :as reagent]
                               [cljs.reader]
                               [re-frame.core :as re-frame]
                               [bamse.core :as core]
                               [bamse.routes :as routes]])))

#?(:cljs/ssr nil
   :cljs/browser
   (do
     (enable-console-print!)

     (defn get-data [tag]
       (-> (.getElementById js/document "server-data")
           (.getAttribute (str "data-" tag))
           (cljs.reader/read-string)))

     (def app-dom-element (.getElementById js/document "app"))

     (defn render [& hydrate]
       (if hydrate
         (react-dom/hydrate (reagent/as-element [core/app-view])
                            app-dom-element)
         (reagent/render    [core/app-view]
                            app-dom-element)))


     (defn start [& [hydrate]]
       (println "[App] Starting...")
       (try
         (let [state (get-data "state")]
           (println "[App] Loaded server state")
           (core/init {:state state}))
         (catch js/Error _
           (println "[App] Could not load server state")
           (core/init {})))
       (routes/client-init)
       (render hydrate))

     (defn reload []
       (println "[App] Reloading...")
       (re-frame/clear-subscription-cache!)
       (render))

     (defn ^:export init [& [skip-hydrate]]
       (if skip-hydrate
         (start)
         (start true))))

   :default nil)
