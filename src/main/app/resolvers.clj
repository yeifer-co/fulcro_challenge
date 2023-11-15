(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

; (def tasks-schema
;  [{:db/ident       :task/id
;    :db/valueType   :db.type/long
;    :db/cardinality :db.cardinality/one
;    :db/unique      :db.unique/identity
;    :db/doc         "Task id"}
;
;   {:db/ident       :task/description
;    :db/valueType   :db.type/string
;    :db/cardinality :db.cardinality/one
;    :db/doc         "Task description"}
;
;   {:db/ident       :task/completed
;    :db/valueType   :db.type/boolean
;    :db/cardinality :db.cardinality/one
;    :db/doc         "Task completed"}])

(def tasks-table
  {1 {:task/id          1
      :task/description "Do the dishes"
      :task/completed   true
      :task/list-id     1}
   2 {:task/id          2
      :task/description "Take out the trash"
      :task/completed   false
      :task/list-id     1}
   3 {:task/id          3
      :task/description "Make the bed"
      :task/completed   true
      :task/list-id     1}})

(def tasks-list-table
  {1 {:task-list/id          1
      :task-list/item-count  3
      :task-list/tasks       [1 2 3]}})

;; Given a :task/id, return data for that task
(pc/defresolver get-task [env {:task/keys [id]}]
                {::pc/input  #{:task/id}
                 ::pc/output [:task/id :task/description :task/completed :task/list-id]}
                (get tasks-table id))

;; Given a :task-list/id, this can generate a list item-count and the tasks
;; in that list (but just with their IDs)
(pc/defresolver get-task-list [env {:task-list/keys [id]}]
                {::pc/input  #{:task-list/id}
                 ::pc/output [:task-list/id :task-list/item-count {:task-list/tasks [:task/id]}]}
                (when-let [task-list (get tasks-list-table id)]
                  (assoc task-list
                         :task-list/tasks (mapv (fn [id] {:task/id id}) (:task-list/tasks task-list)))))

(def resolvers [get-task get-task-list])