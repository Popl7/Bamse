(ns bamse.test-routes
  (:require [cljs.nodejs :as nodejs]))

(def express (nodejs/require "express"))
(def markdown ((nodejs/require "markdown-it") "default"))

(def readme "# Bamse
## Universal Re-frame setup running on Node.js

I added a re-frame frontend and passed the state from the server to the frontend.
Work in progress :-)

## Used libraries and features
* [clojurescript](https://clojurescript.org/)
* [re-frame](https://github.com/Day8/re-frame)
* [shadow-cljs](https://github.com/thheller/shadow-cljs)
* [devcards](https://github.com/bhauman/devcards)
* [bidi](https://github.com/juxt/bidi)
* [pushy](https://github.com/kibu-australia/pushy)
* [devtools](https://github.com/binaryage/cljs-devtools)
* redux time travelling (part of this project :-))

## Demo
[https://bamse.ln2.nl](https://bamse.ln2.nl)

## Development mode
`npm ci`

`npm run watch`

And after compile run:

`npm run dev`

#### App
[http://localhost:3000](http://localhost:3000)

#### Devcards
[http://localhost:3000/devcards.html](http://localhost:3000/devcards.html)

#### Shadow-Cljs dashboard
[http://localhost:9630](http://localhost:9630)

## Production mode
### building
`npm ci`

`npm run release`
### running
`cd resources`

`npm ci --only-production`

`PORT=4000 node server/server.js`

[http://localhost:4000](http://localhost:4000)

## License
The license is MIT")

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
