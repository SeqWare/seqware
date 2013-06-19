(ns io.seqware.report
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as db]
            [clojure.string :as str]))

(defn print-row [row]
  (->> row
    (map #(or % ""))
    (map #(str/replace % \tab \space))
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
          (map #(.getString result-set %))
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
              "File Path"])

(def ^:dynamic *study-report-sql-resource*
  #_"test.sql"
  "study-report.sql")

(def ^:dynamic *db-spec*
  #_"postgres://seqware:seqware@10.0.11.20:5432/seqware_meta_db_2013_06_10"
  {:name "java:comp/env/jdbc/SeqWareMetaDB"})

(defn study-report [study-id header?]
  (let [sql (slurp (io/resource *study-report-sql-resource*))]
    (with-open [conn (db/get-connection *db-spec*)
                ps (.prepareStatement conn sql)]
      (.setObject ps 1 study-id)
      (with-open [rs (.executeQuery ps)]
        (when header?
          (print-row headers))
        (print-results rs)))))

(defn write-study-report! [study-id out]
  (binding [*out* out]
    (study-report study-id true)))

