(ns bamse.users.subs
 (:require
  [re-frame.core :as re-frame]))

(defn register []
;; users
 (re-frame/reg-sub
  ::users
  (fn [db _]
    (get-in db [:users :http-result])))

 (re-frame/reg-sub
  ::users-loading
  (fn [db _]
    (= (get-in db [:users :http-status]) :loading)))

;; add-user
 (re-frame/reg-sub
  ::add-user-loading
  (fn [db _]
    (= (get-in db [:add-user :http-status]) :loading)))

;; delete-user
 (re-frame/reg-sub
  ::delete-user-loading
  (fn [db _]
    (= (get-in db [:delete-user :http-status]) :loading)))

;; edit user
 (re-frame/reg-sub
  ::update-user-loading
  (fn [db _]
    (= (get-in db [:update-user :http-status]) :loading)))

;; user
 (re-frame/reg-sub
  ::user
  (fn [db _]
    (get-in db [:user :item :http-result])))

 (re-frame/reg-sub
  ::user-loading
  (fn [db _]
    (= (get-in db [:user :item :http-status]) :loading))))
