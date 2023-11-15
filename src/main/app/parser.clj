(ns app.parser
  (:require
    [app.resolvers]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(def resolvers [app.resolvers/resolvers])

(def pathom-parser
  (p/parser
    {::p/env     {::p/reader                 [p/map-reader
                                              pc/reader2
                                              pc/ident-reader
                                              pc/index-reader]
                  ::pc/mutation-join-globals [:tempids]}
     ::p/mutate  pc/mutate
     ::p/plugins [(pc/connect-plugin {::pc/register [resolvers]})
                  p/error-handler-plugin
                  (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query)
  )

(comment
  (require 'app.parser)

  ;;; Given a :task/id, return data for that task
  ;(pc/defresolver get-task [env {:task/keys [id]}]
  ;                {::pc/input  #{:task/id}
  ;                 ::pc/output [:task/id :task/description :task/completed :task/list-id]}
  ;                (get tasks-table id))
  ;

  ; Call get-task resolver
  (app.parser/api-parser [{[:task/id 2] [:task/id
                                         :task/description
                                         :task/completed
                                         :task/list-id
                                         ]}])

  ;;; Given a :task-list/id, this can generate a list item-count and the tasks
  ;;; in that list (but just with their IDs)
  ;(pc/defresolver get-task-list [env {:task-list/keys [id]}]
  ;                {::pc/input  #{:task-list/id}
  ;                 ::pc/output [:task-list/id :task-list/item-count {:task-list/tasks [:task/id]}]}
  ;                (when-let [task-list (get tasks-list-table id)]
  ;                  (assoc task-list
  ;                         :task-list/tasks (mapv (fn [id] {:task/id id}) (:task-list/tasks task-list)))))

  ; Call get-task-list resolver
  (app.parser/api-parser [{[:task-list/id 1] [:task-list/id
                                              :task-list/item-count
                                              {:task-list/tasks [:task/id]}]}])

  )