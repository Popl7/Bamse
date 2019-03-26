(ns bamse.helpers
  (:require [re-frame.core :as re-frame]
            [cljs.spec.alpha :as s]
            [day8.re-frame.http-fx :as http-fx]
            [ajax.core :as ajax]
            [goog.string :as gstring]
            [goog.string.format]
            [cemerick.url :refer [url url-encode]]
            [bamse.config :as config]
            [re-frame-redux.core :as redux :refer [redux-debug]]))

;; Translations
(defn tr [lang s & args]
  (let [string (or (get-in config/dictionary [lang s]) s)]
    (apply gstring/format string args)))

(defn trn [lang strings count & args]
  (let [[singular plural] (or (get-in config/dictionary [lang strings]) strings)]
    (if (= 1 count)
      (apply gstring/format singular (conj args count))
      (apply gstring/format plural   (conj args count)))))


;; Views
(defn spinner []
 [:div.spinner-border.text-primary {:role :status
                                    :style {:fontSize 15
                                            :width 50
                                            :height 50
                                            :marginTop 15
                                            :marginBottom 15}}
  [:span.sr-only "Loading..."]])

(defn user-avatar-url [user & [size]]
  (let [base-url "https://robohash.org/"
        extension ".png"
        url (url base-url (url-encode (str (:first-name user) (:last-name user) extension)))
        size (or size 120)
        img-set "set4"
        format (str size "x" size)]
    (str (assoc url :query {:set img-set
                            :size format}))))

(defn user-avatar [user & [size]]
  (let [size (or size 120)
        imgSize (- size 2)]
    [:img.md-avatar.rounded {:src (user-avatar-url user imgSize)
                             :alt "Avatar"
                             :style {:verticalAlign :middle
                                     :width         size
                                     :height        size
                                     :border        "1px solid lightgrey"}}]))


;; Interceptors
;;
(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "[Db]: invalid: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (re-frame/after (partial check-and-throw :bamse.db/db)))

(def ssr-waits
  (re-frame.core/->interceptor
   :id      :ssr-waits
   :after (fn [{:keys [effects] :as context}]
            (let [http-fx (:http-fx effects)
                  ssr-wait (:ssr-wait http-fx)
                  db (:db effects)]
              (if (and http-fx ssr-wait)
                (let [new-db (assoc db :ssr-waits (conj (:ssr-waits db) ssr-wait))]
                  (assoc-in context [:effects :db] new-db))
                context)))))

(re-frame/reg-event-fx
 ::debug
 (fn [_ [_ val]]
   (println "debug event: " val)))


;; Default Interceptors
;;
(def standard-interceptors-db
  [(when config/redux? redux-debug)
   check-spec-interceptor
   ssr-waits])

(def standard-interceptors-fx
  [(when config/redux? redux-debug)
   check-spec-interceptor
   ssr-waits])


;; Custom registrations
;;
; (def reg-event-db redux/reg-event-db)
; (def reg-event-fx redux/reg-event-fx)

(defn reg-event-db
  ([id handler-fn]
   (re-frame/reg-event-db id
                    standard-interceptors-db
                    handler-fn))
  ([id interceptors handler-fn]
   (re-frame/reg-event-db id
                    [standard-interceptors-db interceptors]
                    handler-fn)))

(defn reg-event-fx
  ([id handler-fn]
   (re-frame/reg-event-fx id
                    standard-interceptors-fx
                    handler-fn))
  ([id interceptors handler-fn]
   (re-frame/reg-event-fx id
                    [standard-interceptors-fx interceptors]
                    handler-fn)))

;; Custom ajax effect
;;
(re-frame/reg-fx
  :http-fx
  (fn [{:keys [token] :as options}]
    (let [defaults     {:method          :get
                        :timeout         config/ajax-timeout
                        :format          (ajax/json-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [::debug]
                        :on-failure      [::debug]}

          access-token (:access-token token)

          interceptors []
          interceptors (if access-token
                         (conj interceptors (ajax/to-interceptor {:name    "Token Interceptor"
                                                                  :request #(assoc-in % [:headers :Authorization] (str "Bearer " access-token))}))
                         interceptors)
          interceptors {:interceptors interceptors}

          options (assoc options :uri (str config/api-server (:uri options)))
          options (merge defaults interceptors options)]

      (http-fx/http-effect options))))
