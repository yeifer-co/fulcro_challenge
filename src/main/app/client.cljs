(ns app.client
  (:require ["semantic-ui-react" :refer [Container Segment Button Input]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.dom :as dom :refer [span]]
            [com.fulcrologic.fulcro.dom.events :as events]
            [com.fulcrologic.fulcro.mutations :as mutation :refer [defmutation]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc fragment]]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.core :as p]
            ))

(def sui-container (interop/react-factory Container))
(def sui-segment (interop/react-factory Segment))
(def sui-button (interop/react-factory Button))
(def sui-input (interop/react-factory Input))

(defonce app (app/fulcro-app))
; defonce ensures that the app is only created once, it is like a constant, but really it is a singleton pattern

(defmutation delete-task [{:keys [id list-id]}]
  (action [{:keys [state]}]                                 ; What to do locally
          (swap! state update :task/id dissoc id)
          (swap! state merge/remove-ident* [:task/id id] [:task-list/id list-id :task-list/tasks])))

(defmutation edit-description [{:keys [id new-value]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:task/id id :task/description] new-value)))

(defmutation edit-completed [{:keys [id new-value]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:task/id id :task/completed] new-value)))

(defn on-edit-toggle [this]
  (comp/set-state! this (update (comp/get-state this) :editing? not)))

; Handle event
(defn on-edit-update [this evt]
  (comp/set-state! this (assoc (comp/get-state this) :edit-value (events/target-value evt))))

(defn on-edit-confirm [this mutation id]
  (if (not-empty (comp/get-state this :edit-value))
    (do
      (comp/transact! this [(mutation {:id id :new-value (comp/get-state this :edit-value)})])
      (comp/set-state! this (assoc (comp/get-state this) :editing? false)))))

(defn on-edit-cancel [this]
  (comp/set-state! this (assoc (comp/get-state this) :editing? false)))

(defn on-delete [this mutation id list-id]
  (comp/transact! this [(mutation {:id id :list-id list-id})]))

(defn on-completed-change [this mutation id]
  (comp/set-state! this (update (comp/get-state this) :task-completed? not))
  (comp/transact! this [(mutation {:id id :new-value (comp/get-state this :task-completed?)})]))

(defsc Task [this {:task/keys [id description completed list-id] :as props}]
  {:query [:task/id :task/description :task/completed :task/list-id]
   :ident :task/id
   :initial-state {:task/id           :param/id
                   :task/description  :param/description
                   :task/completed    :param/completed
                   :task/list-id      :param/list-id}
   :initLocalState (fn [this {:task/keys [id description completed] :as props}]
                     {:editing?         false
                      :edit-value       description
                      :task-completed?  completed})}
  (js/console.log "Task render" id)
  (let [{:keys [editing? edit-value task-completed?]} (comp/get-state this)]
    (sui-segment {:key (str "task-" id) :vertical true}
      (if editing?
      (fragment
        (sui-input {:label {:icon "edit" :color (if task-completed? "teal" "yellow") :onClick #(on-edit-toggle this)}
                    :labelPosition "left corner" :defaultValue edit-value :onChange #(on-edit-update this %)
                    :style {:width "88%"}})
        (sui-button {:onClick #(on-edit-cancel this) :color "grey"
                     :floated "right" :icon "times circle outline" :style {:marginLeft "10px"}})
        (sui-button {:onClick #(on-edit-confirm this edit-description id) :color "green"
                     :floated "right" :icon "check circle outline" :style {:marginLeft "10px"}}))
      (fragment
        (sui-button {:onClick #(on-completed-change this edit-completed id) :color (if task-completed? "teal" "yellow")
                     :circular true :icon (if task-completed? "check circle outline" "circle outline")})
        (span {:style {:textDecoration (if task-completed? "line-through" "none")
                       :paddingLeft "10px" :width "80%" :display "inline-block"}} description)
        (sui-button {:onClick #(on-delete this delete-task id list-id) :color "red"
                     :floated "right" :icon "trash alternate outline" :style {:marginLeft "10px"}})
        (sui-button {:onClick #(on-edit-toggle this) :color "blue"
                     :floated "right" :icon "edit outline" :style {:marginLeft "10px"}}))))))
(def ui-task (comp/factory Task {:keyfn :task/id}))

(defsc TaskList [this {:task-list/keys [tasks]}]
  {:query   [:task-list/id
             :task-list/item-count
             {:task-list/tasks (comp/get-query Task)}]
   :ident   :task-list/id
   :initial-state {:task-list/id    :param/id
                   :task-list/item-count 3
                   :task-list/tasks [{:id 1 :description "Task 1" :completed false :list-id 1}
                                     {:id 2 :description "Task 2" :completed true :list-id 1}
                                     {:id 3 :description "Task 3" :completed false :list-id 1}
                                     ]}}
  (js/console.log "TaskList render" ::task-list)
  (when (not-empty tasks)
    (map ui-task tasks)))
(def ui-task-list (comp/factory TaskList))

(defmutation add-task [{:keys [list-id description]}]
  (action
    [{:keys [state]}]
    (let [new-id (inc (get-in @state [:task-list/id list-id :task-list/item-count]))]
      (swap! state merge/merge-component Task {:task/id           new-id
                                               :task/description  description
                                               :task/completed    false
                                               :task/list-id      list-id}
             :append [:task-list/id list-id :task-list/tasks])
      (swap! state update-in [:task-list/id list-id :task-list/item-count] inc))))

(defn on-add-task [this task-list description]
  (if (not-empty description)
    (do
      (js/console.log "Adding task" description)
      (comp/transact! this [(add-task {:list-id (:task-list/id task-list)
                                       :description description})])
      (comp/set-state! this (assoc (comp/get-state this) :edit-value "")))))

(defsc TaskInput [this {:task-input/keys [id description task-list]}]
  {:query [:task-input/id :task-input/description {:task-input/task-list (comp/get-query TaskList)}]
   :ident :task-input/id
   :initial-state {:task-input/id           :param/id
                   :task-input/description  ""
                   :task-input/task-list    {:id :param/list-id}}
   :initLocalState (fn [this {:task-input/keys [description task-list]}]
                     {:edit-value description})}
  (js/console.log "TaskInput render" ::task-input)
  (let [{:keys [edit-value]} (comp/get-state this)]
    (sui-segment {:key (str "input-" id) :vertical true :style {:width "90%" :margin "auto"}}
                 (sui-input {:action {:icon "plus"
                                      :color "violet"
                                      :content "Add Task"
                                      :onClick #(on-add-task this task-list edit-value)}
                             :placeholder "Add Task"
                             :onChange #(on-edit-update this %)
                             :value edit-value
                             :style {:width "100%"}})
                 )))
(def ui-task-input (comp/factory TaskInput))

(defsc Root [this {:root/keys [task-input task-list]}]
  {:query [{:root/task-input (comp/get-query TaskInput)}
           {:root/task-list (comp/get-query TaskList)}]
   :initial-state {:root/task-input {:id 1 :list-id 1}
                   :root/task-list {:id 1}}}
  ; Root component does not need an ident, it is the root of the tree in database
  (js/console.log "Root render" ::root)
  (sui-container {:type "fluid" :style {:paddingTop "5em"}}
                 (sui-segment {:key "task-list-1"
                                :raised true :padded "very" :style {:width "80%" :margin "auto"}}
                              (ui-task-input task-input)
                              (ui-task-list task-list))))

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

  ; Remove all data from the app (The client-side database)
  (reset! (::app/state-atom app) {})

  ; Check the current state of the app (The client-side database)
  (app/current-state app)

  ; Check the Task description value
  (comp/get-state app :root/task :task/description)

  ; Render the app to the DOM
  (app/schedule-render! app)

  (comp/transact! app [(make-older {:person/id 1})])        ; transact! is a function that takes the app and a vector of mutations to apply to the app local and remote state

  ; Get Task description value
  (comp/get-state app :root/task :task/description)

  (swap! (::app/state-atom app) assoc-in [:person/id 1 :person/age] 26) ; This will create the person map if it does not exist
  (swap! (::app/state-atom app) update-in [:person/id 1 :person/age] inc) ; This will update the person map if it exists, note that the update function (inc) is called with the current value

  (merge/merge-component! app Person {:person/id 1
                                      :person/name "John Doe"
                                      :person/age 25
                                      :person/cars [{:car/id 1 :car/model "Ford Mustang"}
                                                    {:car/id 2 :car/model "Chevy Camaro"}]}
                          :replace [:root/person])

  (merge/merge-component! app Car {:car/id 3
                                   :car/model "Dodge Charger"}
                          :append [:person/id 1 :person/cars])


  (merge/merge-component! app Person {:person/id 1
                                      :person/name "John Doe"
                                      :person/age 23})
  (merge/merge-component! app Person {:person/id 2
                                      :person/name "Mary Ellen"
                                      :person/age 23})
  (merge/merge-component! app Person {:person/id 3
                                      :person/name "Jane Lee"
                                      :person/age 27})


; Pathom test

(require '[com.wsscode.pathom.core :as pathom])
(def pathom-parser (pathom/parser {}))

(pathom-parser {} {} [::latest-task])

; DB testing

  (require '[datomic.api :as d])
  (def db-uri "datomic:dev://localhost:4334/hello")

  (d/create-database db-uri)

  (def conn (d/connect db-uri))

; Any transactions submitted to the connection will be persisted to the storage that you chose when creating your database.
  @(d/transact conn [{:db/doc "Hello world"}])

(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}])

@(d/transact conn movie-schema)

(def first-movies [{:movie/title "The Goonies"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Commando"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Repo Man"
                    :movie/genre "punk dystopia"
                    :movie/release-year 1984}])

@(d/transact conn first-movies)

; Query the database

(def db (d/db conn))

(def all-movies-q '[:find ?e
                    :where [?e :movie/title]])

(d/q all-movies-q db)


(def all-titles-q '[:find ?movie-title
                    :where [_ :movie/title ?movie-title]])

(def titles-from-1985 '[:find ?title
                        :where [?e :movie/title ?title]
                        [?e :movie/release-year 1985]])

(def all-data-from-1985 '[:find ?title ?year ?genre
                          :where [?e :movie/title ?title]
                          [?e :movie/release-year ?year]
                          [?e :movie/genre ?genre]
                          [?e :movie/release-year 1985]])

(d/q all-data-from-1985 db)


; New query

(require '[datomic.api :as d])
(def db-uri "datomic:dev://localhost:4334/hello")

(d/create-database db-uri)

(def conn (d/connect db-uri))

(def db (d/db conn))


(def all-data-from-1985 '[:find ?title ?year ?genre
                          :where [?e :movie/title ?title]
                          [?e :movie/release-year ?year]
                          [?e :movie/genre ?genre]
                          [?e :movie/release-year 1985]])

(d/q all-data-from-1985 db)





; Historical queries


(d/q '[:find ?e
       :where [?e :movie/title "Commando"]]
     db)


(def commando-id
  (ffirst (d/q '[:find ?e
                 :where [?e :movie/title "Commando"]]
               db)))


)
