/*
 * Copyright (C) 2013 SeqWare
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
package io.seqware.webservice.controller;

/**
 * Holds information for the recursive deletion tool
 * @author dyuen
 */
public class ModelAccessionIDTuple {
    private int accession;
    private int id;
    private String adminModelClass;
    
     public ModelAccessionIDTuple() {
    }

    public ModelAccessionIDTuple(int accession, int id, String adminModelClass) {
        this.accession = accession;
        this.id = id;
        this.adminModelClass = adminModelClass;
    }

    /**
     * @return the accession
     */
    public int getAccession() {
        return accession;
    }

    /**
     * @param accession the accession to set
     */
    public void setAccession(int accession) {
        this.accession = accession;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the adminModelClass
     */
    public String getAdminModelClass() {
        return adminModelClass;
    }

    /**
     * @param adminModelClass the adminModelClass to set
     */
    public void setAdminModelClass(String adminModelClass) {
        this.adminModelClass = adminModelClass;
    }
    
}
