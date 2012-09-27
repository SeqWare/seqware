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
 * 
 * @author yongliang
 */
public class SeqwareModuleJob extends Job1 {
    private Module module;

    public SeqwareModuleJob(String algo) {
	this(algo, Module.Seqware_GenericCommandRunner);
    }

    public SeqwareModuleJob(String algo, boolean local) {
	this(algo, Module.Seqware_GenericCommandRunner, local);
    }

    public SeqwareModuleJob(String algo, Module module) {
	this(algo, module, false);
    }

    public SeqwareModuleJob(String algo, Module module, boolean local) {
	super(algo);
	this.name = local ? "java_local" : "seqware";
	this.version = local ? "1.6.0" : "0.12.5";
	this.module = module;
    }

    public Module getModule() {
	return this.module;
    }

}
