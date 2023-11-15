(comment
  ; Require the datomic library
  (require '[datomic.api :as d])

  ; Create a connection to the database
  (def db-uri "datomic:dev://localhost:4334/todo")

  ; Create the database
  (d/create-database db-uri)

  ; Connect to the database
  (def conn (d/connect db-uri))

  ; Create the schema
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

  ; Transact the schema
  @(d/transact conn schema)

  ; Add some initial data
  (def tasks-table
    [{:task/id          1
      :task/description "Do the dishes"
      :task/completed   true
      :task/list-id     1}
     {:task/id          2
      :task/description "Take out the trash"
      :task/completed   false
      :task/list-id     1}
     {:task/id          3
      :task/description "Make the bed"
      :task/completed   true
      :task/list-id     1}])

  (def tasks-list-table
    [{:task-list/id           1
      :task-list/item-count   3
      :task-list/tasks        [1 2 3]}])

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

  ; New value for task 2 description

  ; Get entity id for task 2

  (def task-2-id (ffirst (d/q '[:find ?e
                                  :where
                                  [?e :task/id 2]]
                                db)))

  @(d/transact conn [{:db/id task-2-id
                      :task/description "Take out the trash and recycling"}])

  ; Get current database value

  (def db (d/db conn))

  (d/q all-tasks-q db)

  )

