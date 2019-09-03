(ns bamse.server.user-routes
  (:require [cljs.nodejs :as nodejs]
            [bamse.server.server-db :as db]))

(def express (nodejs/require "express"))


(defn api-users [_ res]
 (db/users (fn [err rows]
             (if err
               (do
                 (println "users api - 500 " err)
                 (-> res
                     (.status 500)
                     (.send (clj->js err.message))))
               (do
                 (println "users api - 200")
                 (-> res
                     (.status 200)
                     (.send (clj->js {:users rows}))))))))

(defn api-user [req res]
  (db/user req.params.id
           (fn [err row]
             (if err
               (do
                 (println "user api - 500 " err)
                 (-> res
                     (.status 500)
                     (.send (clj->js err.message))))
               (do
                 (println "user api - 200")
                 (-> res
                     (.status 200)
                     (.send (clj->js {:user row}))))))))

(defn add-user [req res]
;; validate and save or 500 error
  (let [params (js->clj req.body :keywordize-keys true)]
   (db/add-user params
                (fn [err]
                  (if err
                    (do
                      (println "add user api - 500 " err)
                      (-> res
                          (.status 500)
                          (.send (clj->js err.message))))
                    (do
                      (println "add user api - 200")
                      (-> res
                          (.status 200)
                          (.send (clj->js {:result :ok})))))))))

(defn update-user [req res]
;; validate and save or 500 error
  (let [params (js->clj req.body :keywordize-keys true)]
    (db/update-user params
                    (fn [err]
                      (if err
                        (do
                          (println "update user api - 500 " err)
                          (-> res
                              (.status 500)
                              (.send (clj->js err.message))))
                        (do
                          (println "update user api - 200")
                          (-> res
                              (.status 200)
                              (.send (clj->js {:result :ok})))))))))

(defn delete-user [req res]
  (db/delete-user req.params.id
                  (fn [err]
                    (if err
                      (do
                        (println "delete user api - 500 " err)
                        (-> res
                            (.status 500)
                            (.send (clj->js err.message))))
                      (do
                        (println "delete user api - 200")
                        (-> res
                            (.status 200)
                            (.send (clj->js {:result :ok}))))))))


(defn routes []
  (let [router (.Router express)]
   (.get router "/api/users" api-users)
   (.get router "/api/user/:id" api-user)
   (.post router "/api/users" add-user)
   (.post router "/api/user/:id" update-user)
   (.delete router "/api/user/:id" delete-user)))
