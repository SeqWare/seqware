package net.sourceforge.seqware.pipeline.plugins.filelinker;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;

import org.junit.Test;

public class MetadataPluginTest {
	@Test
	public void testMetadataOut() {
		File file = null;
		try {
			file = FileTools.createFileWithUniqueName(new File("/tmp"), "metadataoutputtest");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String opt = "--plugin net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables --output-file";
		String[] args = opt.split(" ");
		List<String> list = new ArrayList<String>(Arrays.asList(args));
		list.add(file.getAbsolutePath());
		String[] args2 = list.toArray(args);
		new PluginRunner().run(args2);
		assertTrue(FileTools.fileExistsAndNotEmpty(file).getReturnValue() == ReturnValue.SUCCESS);
	}


}