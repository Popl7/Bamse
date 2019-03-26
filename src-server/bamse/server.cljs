(ns bamse.server
  (:require [cljs.nodejs :as nodejs]
            [bamse.config :as config]
            [bamse.render :as render]
            [bamse.user-routes :as user-routes]
            [bamse.test-routes :as test-routes]
            [cljs.core.async :refer [chan <!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(nodejs/enable-util-print!)

(defonce server (atom nil))

(def express (nodejs/require "express"))
(def cors (nodejs/require "cors"))
(def serve-static (nodejs/require "serve-static"))
(def body-parser (nodejs/require "body-parser"))
(def slow (nodejs/require "connect-slow"))
(def colors (nodejs/require "colors/safe"))
(def cookie-parser (nodejs/require "cookie-parser"))

(defn handle-request [req res]
  (let [ch (chan)
        full-url (str req.protocol "://" req.headers.host req.originalUrl)
        lang (or (-> req
                     (.-cookies)
                     (js->clj :keywordize-keys true)
                     :language
                     keyword)
                 :nl)
        render-chan (render/render-page ch full-url (.-path req) lang)]
    (go
      (.send res (<! render-chan)))))

(defn start-server []
 (let [app  (express)
       port (or js/process.env.PORT 3000)]

  ;; remove server header
  (.disable app "x-powered-by")

  ;; add CORS req.headers.host
  (def corsOpt
   {:allowedHeaders ["Content-Type" "Authorization"]
    :exposedHeaders []
    :origin "*"
    :methods "GET,HEAD,PUT,PATCH,POST,DELETE"})
  (.use app (cors corsOpt))

   ;; parse JSON body
  (.use app (.urlencoded body-parser #js {:extended true}))
  (.use app (.json body-parser))

  ;; parse cookies
  (.use app (cookie-parser))

   ;; on development, slow down api server
  (when config/debug?
    (.use app (slow #js {:url   #"^/api/"
                         :delay 200})))
   ;; add routes
  (.get app "/" handle-request)
  (.use app (serve-static "public"))
  (.use app (user-routes/routes))
  (.use app (test-routes/routes))
  (.get app "*" handle-request)

  ;; start listening
  (.listen app port
           (fn [err]
             (if err
               (println (.bgRed colors "Server start failed"))
               (println "Server started on port" port))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn stop []
  (println (.red colors "Stopping Server..."))
  (when-some [my-server @server]
    (.close my-server)))

(defn start []
  (println (.red colors "Starting server..."))
  (reset! server (start-server)))

(defn restart []
  (stop)
  (start))

(defn ^:export main []
  (start))
