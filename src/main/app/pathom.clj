(ns app.pathom
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]
            [datomic.api :as d]
            [app.db-schema :as schema]))

(pc/defresolver read-task
             [_ {:keys [id]}]
             (let [db (schema/get-db)]
               (d/q '[:find ?description ?completed
                      :where [?task-id :task/id ?id]
                      [?task-id :task/description ?description]
                      [?task-id :task/completed ?completed]]
                    db id)))

(pc/defresolver read-task
                [{::keys [id]} _]
                (let [task (d/q '[:find ?description ?completed
                                  :where [?task :task/id ?id]
                                  [?task :task/description ?description]
                                  [?task :task/completed ?completed]]
                                (schema/get-db) id)]
                  {:task/id          id
                   :task/description (second task)
                   :task/completed   (boolean (nth task 2))}))

(pc/defresolver list-tasks
             [_ _]
             (let [db (schema/get-db)]
               (d/q '[:find ?id ?description ?completed
                      :where [?task-id :task/id ?id]
                      [?task-id :task/description ?description]
                      [?task-id :task/completed ?completed]]
                    db)))

;; Define other resolvers for create, update, and delete tasks as needed
(pc/defmutation create-task [{:keys [description completed]}]
             (action
               [{:keys [state]}]
               (let [new-id (d/tempid :db.part/user)]
                 (pc/transact! state [{:db/id new-id
                                       :task/id new-id
                                       :task/description description
                                       :task/completed completed}]))))

(pc/defmutation update-task [{:keys [id description completed]}]
             (action
               [{:keys [state]}]
               (pc/transact! state [{:db/id id
                                     :task/id id
                                     :task/description description
                                     :task/completed completed}])))

(pc/defmutation delete-task [{:keys [id]}]
             (action
               [{:keys [state]}]
               (pc/transact! state [{:db/id id :task/id id :db/retractEntity true}])))

; Test data
(pc/defresolver latest-task [_ _]
                {::pc/output [{::latest-task [:task/id :task/description :task/completed]}]}
                {::latest-task {:task/id          3
                                :task/description "Task Example"
                                :task/completed   true}})

(def parser
  (p/parser
    {::p/env  {::p/reader [p/map-reader
                           pc/reader2
                           pc/open-ident-reader
                           p/env-placeholder-reader]
               ::p/placeholder-prefixes #{">"}}
     ::p/mutate pc/mutate
     ::p/plugins [(pc/connect-plugin {::pc/register [read-task
                                                     list-tasks
                                                     create-task
                                                     update-task
                                                     delete-task
                                                     latest-task
                                                     ]})
                    p/error-handler-plugin
                    p/trace-plugin]}))
