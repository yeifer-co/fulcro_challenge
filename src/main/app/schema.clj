(ns app.schema
  (:require
    [datomic.api :as d]
    [com.wsscode.pathom.connect :as pc]
    ))

(def db-uri "datomic:dev://localhost:4334/todo")

(defn get-conn
  ([env] (get env ::pc/connection))
  ([] (d/connect db-uri)))

(defn get-db
  ([env] (d/db (get-conn env)))
  ([] (d/db (get-conn))))

(def queries
  {;; task queries
   :all-tasks-q       '[:find ?e ?id ?description ?completed ?list-id
                       :where
                       [?e :task/id ?id]
                       [?e :task/description ?description]
                       [?e :task/completed ?completed]
                       [?e :task/list-id ?list-id]]

   :get-task-ident-q  '[:find ?e
                        :in $ ?id
                        :where
                        [?e :task/id ?id]]

   :task-by-id-q      '[:find ?e ?id ?description ?completed ?list-id
                        :in $ ?id
                        :where
                        [?e :task/id ?id]
                        [?e :task/description ?description]
                        [?e :task/completed ?completed]
                        [?e :task/list-id ?list-id]]

   ;; task-list queries
   :all-tasks-lists-q     '[:find ?e ?id ?item-count ?tasks
                           :where
                           [?e :task-list/id ?id]
                           [?e :task-list/item-count ?item-count]
                           [?e :task-list/tasks ?tasks]]

   :get-task-list-ident-q '[:find ?e
                            :in $ ?id
                            :where
                            [?e :task-list/id ?id]]

   :task-list-by-id-q     '[:find ?e ?id ?item-count ?tasks
                            :in $ ?id
                            :where
                            [?e :task-list/id ?id]
                            [?e :task-list/item-count ?item-count]
                            [?e :task-list/tasks ?tasks]]

   :task-list-item-count-q '[:find ?item-count
                             :in $ ?id
                             :where
                             [?e :task-list/id ?id]
                             [?e :task-list/item-count ?item-count]]
   })

(def schema
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
    :db/doc         "Task list id"}

   {:db/ident       :task-list/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Task list id"}

   {:db/ident       :task-list/item-count
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "Task list item count"}

   {:db/ident       :task-list/tasks
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "Task list tasks"}])

(def data-dummy
  {:tasks-table [{:task/id          1
                  :task/description "Hacking into Monday's source code 🖥"
                  :task/completed   false
                  :task/list-id     1}
                 {:task/id          2
                  :task/description "Compiling a recipe for success 📝"
                  :task/completed   true
                  :task/list-id     1}
                 {:task/id          3
                  :task/description "Rebooting my social skills 🤖"
                  :task/completed   true
                  :task/list-id     1}
                 {:task/id          4
                  :task/description "Password-protecting my dreams 🔐"
                  :task/completed   false
                  :task/list-id     1}]

   :tasks-list-table [{:task-list/id         1
                       :task-list/item-count 4
                       :task-list/tasks      [1 2 3 4]}]})

(comment

  (require '[app.schema :as schema] :reload)

  ; Add some initial data

  @(d/transact (get-conn) (:tasks-table data-dummy))
  @(d/transact (get-conn) (:tasks-list-table data-dummy))

  ; Get database values

  (d/q (:all-tasks-q queries) (get-db))
  (d/q (:all-tasks-lists-q queries) (get-db))

  ; Update completed field for task 2

  ; Using :db/add
  @(d/transact (get-conn) [[:db/add
                            (ffirst (d/q (:get-task-ident-q queries) (get-db) 2))
                            :task/completed false]])

  ; Using :db/id
  @(d/transact (get-conn) [{:db/id
                            (ffirst (d/q (:get-task-ident-q queries) (get-db) 2))
                            :task/completed false}])

  ; Update description field for task 2

  ; Using :db/add
  @(d/transact (get-conn) [[:db/add
                            (ffirst (d/q (:get-task-ident-q queries) (get-db) 2))
                            :task/description "Installing a firewall against negativity 🙂"]])

  ; Using :db/id
  @(d/transact (get-conn) [{:db/id
                            (ffirst (d/q (:get-task-ident-q queries) (get-db) 2))
                            :task/description "Trying to find the 'Any' key 🙁"}])




  ; Require the datomic library
  (require '[datomic.api :as d])

  ; Create a connection to the database
  (def db-uri "datomic:dev://localhost:4334/todo")

  ; Create the database
  (d/create-database db-uri)

  ; Connect to the database
  (def conn (d/connect db-uri))

  ; Transact the schema
  @(d/transact conn schema)

  ; Add some initial data
  (def tasks-table
    [{:task/id          1
      :task/description "Hacking into Monday's source code"
      :task/completed   false
      :task/list-id     1}
     {:task/id          2
      :task/description "Compiling a recipe for success"
      :task/completed   true
      :task/list-id     1}
     {:task/id          3
      :task/description "Rebooting my social skills"
      :task/completed   true
      :task/list-id     1}
     {:task/id          4
      :task/description "Password-protecting my dreams"
      :task/completed   false
      :task/list-id     1}])

  (def tasks-list-table
    [{:task-list/id         1
      :task-list/item-count 4
      :task-list/tasks      [1 2 3 4]}])

  @(d/transact conn tasks-table)
  @(d/transact conn tasks-list-table)

  ; Get a current value of the database to issue your query against it

  ; Get database value
  (def db (d/db conn))

  (def all-tasks-q '[:find ?e ?id ?description ?completed ?list-id
                     :where
                     [?e :task/id ?id]
                     [?e :task/description ?description]
                     [?e :task/completed ?completed]
                     [?e :task/list-id ?list-id]])

  (d/q all-tasks-q db)

  ; Get a single task by id
  (def task-by-id-q '[:find ?e ?id ?description ?completed ?list-id
                      :in $ ?id
                      :where
                      [?e :task/id ?id]
                      [?e :task/description ?description]
                      [?e :task/completed ?completed]
                      [?e :task/list-id ?list-id]])

  (d/q task-by-id-q db 2)

  ; New value for task 2 description

  ; Get entity id for task 2

  (def task-2-id (ffirst (d/q '[:find ?e
                                :where
                                [?e :task/id 2]]
                              db)))

  @(d/transact conn [{:db/id            task-2-id
                      :task/description "Take out the trash :)"}])

  ; Get entity id for task list 1
  (def task-list-1-id (ffirst (d/q '[:find ?e
                                     :where
                                     [?e :task-list/id 1]]
                                   db)))

  ; Query for all tasks-lists
  (def all-tasks-lists-q '[:find ?e ?id ?item-count ?tasks
                           :where
                           [?e :task-list/id ?id]
                           [?e :task-list/item-count ?item-count]
                           [?e :task-list/tasks ?tasks]])

  (d/q all-tasks-lists-q db)

  (def new-task
    [{:task/id          4
      :task/description "Do the laundry"
      :task/completed   false
      :task/list-id     1}])

  ; Add a new task
  @(d/transact conn new-task)

  ; Assing task 4 to task list 1, so add 4 to task-list/tasks and change ite-count to 4
  (def task-list-1-id (ffirst (d/q '[:find ?e
                                     :where
                                     [?e :task-list/id 1]]
                                   db)))

  ; Unreference task 4 from task-list/tasks and change item-count to 3
  @(d/transact conn [[:db/retract task-list-1-id :task-list/tasks 4]])

  ; Add task 4 to task-list/tasks and change item-count to 4
  @(d/transact conn [[:db/add task-list-1-id :task-list/tasks 4]
                     {:db/id task-list-1-id :task-list/item-count 4}])

  ; Get entity id for a task-list using :task-list/id provided
  (def get-task-list-id [task-list-id]
    (ffirst (d/q '[:find ?e
                   :where
                   [?e :task-list/id ?id]]
                 db task-list-id)))

  ; Delete task 4, use retract to delete the entity
  (def task-4-id (ffirst (d/q '[:find ?e
                                :where
                                [?e :task/id 4]]
                              db)))

  ; Retract field :task/id for task 4
  @(d/transact conn [[:db/retract task-4-id :task/id 4]])

  ; Retract entity task 4
  @(d/transact conn [[task-4-id :db/retractEntity true]])

  ; Get entity id for a task using :task/id provided
  (defn get-task-id [task-id]
    (ffirst (d/q '[:find ?e
                   :where
                   [?e :task/id ?id]]
                 db task-id)))


  ; Get entity id for task 4
  (def task-4-id (ffirst (d/q '[:find ?e
                                :where
                                [?e :task/id 4]]
                              db)))

  ; Change :task/list-id for task 4 to 1
  @(d/transact conn [{:db/id        task-4-id
                      :task/list-id 1}])

  ; Get current database value

  (def db (d/db conn))

  (d/q all-tasks-q db)
  (d/q all-tasks-lists-q db)

  )

