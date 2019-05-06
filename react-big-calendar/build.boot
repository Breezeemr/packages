(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.10.3" :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
  '[boot.core :as boot]
  '[boot.tmpdir :as tmpd]
  '[clojure.java.io :as io]
  '[clojure.string :as str]
  '[boot.util :refer [sh]])

(def +lib-version+ "0.20.3")
(def +version+ (str +lib-version+ "-t2"))
#_(def +lib-folder+ (format "react-big-calendar-%s" +lib-version+))

(def +lib-folder+ "react-big-calendar-master")

(task-options!
  pom {:project     'cljsjs/react-big-calendar
       :version     +version+
       :description "An events calendar component built for React and made for modern browsers (read: IE10+) and uses flexbox over the classic tables-ception approach."
       :url         "http://intljusticemission.github.io/react-big-calendar/examples/"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"BSD" "http://opensource.org/licenses/BSD-3-Clause"}})

#_(def url (format "https://github.com/intljusticemission/react-big-calendar/archive/v%s.zip" +lib-version+))

(def url "https://github.com/Breezeemr/react-big-calendar/archive/merge-branch.zip")

(deftask download-react-big-calendar []
  (download :url url
    #_:checksum #_""
    :unzip true))

(deftask package []
  (comp
    (download-react-big-calendar)
    (sift :move {#"^react-big-calendar-[^/]*/" ""})
    (run-commands
           :commands [;["npm" "install"]
                      ;["npm" "install" "webpack"]
                      ;["npm" "run" "build"]
                      ["yarn" "install"]
                      ["yarn" "install" "webpack"]
                      ["yarn" "build"]
                      ;["./node_modules/.bin/webpack" "--config" "webpack.config.cljsjs.js"]
                      ])
    (sift :move {#"dist/react-big-calendar.js"
                 "cljsjs/react-big-calendar/development/react-big-calendar.inc.js"
                 #".*/less/(.+\.less)"
                 "cljsjs/react-big-calendar/development/less/$1"
                 #"dist/react-big-calendar.min.js"
                 "cljsjs/react-big-calendar/production/react-big-calendar.min.inc.js"
                 })
    (sift :include #{#"^cljsjs" #"^deps.cljs"})
    (pom)
    (jar)))

;react-big-calendar/lib/css/react-big-calendar.css
