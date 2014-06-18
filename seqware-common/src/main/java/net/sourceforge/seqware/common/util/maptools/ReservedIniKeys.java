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

package net.sourceforge.seqware.common.util.maptools;

/**
 * This enum collects all reserved ini file keys that are reserved by SeqWare.
 * 
 * @author dyuen
 */
public enum ReservedIniKeys {
    PARENT_UNDERSCORE_ACCESSIONS("parent_accessions"), PARENT_DASH_ACCESSIONS("parent-accessions"), OUTPUT_PREFIX("output_prefix"), OUTPUT_DIR(
            "output_dir"), PARENT_ACCESSION("parent_accession"), MANUAL_OUTPUT("manual_output"), INPUT_FILE("input_file"), WORKFLOW_RUN_ACCESSION_DASHED(
            "workflow-run-accession"), WORKFLOW_RUN_ACCESSION_UNDERSCORES("workflow_run_accession"), METADATA("metadata"), WORKFLOW_BUNDLE_DIR(
            "workflow_bundle_dir"), SEQWARE_LINES_NUMBER("seqware-output-lines-number");

    private final String key;

    ReservedIniKeys(String key) {
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
}
