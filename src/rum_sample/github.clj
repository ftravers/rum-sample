(ns rum-sample.github
  (:require
   [clojure.set :as c-set]
   [clojure.core.async :refer [<!! timeout]]
   [clojure.string :as c-str]
   [clojure.data.json :as json]
   [clj-http.client :as client]
   [clojure.test :as tst :refer [is]]))

(defn rate-limited
  [f-seq wait-ms]
  (map
   (fn [api]
     (<!! (timeout wait-ms))
     (api))))

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
  (map
   (fn [c]
     (let [[commit-count username]
           ((juxt :total (comp :login :author)) c)]
       {:username username
        :number_of_commits commit-count}))
   com))

(defn get-repos!
  [repos-per-page]
  (let [page 1]
    (->> (get-json (str git-api
                        "/search/repositories?q=language=Clojure&sort=stars&"
                        "per_page=" repos-per-page "&"
                        "page=" page)))))

(defn select-keys* [m paths]
  (into {} (map (fn [p] [(last p) (get-in m p)]) paths)))

(defn mod-repo-data [m]
  (-> m
      (update-in [:full_name] #(last (c-str/split % #"/")))
      (c-set/rename-keys {:full_name :repo
                          :login :owner
                          :stargazers_count :stars})
      (assoc :repository_name (:full_name m))))

(defn extract-repo-data [m]
  (select-keys* m [[:stargazers_count] [:full_name] [:owner :login]]))

(defn get-commits-2!
  {:test
   (fn []
     (is
      (=
       {:repo "clojure-style-guide",
        :owner "bbatsov",
        :stars 3150,
        :commits
        ({:username "bbatsov", :number_of_commits 22}
         {:username "uvtc", :number_of_commits 13})}
       (get-commits-2!
        {:repo "clojure-style-guide"
         :owner "bbatsov"
         :stars 3150}
        2)
       )))}
  [repo-m commit-count]
  (let [repo-n-owner (-> repo-m ((juxt :repo :owner)))
        commits (->>
                 repo-n-owner
                 (apply get-commits!)
                 (take commit-count))
        clean-commits (commits-extract-keys commits)]
    (assoc repo-m :commits clean-commits)))

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

(defn add-email
  {:test
   (fn []
     (is
      (=
       {:repo "clojure-style-guide",
        :owner "bbatsov",
        :stars 3150,
        :commits
        '({:number_of_commits 22, :email "bozhidar@batsov.com"}
          {:number_of_commits 13, :email "jgabriele@fastmail.fm"})}
      
       (add-email
        {:repo "clojure-style-guide",
         :owner "bbatsov",
         :stars 3150,
         :commits
         '({:username "bbatsov", :number_of_commits 22}
           {:username "uvtc", :number_of_commits 13})}))))}

  [{:keys [repo stars commits] :as rps}]
  (let [cmt-w-email
        (map (fn [commit]
               (merge
                (dissoc commit :username)
                (get-user (:username commit))))
             commits)]
    (assoc rps :commits cmt-w-email)))

(defn unbounce-problem
  {:test
   (fn []
     (is (=
          (unbounce-problem 2 2))
         ))}
  [repo-count commits-per-repo-count]
  (->>
   (get-repos! repo-count)
   :items
   (map #(-> %
             extract-repo-data
             mod-repo-data
             (get-commits-2! commits-per-repo-count)
             (dissoc :repo)
             (dissoc :owner)))))



