(ns bamse.subs
  (:require
   [re-frame.core :as re-frame]
   [bamse.users.subs]))

(defn register []

 (bamse.users.subs/register)

 (re-frame/reg-sub
  ::db
  (fn [db _]
    db))

 (re-frame/reg-sub
  ::title
  (fn [db _]
    (:title db)))

 (re-frame/reg-sub
  ::active-route
  (fn [db _]
    (:active-route db)))

  (re-frame/reg-sub
  ::language
  (fn [db _]
    (:language db)))

 (re-frame/reg-sub
  ::active-route-id
  :<- [::active-route]
  (fn [route _]
    (when-let [id (get-in route [:route-params :id])]
      (js/parseInt id))))

 (re-frame/reg-sub
  ::mobile-menu-open
  (fn [db _]
    (:mobile-menu-open db)))

  (re-frame/reg-sub
  ::language-menu-open
  (fn [db _]
    (:language-menu-open db)))

 (re-frame/reg-sub
  ::ssr-waits
  (fn [db _]
    (let [waits (:ssr-waits db)
           ;; _ (println "waiting for: " waits)
          waits (map #(get-in db (conj % :http-status)) waits)]
      (every? #(or (= % :result)
                   (= % :error)) waits))))

 (re-frame/reg-sub
  ::readme
  (fn [db _]
    (get-in db [:readme :http-result])))

 (re-frame/reg-sub
  ::readme-loading
  (fn [db _]
    (= (get-in db [:readme :http-status]) :loading)))

 (re-frame/reg-sub
  ::url
  (fn [db _]
    (get-in db [:url :http-result])))

 (re-frame/reg-sub
  ::url-loading
  (fn [db _]
    (= (get-in db [:url :http-status]) :loading)))

 (re-frame/reg-sub
  ::poe
  (fn [db _]
    (get-in db [:poe :http-result])))

 (re-frame/reg-sub
  ::poe-loading
  (fn [db _]
    (= (get-in db [:poe :http-status]) :loading))))
