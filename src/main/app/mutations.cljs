(ns app.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    ))

(defmutation delete-task [{:keys [id list-id]}]
  (action [{:keys [state]}]                                 ; What to do locally
          (swap! state update :task/id dissoc id)
          (swap! state merge/remove-ident* [:task/id id] [:task-list/id list-id :task-list/tasks]))
  (remote [env] true))

(defmutation edit-completed [{:keys [id new-value]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:task/id id :task/completed] new-value))
  (remote [env] true))

(defmutation edit-description [{:keys [id new-value]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:task/id id :task/description] new-value))
  (remote [env] true))

(defmutation add-task [{:keys [list-id description]}]
  (action
    [{:keys [state]}]
    (let [new-id (inc (get-in @state [:task-list/id list-id :task-list/item-count]))]
      #_(swap! state update-in [:task-list/id list-id :task-list/tasks] conj {:task/id           new-id
                                                                            :task/description  description
                                                                            :task/completed    false
                                                                            :task/list-id      list-id})
      ; Use merge/merge-ident to add the new task to the task-list/tasks vector
      (swap! state merge/merge-ident [:task/id new-id] {:task/id           new-id
                                                        :task/description  description
                                                        :task/completed    false
                                                        :task/list-id      list-id})
      (swap! state update-in [:task-list/id list-id :task-list/item-count] inc)))
  (remote [env] true))