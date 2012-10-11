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
package net.sourceforge.seqware.pipeline.modules.examples;

import net.sourceforge.seqware.pipeline.runner.PluginRunner;

/**
 *
 * @author yongliang
 */
public class MultiEchoTest {
	
	private void test() {
		String[] a = new String[] {"--plugin", 
			"net.sourceforge.seqware.pipeline.plugins.ModuleRunner",
			"--",  "--metadata-parentID", "25653", "--module",
			"net.sourceforge.seqware.pipeline.modules.examples.echo",
			"--",  "Hey"};
		for(int i=0; i<1; i++) {
			ThreadTest tt = new ThreadTest();
			tt.setArgs(a);
			new Thread(tt).start();
		}		
	}
	
	public static void main(String args[]) {
		MultiEchoTest test = new MultiEchoTest();
		test.test();
	}
	
	class ThreadTest implements Runnable {

		private String[] args;
		
		public void setArgs(String[] a) {
			this.args = a;
		}
		
		@Override
		public void run() {
			PluginRunner runner = new PluginRunner();
			runner.run(args);
		}
		
	}
}
