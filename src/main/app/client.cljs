(ns app.client
  (:require [app.application :refer [app]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc fragment]]
            [com.wsscode.pathom.core :as p]
            [app.ui :as ui]
            ))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! app ui/Root "app")
  (df/load! app [:task-list/id 1] ui/TaskList)
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))

(comment

  ; Remove all data from the app (The client-side database)
  (reset! (::app/state-atom app) {})

  ; Check the current state of the app (The client-side database)
  (app/current-state app)

  ; Check the Task description value
  (comp/get-state app :root/task :task/description)

  ; Render the app to the DOM
  (app/schedule-render! app)

)
