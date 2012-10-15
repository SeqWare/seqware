/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * <p>SeqwareModuleJob class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public class SeqwareModuleJob extends Job {
    private Module module;

    /**
     * <p>Constructor for SeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     */
    public SeqwareModuleJob(String algo) {
	this(algo, Module.GenericCommandRunner);
    }

    /**
     * <p>Constructor for SeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param local a boolean.
     */
    public SeqwareModuleJob(String algo, boolean local) {
	this(algo, Module.GenericCommandRunner, local);
    }

    /**
     * <p>Constructor for SeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param module a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
     */
    public SeqwareModuleJob(String algo, Module module) {
	this(algo, module, false);
    }

    /**
     * <p>Constructor for SeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param module a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
     * @param local a boolean.
     */
    public SeqwareModuleJob(String algo, Module module, boolean local) {
	super(algo);
	this.name = local ? "java_local" : "seqware";
	this.version = local ? "1.6.0" : "0.12.5";
	this.module = module;
    }

    /**
     * <p>Getter for the field <code>module</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
     */
    public Module getModule() {
	return this.module;
    }

}
