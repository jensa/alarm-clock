(ns alarm-clock.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response response file-response content-type]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [alarm-clock.utils :as utils]))

(def post-db (atom{}))
(defn popular-comp [el1 el2]
    (> (get el1 "votes") (get el2 "votes")))

(defn db-add [table doc]
  (swap! post-db update-in [table] conj doc)
  (clojure.string/join " " ["successfully added" doc]))

(defn db-get [table sortby limit]
  (take limit (sort-by sortby (get @post-db table))))

(defn search-by-name [table name]
      (filter #(= name (:name %)) (get @post-db table)))

(defn get-posts []
  (deref post-db))

(defn add-post [data]
  (response (db-add "posts" (assoc data "id" 4))))

(defn get-popular-posts [limit]
  (reverse (db-get "posts" :votes limit)))

(defroutes app-routes
  (GET  "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (GET  "/posts" [] (response (get-posts)))
  (GET "/posts/popular" [limit] (response (get-popular-posts (utils/parse-int limit))))
  (POST "/posts/add" [data] (add-post (clojure.walk/keywordize-keys (json/read-str data))))
  (route/not-found "Not Found"))

  (def app
    (-> app-routes
        (wrap-params)
        (middleware/wrap-json-response)
        (wrap-defaults api-defaults)))
