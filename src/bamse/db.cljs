(ns bamse.db
  (:require [cljs.spec.alpha :as s]))

(def default-db
  {:title            nil
   :active-route     nil
   :language         :en
   :mobile-menu-open false
   :ssr-waits     (set '())
   :readme           {:http-status :empty}
   :url              {:http-status :empty}
   :poe              {:http-status :empty}
   :user             {:id   nil
                      :item {:http-status :empty}}
   :users            {:http-status :empty}})

(s/def ::db (s/keys :req-un [::title
                             ::active-route
                             ::mobile-menu-open
                             ::ssr-waits
                             ::url
                             ::poe
                             ::user
                             ::users]))

(s/def ::http-status #{:empty
                       :loading
                       :error
                       :result})

(s/def ::http-result #{(s/nilable string?)
                       (s/nilable array?)})

(s/def ::http-error (s/nilable string?))

(s/def ::http-request (s/keys :req-un [::http-status]
                              :opt-un [;; ::http-result
                                       ::http-error]))

(s/def ::title (s/nilable string?))
(s/def ::active-route (s/nilable map?))
(s/def ::mobile-menu-open boolean?)
(s/def ::ssr-waits set?)

(s/def ::readme ::http-request)
(s/def ::url ::http-request)
(s/def ::poe ::http-request)
(s/def ::id (s/nilable int?))
(s/def ::item ::http-request)

(s/def ::user (s/keys :req-un [::id
                               ::item]))
(s/def ::users ::http-request)
