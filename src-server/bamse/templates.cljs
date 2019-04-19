(ns bamse.templates
  (:require [bamse.config :as config]))

(defn index [{:keys [body state url]}]
  (let [new-title       (if (:title state) (str config/default-title " - " (:title state)) config/default-title)
        new-description (if (:description state) (:description state) config/default-description)
        lang            (name (:language state))
        og              (:og state)
        og-title        (or (:title og) new-title)
        og-type         (or (:type og) "article")
        og-url          (or (:url og) url)
        og-image        (or (:image og) config/default-image)
        og-description  (or (:description og) new-description)]
    [:html {:lang lang}
     [:head
      [:title new-title]

      [:meta {:charset "utf-8"}]
      [:meta {:name    "viewport"
              :content "width=device-width, initial-scale=1.0"}]

      [:meta {:name    "description"
              :content new-description}]
      [:meta {:property "og:title"
              :content  og-title}]
      [:meta {:property "og:type"
              :content  og-type}]
      [:meta {:property "og:url"
              :content  og-url}]
      [:meta {:property "og:image"
              :content  og-image}]
      [:meta {:property "og:description"
              :content  og-description}]

      [:link {:rel  "stylesheet"
              :href "/css/main.css"}]

      [:link {:rel "manifest"
              :href "/manifest.json"}]
      [:link {:rel   "apple-touch-icon"
              :sizes "180x180"
              :href  "/apple-touch-icon.png"}]
      [:link {:rel   "icon"
              :type  "image/png"
              :sizes "32x32"
              :href  "/favicon-32x32.png"}]
      [:link {:rel   "icon"
              :type  "image/png"
              :sizes "16x16"
              :href  "/favicon-16x16.png"}]
      [:link {:rel   "mask-icon"
              :href  "/safari-pinned-tab.svg"
              :color "#3f6f7d"}]
      [:meta {:name    "msapplication-TileColor"
              :content "#00aba9"}]
      [:meta {:name    "theme-color"
              :content "#ffffff"}]]

     [:body
      [:div#app [body]]
      [:div#server-data {:style      {:display "none"}
                         :data-state (pr-str state)}]
      [:script {:src "/js/main.js"}]
      [:script {:dangerouslySetInnerHTML {:__html "bamse.browser.init();"}}]]]))
