(ns app.client
(:require [com.fulcrologic.fulcro.application :as app]
          [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
          [com.fulcrologic.fulcro.dom :as dom]))

(defsc sample [this props]
  {}
  (dom/div nil "Hello Worlda"))

(def app (app/fulcro-app))

(defn ^:export init []
  (app/mount! app sample ("app")))

(defn f [x]
  (println "Called f")
  (+ x 1)
  )

(comment
  (f 1)

  )