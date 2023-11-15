(ns app.schema
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/todo")

(def tasks-schema
  [{:db/ident       :task/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Task id"}

   {:db/ident       :task/description
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Task description"}

   {:db/ident       :task/completed
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "Task completed"}

   {:db/ident       :task/list-id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "Task list id"}])

; returns a connection to the database
(defn get-conn []
  (d/connect db-uri))

; Initialize the database with the schema
(defn init-db []
  (d/create-database db-uri)
  @(d/transact (get-conn) tasks-schema))

;Get a current value of the database to issue your query against it
(defn get-db []
  (d/db (get-conn)))

