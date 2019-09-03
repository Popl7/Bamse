(ns bamse.users.events
 (:require
  [ajax.core :as ajax]
  [bamse.helpers :refer [reg-event-db reg-event-fx user-avatar-url]]))

(defn register []

;; users
 (reg-event-fx
  ::get-users-success
  (fn [{:keys [:db]} [_ res]]
    {:db (assoc db :users {:http-status :result
                           :http-result (get-in res [:users])})}))

 (reg-event-fx
  ::get-users-failure
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (-> db
             (assoc :users {:http-status :error
                            :http-error "error"}))}))

 (reg-event-fx
  ::reget-users
  (fn [{:keys [:db]} _]
    {:db (assoc db :users {:http-status :empty})
     :dispatch [::get-users]}))

 (reg-event-fx
  ::get-users
  (fn [{:keys [:db]} _]
    (if (= (get-in db [:users :http-status]) :empty)
      {:db (assoc db :users {:http-status :loading})
       :http-fx {:uri  "/api/users"
                 :ssr-wait [:users]
                 :on-success [::get-users-success]
                 :on-failure [::get-users-failure]}}
      {})))


;; add-user
 (reg-event-fx
  ::add-user-success
  (fn [{:keys [:db]} _]
    {:db (-> db
             (assoc :users {:http-status :empty})
             (assoc :add-user nil))
     :navigate-to "/users"}))

 (reg-event-fx
  ::add-user-failure
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (-> db
             (assoc :add-user {:http-status :error
                               :http-error  "error"}))}))
 (reg-event-fx
  ::add-user
  (fn [{:keys [:db]} [_ user]]
    {:db (assoc db :new-user {:http-status :loading})
     :http-fx {:uri    "/api/users"
               :method :post
               :params user
               :on-success [::add-user-success]
               :on-failure [::add-user-failure]}}))


;; update-user
 (reg-event-fx
  ::update-user-success
  (fn [{:keys [:db]} [_ id]]
    {:db (-> db
          (assoc :users {:http-status :empty})
          (assoc :update-user nil))
     :navigate-to (str "/user/" id)}))

 (reg-event-fx
  ::update-user-failure
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (assoc db :update-user {:http-status :error
                                 :http-error  "error"})}))
 (reg-event-fx
  ::update-user
  (fn [{:keys [:db]} [_ user]]
    {:db (assoc db :update-user {:http-status :loading})
     :http-fx {:uri    (str "/api/user/" (:id user))
               :method :post
               :params user
               :on-success [::update-user-success (:id user)]
               :on-failure [::update-user-failure]}}))


;; delete-user
 (reg-event-fx
  ::delete-user-success
  (fn [{:keys [:db]} _]
    {:db (-> db
          (assoc :users {:http-status :empty})
          (assoc :delete-user nil))
     :navigate-to "/users"}))

 (reg-event-fx
  ::delete-user-failure
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (assoc db :delete-user {:http-status :error
                                 :http-error  "error"})}))
 (reg-event-fx
  ::delete-user
  (fn [{:keys [:db]} [_ id]]
    {:db (assoc db :delete-user {:http-status :loading})
     :http-fx {:uri    (str "/api/user/" id)
               :method :delete
               :format (ajax/text-request-format)
               :on-success [::delete-user-success]
               :on-failure [::delete-user-failure]}}))


;; user
 (reg-event-fx
  ::get-user-success
  (fn [{:keys [:db]} [_ res]]
    (let [user (get-in res [:user])]
      {:db       (-> db
                     (assoc :description (:description user))
                     (assoc :og {:title       (:title user)
                                 :type        "article"
                                 :image       (user-avatar-url user)
                                 :description (:description user)})
                     (assoc-in [:user :item] {:http-status :result
                                              :http-result user}))
       :dispatch-multiple {:set-title       (:first-name user)}})))

 (reg-event-fx
  ::get-user-failure
  (fn [{:keys [:db]} [_ res]]
    (println "BAD" (:status-text res))
    {:db (-> db
           (assoc :status-code 404)
           (assoc-in [:user :item] {:http-status :error
                                    :http-error "error"}))}))

 (reg-event-db
  ::reset-user
  (fn [db _]
   (assoc-in db [:user :id] nil)))

 (reg-event-fx
  ::reget-user
  (fn [{:keys [:db]} [_ id]]
    {:db (assoc-in db [:user :id] nil)
     :dispatch [::get-user id]}))

 (reg-event-fx
   ::get-user
   (fn [{:keys [:db]} [_ id]]
     (if (= (get-in db [:user :id]) id)
      {}
      {:db (assoc db :user {:id id
                            :item {:http-status :loading}})
       :http-fx {:uri  (str "/api/user/" id)
                 :ssr-wait   [:user :item]
                 :on-success [::get-user-success]
                 :on-failure [::get-user-failure]}}))))
