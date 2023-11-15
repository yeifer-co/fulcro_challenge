(ns app.mutations
  (:require
    [app.resolvers :refer [tasks-list-table]]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(pc/defmutation delete-task [env {task-list-id :task-list/id
                                  task-id :task/id}]
                {::pc/sym `delete-task}
                (log/info "Delete task" task-id "from task list" task-list-id)
                (swap! tasks-list-table update task-list-id update :task-list/tasks (fn [old-list] (filterv #(not= task-id %) old-list))))

(def mutations [delete-task])