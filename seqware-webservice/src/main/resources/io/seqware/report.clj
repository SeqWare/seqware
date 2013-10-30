(ns io.seqware.report
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as db]
            [clojure.string :as s]))

(defn print-row [row]
  (->> row
    (map str)
    (map #(s/replace % \tab \space))
    (interpose \tab)
    (map print)
    (dorun))
  (newline))

(defn print-results [result-set]
  (let [cols (-> result-set .getMetaData .getColumnCount)
        idxs (range 1 (inc cols))]
    (loop []
      (when (.next result-set)
        (->> idxs
          (map #(.getObject result-set %))
          (print-row))
        (recur)))))

(def headers ["Last Modified" 
              "Study Title" 
              "Study SWID" 
              "Study Attributes" 
              "Experiment Name" 
              "Experiment SWID" 
              "Experiment Attributes" 
              "Parent Sample Name" 
              "Parent Sample SWID" 
              "Parent Sample Attributes" 
              "Sample Name" 
              "Sample SWID" 
              "Sample Attributes" 
              "Sequencer Run Name" 
              "Sequencer Run SWID" 
              "Sequencer Run Attributes" 
              "Lane Name" 
              "Lane Number" 
              "Lane SWID" 
              "Lane Attributes" 
              "IUS Tag" 
              "IUS SWID" 
              "IUS Attributes" 
              "Workflow Name" 
              "Workflow Version" 
              "Workflow SWID" 
              "Workflow Run Name"
              "Workflow Run Status"
              "Workflow Run SWID" 
              "Processing Algorithm" 
              "Processing SWID" 
              "Processing Attributes" 
              "File Meta-Type" 
              "File SWID" 
              "File Attributes"
              "File Path"
              "File Description"
              "File Md5sum"
              "File Size"
              "Skip"])

(def ^:dynamic *db-spec*
  #_"postgres://seqware:seqware@10.0.11.20:5432/seqware_meta_db_2013_06_10"
  {:name "java:comp/env/jdbc/SeqWareMetaDB"})

(defn in [col values]
  [(str col " in (" (apply str (interpose ", " (repeat (count values) "?"))) ")")
   values])

(defn like [col values]
  (let [wc-values (->> values
                    (map (fn [v]
                           [(str v ":%") (str "%:" v ":%") (str "%:" v)]))
                    (apply concat))
        eq (repeat (count values) (str col " = ?"))
        wc (repeat (count wc-values) (str col " like ?"))
        frag (apply str (interpose " or " (concat eq wc)))
        vals (concat values wc-values)]
    [(str "(" frag ")") vals]))

(defn ->ints [coll]
  (->> coll
    (map #(try (Integer/parseInt %)
            (catch NumberFormatException e nil)))
    (keep identity)))

(defn ->bools [coll]
  (map #(Boolean/parseBoolean %) coll))

(defn clause [[key values]]
  (case key
    "study"           (in "study_swa" (->ints values))
    "experiment"      (in "experiment_swa" (->ints values))
    "sample"          (in "sample_swa" (->ints values))
    "sample-ancestor" (like "sample_parent_swas" values)
    "sequencer-run"   (in "sequencer_run_swa" (->ints values))
    "lane"            (in "lane_swa" (->ints values))
    "ius"             (in "ius_swa" (->ints values))
    "workflow"        (in "workflow_swa" (->ints values))
    "workflow-run"    (in "workflow_run_swa" (->ints values))
    "file"            (in "file_swa" (->ints values))
    "file-meta-type"  (in "file_meta_type" values)
    "skip"            (in "skip" (->bools values))
    nil))

(defn apply-values [ps values]
  (loop [i 1
         values values]
    (when (seq values)
      (.setObject ps i (first values))
      (recur (inc i) (rest values)))))

(defn file-provenance-report [m]
  (let [clauses (->> m (map clause) (keep identity))
        sql (if (empty? clauses)
              "select * from file_provenance_report"
              (apply str "select * from file_provenance_report where " (interpose " and " (map first clauses))))
        vals (apply concat (map second clauses))]
    (with-open [conn (db/get-connection *db-spec*)
                ps (.prepareStatement conn sql)]
      (apply-values ps vals)
      (with-open [rs (.executeQuery ps)]
        (print-row headers)
        (print-results rs)))))

(defn write-file-provenance-report! [m out]
  (binding [*out* out]
    (file-provenance-report m)))
