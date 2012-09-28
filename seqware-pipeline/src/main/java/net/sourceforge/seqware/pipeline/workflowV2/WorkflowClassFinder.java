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
package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import net.sourceforge.seqware.common.util.Log;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;




public class WorkflowClassFinder {
	

	private final static String FOLDERS_SEPARATOR_AS_STRING = System.getProperty("file.separator");

	private final ResourcePatternResolver resourceResolver;

	public WorkflowClassFinder() {
		this.resourceResolver = new PathMatchingResourcePatternResolver(
			Thread.currentThread().getContextClassLoader());
	}

	public Class<?> findFirstWorkflowClass(String clazzPath) {
		String candidateClassesLocationPattern = "file:" + 
			clazzPath + "**" + FOLDERS_SEPARATOR_AS_STRING + "*.class";
		Resource[] resources = null;

		try {
			resources = resourceResolver.getResources(candidateClassesLocationPattern);	
		} catch (IOException e) {
			throw new RuntimeException(
			"An I/O problem occurs when trying to resolve "
				+ "ressources matching the pattern : "
			+ candidateClassesLocationPattern, e);

		}
		for(Resource resource: resources) {
			try {
				//get the path
				String path = resource.getFile().getPath();
				String qPath = path.substring(clazzPath.length(),path.length() 
					- ".class".length());
				qPath = qPath.replaceAll(FOLDERS_SEPARATOR_AS_STRING, ".");
				URL url = new URL("file://"+clazzPath);
				URL[] urls = new URL[]{url};
				ClassLoader cl = new URLClassLoader(urls);				
				Class cls = cl.loadClass(qPath);
				Log.info("CLASS LOADED "+ qPath);
				return cls;
				
			} catch (IOException ex) {
				Log.error(ex);
			} catch (ClassNotFoundException ex) {
				Log.error(ex);
			}
		}
		return null;

	}

}
