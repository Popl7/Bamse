(ns bamse.events
  (:require
   [re-frame.core :as re-frame]
   [re-frame-redux.core :as redux]
   [bamse.config :as config]
   [bamse.db :as db]
   [bamse.users.events]
   [bamse.helpers :refer [reg-event-db reg-event-fx]]))

(defn register []

 (bamse.users.events/register)
 (redux/register-redux-events :bamse.db/db)

 (reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

  (reg-event-db
   ::set-db
   (fn [_ [_ new-db]]
     new-db))

  (reg-event-fx
   ::set-language
   (fn [{:keys [:db]} [_ lang]]
     {:db (assoc db :language lang)
      :set-cookie ["language" lang]}))

 (re-frame/reg-fx
  :dispatch-multiple
  (fn [events]
    (let [filtered (into {} (filter (comp not nil? first) events))]
      (doall (map re-frame/dispatch filtered)))
    nil))

 (re-frame/reg-fx
  :set-title
  (fn [title]
    (if config/client?
      (try
        (let [new-title (if title (str config/default-title " - " title) config/default-title)]
          (aset js/document "title" new-title))
        (catch :default _)))))

 (reg-event-fx
  :set-title
  (fn [{:keys [:db]} [_ title]]
    {:db        (assoc db :title title)
     :set-title title}))

 (reg-event-db
  :set-active-route
  (fn [db [_ route]]
    (assoc db :active-route route)))

 (reg-event-db
  ::toggle-mobile-menu
  (fn [db _]
    (assoc db :mobile-menu-open (not (:mobile-menu-open db)))))

  (reg-event-db
  ::close-mobile-menu
  (fn [db _]
    (assoc db :mobile-menu-open false)))

  (reg-event-db
  ::toggle-language-menu
  (fn [db _]
    (assoc db :language-menu-open (not (:language-menu-open db)))))

 (reg-event-db
  ::close-language-menu
  (fn [db _]
    (assoc db :language-menu-open false)))


;; readme
 (reg-event-fx
  ::good-readme-result
  (fn [{:keys [:db]} [_ res]]
    {:db (assoc db :readme {:http-status :result
                            :http-result (get-in res [:readme])})}))

 (reg-event-fx
  ::bad-readme-result
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (assoc db :readme {:http-status :error
                            :http-error "error"})}))

 (reg-event-fx
  ::reget-readme
  (fn [{:keys [:db]} _]
    {:db (assoc db :readme {:http-status :empty})
     :dispatch [::get-readme]}))

 (reg-event-fx
  ::get-readme
  (fn [{:keys [:db]} _]
    (if (or (= (get-in db [:readme :http-status]) :empty)
            (= (get-in db [:readme :http-status]) :loading))
      {:db (assoc db :readme {:http-status :loading})
       :http-fx {:uri        "/api/readme"
                 :ssr-wait [:readme]
                 :on-success [::good-readme-result]
                 :on-failure [::bad-readme-result]}}
      {})))


;; url
 (reg-event-fx
  ::good-url-result
  (fn [{:keys [:db]} [_ res]]
    {:db (assoc db :url {:http-status :result
                         :http-result (get-in res [:url])})}))

 (reg-event-fx
  ::bad-url-result
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (assoc db :url {:http-status :error
                         :http-error "error"})}))

 (reg-event-fx
  ::reget-url
  (fn [{:keys [:db]} _]
    {:db (assoc db :url {:http-status :empty})
     :dispatch [::get-url]}))

 (reg-event-fx
  ::get-url
  (fn [{:keys [:db]} _]
    (if (or (= (get-in db [:url :http-status]) :empty)
            (= (get-in db [:url :http-status]) :loading))
      {:db (assoc db :url {:http-status :loading})
       :http-fx {:uri        "/api/test"
                 :ssr-wait [:url]
                 :on-success [::good-url-result]
                 :on-failure [::bad-url-result]}}
      {})))

;; poe
 (reg-event-fx
  ::good-poe-result
  (fn [{:keys [:db]} [_ res]]
    {:db (assoc db :poe {:http-status :result
                         :http-result (get-in res [:quote])})}))

 (reg-event-fx
  ::bad-poe-result
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (assoc db :poe {:http-status :error
                         :http-error "error"})}))

 (reg-event-fx
  ::reget-poe
  (fn [{:keys [:db]} _]
    {:db (assoc db :poe {:http-status :empty})
     :dispatch [::get-poe]}))

 (reg-event-fx
  ::get-poe
  (fn [{:keys [:db]} _]
    (if (and config/client?
         (= (get-in db [:poe :http-status]) :empty))
      {:db (-> db
               (assoc :poe {:http-status :loading}))
       :http-fx {:uri  "/api/poe"
                 :on-success [::good-poe-result]
                 :on-failure [::bad-poe-result]}}
      {}))))
