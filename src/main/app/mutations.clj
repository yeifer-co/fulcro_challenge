(ns app.mutations
  (:require
    [com.wsscode.pathom.connect :as pc]
    [datomic.api :as d]
    [taoensso.timbre :as log]
    [app.schema :as schema]))

(defn retract-task-by-id [env id]
  (let [conn (schema/get-conn env)
        db (schema/get-db env)
        get-task-ident-q (:get-task-ident-q schema/queries)
        task-ident (ffirst (d/q get-task-ident-q db id))]
    (log/info "Retracting task" id)
    @(d/transact conn [[:db/retract task-ident :task/id id]]))
  nil) ; Return nil to avoid returning the task map (it is not handled by the mutation)

(defn retract-task-from-list [env list-id task-id]
  (let [conn (schema/get-conn env)
        db (schema/get-db env)
        get-task-list-ident-q (:get-task-list-ident-q schema/queries)
        task-list-ident (ffirst (d/q get-task-list-ident-q db list-id))]
    (log/info "Retracting task" task-id "from list" list-id)
    @(d/transact conn [[:db/retract task-list-ident :task-list/tasks task-id]]))
  nil) ; Return nil to avoid returning the task map (it is not handled by the mutation)

(pc/defmutation delete-task [env {:keys [id list-id]}]
                {::pc/sym `delete-task}
                (log/info "Calling delete-task mutation")
                (retract-task-by-id env id)
                (retract-task-from-list env list-id id))

(defn transact-task-completed [env id new-value]
  (let [conn (schema/get-conn env)
        db (schema/get-db env)
        get-task-ident-q (:get-task-ident-q schema/queries)
        task-ident (ffirst (d/q get-task-ident-q db id))]
    (log/info "Updating task" id "field `completed` to (" new-value ")")
    @(d/transact conn [{:db/id task-ident :task/completed new-value}]))
  nil) ; Return nil to avoid returning the task map (it is not handled by the mutation)

(pc/defmutation edit-completed [env {:keys [id new-value]}]
                {::pc/sym `edit-completed}
                (log/info "Calling edit-completed mutation")
                (transact-task-completed env id new-value))

(defn transact-task-description [env id new-value]
  (let [conn (schema/get-conn env)
        db (schema/get-db env)
        get-task-ident-q (:get-task-ident-q schema/queries)
        task-ident (ffirst (d/q get-task-ident-q db id))]
    (log/info "Updating task" id "field `description` to (" new-value ")")
    @(d/transact conn [{:db/id task-ident :task/description new-value}]))
  nil) ; Return nil to avoid returning the task map (it is not handled by the mutation

(pc/defmutation edit-description [env {:keys [id new-value]}]
                {::pc/sym `edit-description}
                (log/info "Calling edit-description mutation")
                (transact-task-description env id new-value))

(defn transact-new-task [env list-id description]
  (let [conn (schema/get-conn env)
        db (schema/get-db env)
        get-task-list-ident-q (:get-task-list-ident-q schema/queries)
        task-list-item-count-q (:task-list-item-count-q schema/queries)
        task-list-ident (ffirst (d/q get-task-list-ident-q db list-id))
        new-id (inc (ffirst (d/q task-list-item-count-q db list-id)))
        temp-id (d/tempid :db.part/user)
        ]
    (log/info "Adding new task" new-id "to list" list-id)
    @(d/transact conn [{:db/id temp-id :task/id           new-id
                                      :task/description  description
                                      :task/completed    false
                                      :task/list-id      list-id}
                       [:db/add task-list-ident :task-list/tasks new-id]
                       [:db/add task-list-ident :task-list/item-count new-id]]))
  nil) ; Return nil to avoid returning the task map (it is not handled by the mutation

(pc/defmutation add-task [env {:keys [list-id description]}]
                {::pc/sym `add-task}
                (log/info "Calling add-task mutation")
                (transact-new-task env list-id description))

(def mutations [delete-task
                edit-completed
                edit-description
                add-task])