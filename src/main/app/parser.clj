(ns app.parser
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]
    [app.schema :refer [get-conn]]
    [app.resolvers]
    [app.mutations]))

(def middlewares [app.resolvers/resolvers
                  app.mutations/mutations])

(def pathom-parser
  (p/parser
    {::p/env     {::p/reader                  [p/map-reader
                                               pc/reader2
                                               pc/ident-reader
                                               pc/index-reader]
                  ::pc/mutation-join-globals  [:tempids]
                  ::pc/connection             (get-conn)     ; Database connection (Datomic)
                  }
     ::p/mutate  pc/mutate
     ::p/plugins [(pc/connect-plugin {::pc/register [middlewares]})
                  p/error-handler-plugin
                  (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (log/info "Processing query" query)
  (pathom-parser {} query))

(comment
  (require 'app.parser)

  ; Call get-task resolver
  (app.parser/api-parser [{[:task/id 2] [:task/id
                                         :task/description
                                         :task/completed
                                         :task/list-id
                                         ]}])

  ; Call get-task-list resolver
  (app.parser/api-parser [{[:task-list/id 1] [:task-list/id
                                              :task-list/item-count
                                              {:task-list/tasks [:task/id]}]}])

  ; Call get-task-list resolver and trigger get-task resolver
  (app.parser/api-parser [{[:task-list/id 1] [:task-list/id
                                              :task-list/item-count
                                              {:task-list/tasks [:task/id
                                                                 :task/description
                                                                 :task/completed
                                                                 :task/list-id
                                                                 ]}]}])
  )