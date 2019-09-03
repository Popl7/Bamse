(ns bamse.server.server-db
  (:require [cljs.nodejs :as node]))

(def sqlite (node/require "sqlite3"))

(def db
  (new sqlite.Database "database.db"))

(defn users [handler]
 (.all db
       "SELECT *
        FROM users
        ORDER BY `first-name` COLLATE NOCASE ASC, `last-name` COLLATE NOCASE ASC"
       handler))

(defn user [id handler]
  (.get db
        "SELECT *
         FROM users
         WHERE id=?"
        #js [id]
        handler))

(defn add-user [params handler]
 (.run db
       "INSERT INTO users (`first-name`, `last-name`, `title`, `description`) VALUES (?, ?, ?, ?)"
       #js [(:first-name params) (:last-name params) (:title params) (:description params)]
       handler))

(defn update-user [params handler]
 (.run db
       "UPDATE users SET `first-name`=?, `last-name`=?, `title`=?, `description`=? WHERE id=?"
       #js [(:first-name params) (:last-name params) (:title params) (:description params) (:id params)]
       handler))

(defn delete-user [id handler]
 (.run db
       "DELETE from users WHERE id=?"
       #js [id]
       handler))
