(ns app.resolvers
  (:require
    [com.wsscode.pathom.connect :as pc]
    [datomic.api :as d]
    [taoensso.timbre :as log]
    [app.schema :as schema]))

;; Build a task map using a query to get a task by id from the database
(defn get-task-by-id [env id]
  (let [db      (schema/get-db env)
        map-keys [:task/id :task/description :task/completed :task/list-id]
        task-by-id-q (:task-by-id-q schema/queries)]
    (log/info "Query and processing [ :task" id "] from database")
    (->> (d/q task-by-id-q db id)                           ; Query the database
         (first)                                            ; Get the first result (there should only be one)
         (rest)                                             ; Remove the first element (the entity id), it is handled by query
         (zipmap map-keys)                                  ; Zipmap the results to a map
         )))

;; Given a :task/id, return data for that task
(pc/defresolver get-task [env {:task/keys [id]}]
                {::pc/input  #{:task/id}
                 ::pc/output [:task/id :task/description :task/completed :task/list-id]}
                (log/info "Calling get-task resolver")
                (get-task-by-id env id))

(defn mvec-to-mmap [keys multi-vec]
  "Convert a vector of vectors to a map of maps using keys for the inner maps values
  It is assumed that the first element of each vector is the entity id, so it is discarded"
  (map (fn [m-vec]
         (zipmap keys (rest m-vec))) multi-vec))

(defn get-task-list-by-id [env id]
  (let [db      (schema/get-db env)
        map-keys [:task-list/id :task-list/item-count :task-list/tasks]
        task-list-by-id-q (:task-list-by-id-q schema/queries)
        results (d/q task-list-by-id-q db id)]
    ; Results:> [[17592186045422 1 3 3], [17592186045422 1 3 2], [17592186045422 1 3 1]]
    ; Target format: #:task-list{:id 1, :item-count 3, :tasks [#:task{:id 1} #:task{:id 2} #:task{:id 3}]}
    ; Generate the map in target format
    (log/info "Query and processing [ :task-list" id "] from database")
    (let [mmap-task-list (mvec-to-mmap map-keys results)
          task-list (select-keys (first mmap-task-list) [:task-list/id :task-list/item-count])]
      (->> mmap-task-list
           (map :task-list/tasks)
           (mapv (fn [id] {:task/id id}))
           (assoc task-list :task-list/tasks)))))

;; Given a :task-list/id, this can generate a list id item-count and the tasks
(pc/defresolver get-task-list [env {:task-list/keys [id]}]
                {::pc/input  #{:task-list/id}
                 ::pc/output [:task-list/id :task-list/item-count {:task-list/tasks [:task/id]}]}
                (log/info "Calling get-task-list resolver")
                (get-task-list-by-id env id))

(def resolvers [get-task
                get-task-list])
