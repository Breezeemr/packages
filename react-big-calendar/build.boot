(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.5.2" :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
  '[boot.core :as boot]
  '[boot.tmpdir :as tmpd]
  '[clojure.java.io :as io]
  '[clojure.string :as str]
  '[boot.util :refer [sh]])

(def +lib-version+ "0.13.0")
(def +version+ (str +lib-version+ "-e1"))
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

(def url "https://github.com/eistre91/react-big-calendar/archive/master.zip")

(deftask download-react-big-calendar []
  (download :url url
    #_:checksum #_""
    :unzip true))

(def main-file-name "main.js")
(def webpack-file-name "webpack.config.js")

(defn get-file [fileset file-name]
  (io/file
    (:dir (first (filter #(= (:path %) file-name) (boot/user-files fileset))))
    file-name))

(deftask build-react-big-calendar []
  (let [tmp (boot/tmp-dir!)]
    (with-pre-wrap fileset
      (doseq [f (->> fileset boot/input-files)
              :let [target (io/file tmp (tmpd/path f))]]
        (io/make-parents target)
        (io/copy (tmpd/file f) target))
      (io/copy (get-file fileset main-file-name)
        (io/file tmp +lib-folder+ main-file-name))
      (io/copy (get-file fileset webpack-file-name)
        (io/file tmp +lib-folder+ webpack-file-name))
      (binding [boot.util/*sh-dir* (str (io/file tmp +lib-folder+))]
        (do ((sh "npm" "install"))
            ((sh "npm" "install" "webpack"))
            ((sh "npm" "install" "babel-cli"))
            ((sh "node" "--stack-size=1500" "./node_modules/.bin/babel"
               "./src" "--ignore" "*.spec.js" "--out-dir" "./build"))
            ((sh "./node_modules/.bin/webpack"))
            ((sh "./node_modules/.bin/webpack" "--production"))
             ((sh "rm" "-rf" "./node_modules"))))
      (-> fileset (boot/add-resource tmp) boot/commit!))))

#_(deftask patch-react-big-calendar []
  (with-pre-wrap fileset
    (let [tmp (boot/tmp-dir!)
          dayColumn-path (str +lib-folder+ "/src/DayColumn.js")
          dayColumn-file (io/file tmp dayColumn-path)
          _         (io/make-parents dayColumn-file)
          old-entry (boot/tmp-get fileset dayColumn-path)
          patched-contents
          (-> old-entry boot/tmp-file slurp
            (str/replace "      let { height, top, width, xOffset } = style"
              "let { height = style.height, top = style.top, width = style.width, xOffset = style.xOffset} = xStyle"))]
      (spit dayColumn-file patched-contents)
      (-> fileset
        (boot/rm [old-entry])
        (boot/add-resource tmp)
        boot/commit!))))

(deftask package []
  (comp
    (download-react-big-calendar)
    (build-react-big-calendar)
    #_(patch-react-big-calendar)
    (sift :move {#".*react-big-calendar.inc.js"
                 "cljsjs/react-big-calendar/development/react-big-calendar.inc.js"
                 #".*/less/(.+\.less)"
                 "cljsjs/react-big-calendar/development/less/$1"
                 #".*react-big-calendar.min.inc.js"
                 "cljsjs/react-big-calendar/production/react-big-calendar.min.inc.js"
                 })
    (sift :include #{#"^cljsjs" #"^deps.cljs"})
    (pom)
    (jar)))

;react-big-calendar/lib/css/react-big-calendar.css
