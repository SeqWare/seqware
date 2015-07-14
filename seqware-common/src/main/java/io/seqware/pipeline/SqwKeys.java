/*
 * Copyright (C) 2014 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.seqware.pipeline;

import io.seqware.Engines;
import java.util.Arrays;

/**
 * This binds together all of the keys in the .seqware/settings file as a precursor to auto-generating documentation for them.
 *
 * TODO: Remove keys with periods in them
 *
 * @author dyuen
 */
public enum SqwKeys {
    // @formatter:off
    SW_METADATA_METHOD(null, Categories.COMMON, true, "SeqWare MetaDB communication method, can be 'database' or 'webservice' or 'inmemory' or 'none'", "webservice", "database", "webservice", "inmemory", "none"),
    AWS_ACCESS_KEY(null, Categories.COMMON, false, "Amazon cloud settings. Only used if reading and writing to S3 buckets.", "FILLMEIN"),
    AWS_SECRET_KEY(null, Categories.COMMON, false, "Amazon cloud settings. Only used if reading and writing to S3 buckets.", "FILLMEIN"),

    SW_REST_URL(null, Categories.COMMON_WS, true, "Specify the URL for the seqware-webservice", "http://localhost:8080/SeqWareWebService"),
    SW_REST_USER(null, Categories.COMMON_WS, true, "Specify the username for the seqware-webservice", "admin"),
    SW_REST_PASS(null, Categories.COMMON_WS, true, "Specify the password for the seqware-webservice", "admin@admin.com"),
    SW_DB_USER(null, Categories.COMMON_DB, false, "JDBC user for the seqware metadb", "seqware"),
    SW_DB_PASS(null, Categories.COMMON_DB, false, "JDBC password for the seqware metadb", "seqware"),
    SW_DB_SERVER(null, Categories.COMMON_DB, false, "Host for the metadb", "localhost"),
    SW_DB(null, Categories.COMMON_DB, false, "database name", "seqware_meta_db"),
    SW_DEFAULT_WORKFLOW_ENGINE(null, Categories.SCHEDULE_LAUNCH, true,
            "the default engine to use if otherwise unspecified (one of: "+Engines.ENGINES_LIST+")", "oozie-sge"),
    SW_BUNDLE_DIR(null, Categories.INSTALL_LAUNCH, true, "The directory containing bundle directories (into which bundle archives are unzipped)",
            "/home/seqware/SeqWare/provisioned-bundles"),
    SW_BUNDLE_REPO_DIR(null, Categories.INSTALL, true,
            "The directory containing bundle archives (into which a bundle archive is first copied during install)",
            "/home/seqware/SeqWare/released-bundles"),
    BUNDLE_COMPRESSION(null, Categories.INSTALL, false, "Default is to use compression, this can be set to OFF to disable compression", "ON"),
    OOZIE_URL(null, Categories.LAUNCH, true, "URL for the Oozie webservice", "http://localhost:11000/oozie"),
    OOZIE_APP_ROOT(null, Categories.LAUNCH, true, "HDFS directory for storing workflow xml", "seqware_workflow"),
    OOZIE_JOBTRACKER(null, Categories.LAUNCH, true, "Hadoop job tracker, used to schedule jobs for oozie-hadoop engine", "localhost:8021"),
    OOZIE_NAMENODE(null, Categories.LAUNCH, true, "Hadoop name node, possibly redundant (should be refactored)", "hdfs://localhost:8020"),
    OOZIE_QUEUENAME(null, Categories.LAUNCH, true, "Hadoop queue onto which to schedule jobs", "default"),
    OOZIE_WORK_DIR(null, Categories.LAUNCH, true,
            "Working directory where your workflow steps execute and where we store generated scripts and logs", "/usr/tmp/seqware-oozie"),
    OOZIE_RETRY_MAX(null, Categories.LAUNCH, false, "Number of times that Oozie and Whitestar will retry user steps in workflows", "5"),
    OOZIE_RETRY_INTERVAL(null, Categories.LAUNCH, false, "Minutes to wait before retry for user steps in workflows", "5"),
    OOZIE_BATCH_THRESHOLD(null, Categories.LAUNCH, false,
            "Above this threshold, provision file events on the same job/workflow will be batched together", "10"),
    OOZIE_BATCH_SIZE(null, Categories.LAUNCH, false, "Number of provision file events that should be batched together", "100"),
    WHITESTAR_MEMORY_LIMIT(null, Categories.WHITESTAR, false, "Restrict the number of parallel jobs invoked in WhiteStar to this amount of memory",String.valueOf(Integer.MAX_VALUE)),
    FS_HDFS_IMPL("FS.HDFS.IMPL", Categories.LAUNCH, true, "HDFS implementation class", "org.apache.hadoop.hdfs.DistributedFileSystem"),
    OOZIE_SGE_THREADS_PARAM_FORMAT(null, Categories.LAUNCH, false,
            "Only used for 'oozie-sge' engine. Format of qsub flag for specifying number of threads. "
                    + "If present, ${threads} will be replaced with the job-specific value.", "-pe serial ${threads}"),
    OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT(null , Categories.LAUNCH, true, "Format of qsub flag for specifying the max memory. "
            + "If present, ${maxMemory} will be replaced with the job-specific value.", "-l h_vmem=${maxMemory}M"),
    SW_CONTROL_NODE_MEMORY(null, Categories.ADMIN, false, "In atypical environments, the default h_vmem constraint for SGE is too stringent. Override them using this (units in megabytes)", "4000"),
    SW_ADMIN_REST_URL(null, Categories.ADMIN, false, "Location of the admin web service, currently used for deletion", "http://localhost:38080/seqware-admin-webservice"),
    SW_LOCK_ID(null, Categories.ADMIN, false, "Used to override the JUnique lock used to ensure that utilities don't run concurrently","seqware"),
    SW_ENCRYPT_KEY(null, Categories.ADMIN, false,"Legacy key used to encrypt provisioned files", "seqware"),
    SW_DECRYPT_KEY(null, Categories.ADMIN, false,"Legacy key used to decrypt provisioned files", "seqware"),
    SW_PROVISION_FILES_MD5(null, Categories.LAUNCH, false, "Used to determine whether provisioned (out) files should be run through MD5 before and after provisioning", "true"),
    BASIC_TEST_DB_HOST(null, Categories.TESTING, false,"Used to designate a database for integration tests","localhost"),
    BASIC_TEST_DB_NAME(null, Categories.TESTING, false,"Used to designate a database name for integration tests","seqware_meta_db"),
    BASIC_TEST_DB_USER(null, Categories.TESTING, false,"Used to designate a database username for integration tests","seqware"),
    BASIC_TEST_DB_PASSWORD(null, Categories.TESTING, false,"Used to designate a database password for integration tests","seqware"),
    EXTENDED_TEST_DB_HOST(null, Categories.TESTING, false,"Used to designate a database for extended integration tests","localhost"),
    EXTENDED_TEST_DB_NAME(null, Categories.TESTING, false,"Used to designate a database name for extended integration tests","seqware_meta_db"),
    EXTENDED_TEST_DB_USER(null, Categories.TESTING, false,"Used to designate a database username for extended integration tests","seqware"),
    EXTENDED_TEST_DB_PASSWORD(null, Categories.TESTING, false,"Used to designate a database password for extended integration tests","seqware")
    ;
    // @formatter:on

    private final Categories category;
    private final String description;
    private final String defaultValue;
    private final String[] possibleValues;
    private final String overrideName;
    private final boolean required;

    private SqwKeys(String overrideName, Categories category, boolean required, String description, String defaultValue,
            String... possibleValues) {
        this.overrideName = overrideName;
        this.category = category;
        this.description = description;
        this.defaultValue = defaultValue;
        this.possibleValues = possibleValues;
        this.required = required;
    }

    /**
     * Returns the name of the key to be used in the seqware settings file
     *
     * @return
     */
    public String getSettingKey() {
        if (overrideName != null) {
            return overrideName;
        }
        return this.name();
    }

    /**
     * For documentation
     *
     * @return the category
     */
    public Categories getCategory() {
        return category;
    }

    /**
     * For documentation
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This is the value used to populate our auto-generated documentation
     *
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * For documentation
     *
     * @return the possibleValues
     */
    public String[] getPossibleValues() {
        return Arrays.copyOf(possibleValues, possibleValues.length);
    }

    /**
     * Return whether or not this variable is generally required for a typical Oozie-sge install with metadata.
     *
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    public enum Categories {
        // @formatter:off
        COMMON("Common Seqware settings"),
        COMMON_WS("Seqware webservice settings. Only used if SW_METADATA_METHOD=webservice"),
        COMMON_DB(
                "Seqware database settings. Only used if SW_METADATA_METHOD=database and by the database check utility"),
        SCHEDULE_LAUNCH(
                "Settings used by scheduling and launching bundles"),
        INSTALL_LAUNCH(
                "Settings used by both installing and launching bundles"),
        INSTALL(
                "Settings used to configure the installation of workflow bundles"),
        LAUNCH(
                "Oozie engine settings. Only used for both 'oozie' and 'oozie-sge' engines."),
        WHITESTAR("WhiteStar engine settings. Only used for the 'whitestar' series of engines."),
        OOZIE_SGE(
                "Oozie-SGE engine settings. Only used for 'oozie-sge' engine."),
        ADMIN("Settings used for administrators"),
        TESTING("Used for regression testing");
        // @formatter:on

        private final String categoryDescription;

        Categories(String categoryDescription) {
            this.categoryDescription = categoryDescription;
        }

        /**
         * For documentation
         *
         * @return the categoryDescription
         */
        public String getCategoryDescription() {
            return categoryDescription;
        }
    }
}
