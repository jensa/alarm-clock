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
  (:id doc))

(defn db-get [table sortby limit]
  (take limit (sort-by sortby (get @post-db table))))

(defn search-by-id [table id]
      (filter #(= id (:id %)) (get @post-db table)))

(defn get-posts []
  (deref post-db))

(defn add-post [data]
  (response (db-add "posts" (assoc data :id (utils/uuid)))))

(defn light-post [post]
  (select-keys post [:id :title :time]))

(defn get-popular-posts [limit]
  (map light-post (reverse (db-get "posts" :votes limit))))

(defn get-new-posts [limit]
    (map light-post (reverse (db-get "posts" :time limit))))

(defn get-data-by-id [id]
   (:data (first (search-by-id "posts" id))))

(defroutes app-routes
  (GET "/" [] (content-type (resource-response "index.html" {:root "public"}) "text/html"))
  (route/resources "/")
  (GET "/posts" [] (response (get-posts)))
  (GET "/posts/popular" [] (response (get-popular-posts 3)))
  (GET "/posts/new" [limit] (response (get-new-posts (utils/parse-int limit))))
  (GET "/data" [id] (response (get-data-by-id id)))
  (POST "/posts/add" [data] (add-post (clojure.walk/keywordize-keys (json/read-str data))))
  (route/not-found "Not Found"))

  (def app
    (-> app-routes
        (wrap-params)
        (middleware/wrap-json-response)
        (wrap-defaults api-defaults)))
