(ns app.client
(:require [com.fulcrologic.fulcro.application :as app]
          [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
          [com.fulcrologic.fulcro.dom :as dom]))

(defonce app (app/fulcro-app))                              ; defonce ensures that the app is only created once, it is like a constant, but really it is a singleton pattern

(defsc Root [this props]
  (dom/div (str props)))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! app Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))

(comment

  (reset! (::app/state-atom app) {:foo "bar"})
  (app/schedule-render! app)

  )