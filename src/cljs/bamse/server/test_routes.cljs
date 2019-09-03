(ns bamse.server.test-routes
  (:require [cljs.nodejs :as nodejs])
  (:require-macros [bamse.macros :refer [load-readme]]))

(def express (nodejs/require "express"))
(def markdown ((nodejs/require "markdown-it") "default"))

(def readme
  (load-readme))

(defn api-error [req res]
 (println "test api - 404" (.-path req))
 (-> res
     (.status 404)
     (.send (clj->js {:error "Not a valid api url"}))))


(defn api-readme [_ res]
 (println "readme api - 200")
 (-> res
     (.status 200)
     (.send (clj->js {:readme (.render markdown readme)}))))

(defn api-test [_ res]
  (println "test api - 200")
  (-> res
      (.status 200)
      (.send (clj->js {:url (.render markdown "Hello **Bamse**!\n\nGreetings from the Node API server")}))))

(defn api-poe [_ res]
  (println "poe api - 200")
  (-> res
      (.status 200)
      (.send (clj->js {:quote "The now, dreary, borrow. nodded, silence Lenore! distinctly wished purple and
Lenore! a 'Tis And my one my the at nothing the no said the separate Once the
gently upon the this, dreaming the stood beating my dreams Darkness I the word
door. upon of Lenore. and chamber Madam, came Only faintly door. longer. Merely
dying the heart, still remember pondered, I angels This so was fantastic of
door. more. came whispered or the with entrance quaint weak tapping, door. I
morrow;—vainly Presently fact and to long radiant visiter my more. And came
for nothing you, it token. sad for there Nameless the I, sorrow—sorrow more."}))))

(defn routes []
  (let [router (.Router express)]
   (.get router "/api/readme" api-readme)
   (.get router "/api/test" api-test)
   (.get router "/api/poe" api-poe)
   (.all router "/api/*" api-error)))
