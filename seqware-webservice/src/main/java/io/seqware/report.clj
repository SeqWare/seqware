(ns io.seqware.report
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as db]))

(defn print-row [row]
  (doseq [x (interpose \tab row)]
    (if (nil? x)
      (print "null")
      (print x)))
  (newline))

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
              #_"Workflow Run Status"
              "Workflow Run SWID" 
              "Processing Algorithm" 
              "Processing SWID" 
              "Processing Attributes" 
              "File Meta-Type" 
              "File SWID" 
              "File Path"])

(defn print-tsv [result-set]
  (let [cols (-> result-set .getMetaData .getColumnCount)
        idxs (range 1 (inc cols))]
    (print-row (take cols headers))
    (loop []
      (when (.next result-set)
        (->> idxs
          (map #(.getString result-set %))
          (print-row))
        (recur)))))

(def ^:dynamic *study-report-sql-resource* "study-report.sql")

(def ^:dynamic *db-spec*
  "postgres://seqware:seqware@10.0.11.20:5432/seqware_meta_db"
  #_{:name "jdbc/SeqWareMetaDB"})



(defn study-report [study-id]
  (let [sql (slurp (io/resource *study-report-sql-resource*))
        sql (str sql " where st.study_id = ?")]
    (with-open [conn (db/get-connection *db-spec*)
                ps (.prepareStatement conn sql)]
      (.setObject ps 1 study-id)
      (with-open [rs (.executeQuery ps)]
        (print-tsv rs)))))

(defn write-study-report! [study-id out-file]
  (with-open [out (io/writer out-file)]
    (binding [*out* out]
      (study-report study-id))))

