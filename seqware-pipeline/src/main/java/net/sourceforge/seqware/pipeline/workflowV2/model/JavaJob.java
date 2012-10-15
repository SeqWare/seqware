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
 * <p>JavaJob class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
public class JavaJob extends Job {
	/**
	 * <p>Constructor for JavaJob.</p>
	 *
	 * @param algo a {@link java.lang.String} object.
	 */
	public JavaJob(String algo) {
		super(algo);
		this.name = "java";
		this.version = "1.6.0";
	}



}
