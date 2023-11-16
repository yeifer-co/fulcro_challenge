(ns app.ui
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc fragment]]
    [com.fulcrologic.fulcro.dom :refer [span]]
    [com.fulcrologic.fulcro.dom.events :as events]
    ["semantic-ui-react" :refer [Container Segment Button Input]]
    [app.mutations :as api]
    ))

(def sui-container (interop/react-factory Container))
(def sui-segment (interop/react-factory Segment))
(def sui-button (interop/react-factory Button))
(def sui-input (interop/react-factory Input))

(defn on-edit-toggle [this]
  (comp/set-state! this (update (comp/get-state this) :editing? not)))

; Handle event
(defn on-edit-update [this evt]
  (comp/set-state! this (assoc (comp/get-state this) :edit-value (events/target-value evt))))

(defn on-edit-confirm [this id]
  (if (not-empty (comp/get-state this :edit-value))
    (do
      (comp/transact! this [(api/edit-description {:id id :new-value (comp/get-state this :edit-value)})])
      (comp/set-state! this (assoc (comp/get-state this) :editing? false)))))

(defn on-edit-cancel [this]
  (comp/set-state! this (assoc (comp/get-state this) :editing? false)))

(defn on-delete [this id list-id]
  (comp/transact! this [(api/delete-task {:id id :list-id list-id})]))

(defn on-completed-change [this id]
  (comp/set-state! this (update (comp/get-state this) :task-completed? not))
  (comp/transact! this [(api/edit-completed {:id id :new-value (not (comp/get-state this :task-completed?))})]))

(defsc Task [this {:task/keys [id description completed list-id] :as props}]
       {:query [:task/id :task/description :task/completed :task/list-id]
        :ident (fn [] [:task/id (:task/id props)])
        ;:initial-state {:task/id           :param/id
        ;                :task/description  :param/description
        ;                :task/completed    :param/completed
        ;                :task/list-id      :param/list-id}
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
                          (sui-button {:onClick #(on-edit-confirm this id) :color "green"
                                       :floated "right" :icon "check circle outline" :style {:marginLeft "10px"}}))
                        (fragment
                          (sui-button {:onClick #(on-completed-change this id) :color (if task-completed? "teal" "yellow")
                                       :circular true :icon (if task-completed? "check circle outline" "circle outline")})
                          (span {:style {:textDecoration (if task-completed? "line-through" "none")
                                         :paddingLeft "10px" :width "80%" :display "inline-block"}} description)
                          (sui-button {:onClick #(on-delete this id list-id) :color "red"
                                       :floated "right" :icon "trash alternate outline" :style {:marginLeft "10px"}})
                          (sui-button {:onClick #(on-edit-toggle this) :color "blue"
                                       :floated "right" :icon "edit outline" :style {:marginLeft "10px"}}))))))
(def ui-task (comp/factory Task {:keyfn :task/id}))

(defsc TaskList [this {:task-list/keys [id item-count tasks] :as props}]
       {:query   [:task-list/id
                  :task-list/item-count
                  {:task-list/tasks (comp/get-query Task)}]
        :ident    (fn [] [:task-list/id (:task-list/id props)])
        :initial-state {:task-list/id 1}
        }
       (js/console.log "TaskList render" ::task-list)
       (when (not-empty tasks)
         (map ui-task tasks)))
(def ui-task-list (comp/factory TaskList))

(defn on-add-task [this task-list description]
  (if (not-empty description)
    (do
      (js/console.log "Adding task" description)
      (comp/transact! this [(api/add-task {:list-id (:task-list/id task-list)
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
                        :root/task-list {}
                        }}
       ; Root component does not need an ident, it is the root of the tree in database
       (js/console.log "Root render" ::root)
       (sui-container {:type "fluid" :style {:paddingTop "5em"}}
                      (sui-segment {:key "task-list-1"
                                    :raised true :padded "very" :style {:width "80%" :margin "auto"}}
                                   (ui-task-input task-input)
                                   (ui-task-list task-list))))