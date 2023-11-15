(ns user
  (:require
    [app.server :as server]
    [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs refresh]]))

(set-refresh-dirs "src/dev" "src/main")

(defn stop []
  (server/stop))

(defn start []
  (server/start))

(defn restart
  "Stop the server, reload all source code, then restart the server.
  See documentation of tools.namespace.repl for more information."
  []
  (stop)
  (refresh :after 'user/start))

;; These are here so we can run them from the editor with kb shortcuts.  See IntelliJ's "Send Top Form To REPL" in
;; keymap settings.
(comment
  (require 'user)

  (start)
  (restart)
  (stop)
  )