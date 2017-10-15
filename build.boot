(set-env!
 :source-paths #{"src/cljc" "src/clj" "src/js" "thirdparty/js"}
 :resource-paths #{"html"}

 :dependencies '[
                 [org.clojure/clojure "1.8.0"]         ;; add CLJ
                 [org.clojure/clojurescript "0.0-SNAPSHOT"]
                 [adzerk/boot-cljs "2.1.4"]
                 [pandeiro/boot-http "0.8.3"]
                 [adzerk/boot-reload "0.5.2"]
                 [adzerk/boot-cljs-repl "0.3.3"]       ;; add bREPL
                 [com.cemerick/piggieback "0.2.1"]     ;; needed by bREPL
                 [weasel "0.7.0"]                      ;; needed by bREPL
                 [org.clojure/tools.nrepl "0.2.12"]    ;; needed by bRELP
                 [adzerk/boot-test "1.2.0"]
                 [crisptrutski/boot-cljs-test "0.3.4"]
                 [cljsjs/redux "3.6.0-0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.google.template/soy "2017-04-23"]
                 [cljsjs/incremental-dom "0.5.2-1"]
                 ])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-test :refer [test]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]]
         '[boot.core :as boot]
         '[clojure.java.io :as io])

(import com.google.template.soy.SoyFileSet)
(import com.google.template.soy.incrementaldomsrc.SoyIncrementalDomSrcOptions)

(defn- compile-soy!
  [in-file out-file]
  (let [sfs (-> (SoyFileSet/builder)
                (.add in-file)
                (.build))
        results (.compileToIncrementalDomSrc
                 sfs
                 (new SoyIncrementalDomSrcOptions))
        text (first results)]
    (doto out-file
      io/make-parents
      (spit text))))

(defn- soy->js
  [path]
  (.replaceAll path "\\.soy$" ".js"))

(deftask soy
  "Compile .soy files"
  []
  (let [tmp (boot/tmp-dir!)]
    (fn middleware [next-handler]
      (fn handler [fileset]
        (boot/empty-dir! tmp)
        (let [in-files (boot/input-files fileset)
              soy-files (boot/by-ext [".soy"] in-files)]
          (doseq [in soy-files]
            (let [in-file (boot/tmp-file in)
                  in-path (boot/tmp-path in)
                  out-path (soy->js in-path)
                  out-file (io/file tmp out-path)]
              (compile-soy! in-file out-file)))
          (-> fileset
              (boot/add-source tmp)
              (boot/commit!)
              next-handler))))))

(deftask testing
  "Add test/cljc for CLJ/CLJS testing purpose"
  []
  (set-env! :source-paths #(conj % "test/cljc"))
  identity)

;;; add dev task
(deftask dev
  "Launch immediate feedback dev environment"
  []
  (comp
   (testing)
   (serve :dir "target"
          ;:handler 'voting.server/app
          :httpkit true
          :resource-root "target"
          :reload true)
   (watch)
   (soy)
   (reload)
   (cljs-repl) ;; before cljs
   (cljs)
   (target :dir #{"target"})))

(deftask tdd
  "Launch a TDD Environment"
  []
  (comp
   (testing)
   (watch)
   (soy)
   (test-cljs :update-fs? true
              :js-env :phantom
              :namespaces '#{voting.core-spec
                             voting.reducer-spec
                             voting.store-spec})
   (test :namespaces '#{voting.core-spec
                        voting.reducer-spec
                        voting.store-spec})
   (target :dir #{"target"})))
