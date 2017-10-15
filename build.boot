(set-env!
 :source-paths #{"src/cljc" "src/clj"}
 :resource-paths #{"html"}

 :dependencies '[
                 [org.clojure/clojure "1.8.0"]         ;; add CLJ
                 [org.clojure/clojurescript "1.9.473"] ;; add CLJS
                 [adzerk/boot-cljs "1.7.228-2"]
                 [pandeiro/boot-http "0.8.3"]
                 [adzerk/boot-reload "0.5.1"]
                 [adzerk/boot-cljs-repl "0.3.0"]       ;; add bREPL
                 [com.cemerick/piggieback "0.2.1"]     ;; needed by bREPL
                 [weasel "0.7.0"]                      ;; needed by bREPL
                 [org.clojure/tools.nrepl "0.2.12"]    ;; needed by bREPL
                 [adzerk/boot-test "1.2.0"]
                 [crisptrutski/boot-cljs-test "0.3.4"]
                 [cljsjs/redux "3.6.0-0"]
                 [http-kit "2.2.0"]
                 [org.clojure/data.json "0.2.6"]
                 ])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-test :refer [test]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

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
   (serve ;:dir "target"
          :handler 'voting.server/app
          :httpkit true
          :resource-root "target"
          :reload true)
   (watch)
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
   (test-cljs :update-fs? true
              :js-env :phantom
              :namespaces '#{voting.core-spec
                             voting.reducer-spec
                             voting.store-spec})
   (test :namespaces '#{voting.core-spec
                        voting.reducer-spec
                        voting.store-spec})
   (target :dir #{"target"})))
