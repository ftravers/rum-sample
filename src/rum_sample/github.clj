(ns rum-sample.github
  (:require
   [clojure.set :as c-set]
   [clojure.string :as c-str]
   [clojure.data.json :as json]
   [clj-http.client :as client]
   [clojure.test :as tst :refer [is]]))

;; (tst/run-tests 'rum-sample.github)
(defn get-json
  ([url header]
   (json/read-str (:body (client/get url header)) :key-fn keyword))
  ([url]
   (json/read-str (:body (client/get url)) :key-fn keyword)))

(defn get-json-basic-auth
  ([url user pass]
   (json/read-str (:body (client/get url {:basic-auth [user pass]})) :key-fn keyword)))

(def git-api "https://api.github.com/")

(defn get-commits!
  [repo owner]
  (->> (get-json (str git-api "/repos/" owner "/" repo "/stats/contributors"))
       (sort-by :total)
       reverse))

(defn commits-extract-keys [com]
  (map (juxt :total (comp :login :author)) com))

(defn get-repos!
  [repos-per-page]
  (let [page 1]
    (->> (get-json (str git-api
                        "/search/repositories?q=language=Clojure&sort=stars&"
                        "per_page=" repos-per-page "&"
                        "page=" page)))))

(defn select-keys* [m paths]
  (into {} (map (fn [p] [(last p) (get-in m p)]) paths)))

(defn mod-data [m]
  (-> m
      (update-in [:full_name] #(last (c-str/split % #"/")))
      (c-set/rename-keys {:full_name :repo :login :owner :stargazers_count :stars})))

(defn extract-data [m]
  (select-keys* m [[:stargazers_count] [:full_name] [:owner :login]]))

(defn get-commits-2!
  {:test
   (fn []
     (is
      (=
       '([23 "bbatsov"] [13 "uvtc"])

       (get-commits-2!
        {:repo "clojure-style-guide"
         :owner "bbatsov"
         :stars 3150}
        2)

       )))}
  [repo-m count]
  (let [name-owner (-> repo-m ((juxt :repo :owner)))
        commits (->>
                 name-owner
                 (apply get-commits!)
                 (take count))]
    (commits-extract-keys commits)
    ))

(defn get-user [username]
  {:test (fn []
           (is (=
                (get-user "ftravers")
                "fenton.travers@gmail.com"))
           (is (= (get-user "clojure") nil)))}
  (let [email (->> (get-json-basic-auth
                    (str git-api "/users/" username)
                    "ftravers"
                    "WVKr\"(:7J~Qj")
                   :email)]
    (if (not email)
      {:username username}
      {:email email})))

(defn repo-com-usr
  {:test
   (fn []
     (is
      (=
       (repo-com-user )
       )))}
  [{:keys [repo owner stars] :as rp}
   commit-count]
  (let [commits
        (get-commits-2! rp commit-count)]
    (map (fn [[cmt-cnt cmt-username]]
           (merge {:number_of_commits cmt-cnt}
                  (get-user cmt-username)))
         commits)
    ;; {:repository_name repo
    ;;  :stargazers_count stars
    ;;  :authors}
    ))

(comment
  (def rps (get-repos! 2))
  (def itms (:items rps))
  (def clean-repos
    (map #(-> %
              extract-data
              mod-data
              (get-commits-2! 2))
         itms)

    )
  
  ;; ({:repo "clojure", :owner "clojure", :stars 7669}
  ;;  {:repo "clojure-style-guide", :owner "bbatsov", :stars 3150})


  )









;; ```json
;; [
;;   {
;;     "repository_name": "clojure/clojure",
;;     "stargazers_count": 50,
;;     "authors": [
;;       {"email": "...", "number_of_commits": 1000},
;;       {"email": "...", "number_of_commits": 999},
;;       // ...
;;     ]
;;   },
;;   {
;;     "repository_name": "stuartsierra/component",
;;     "stargazers_count": 49,
;;     "authors": [
;;       {"email": "...", "number_of_commits": 531},
;;       {"email": "...", "number_of_commits": 921},
;;       {"email": "...", "number_of_commits": 300},
;;       // ...
;;     ]
;;   }
;;   // , ...
;; ]
;; ```






















