package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import com.google.common.collect.Lists;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

class Archiver {

    private final String oozieWorkingDir;
    private final File scriptsDir;

    Archiver(String oozieWorkingDir, File scriptsDir){
        this.oozieWorkingDir = oozieWorkingDir;
        this.scriptsDir = scriptsDir;
    }

    boolean archiveSeqWareMetadataCalls(String command){
        return writeToFile("archive_script_with_metadata.sh", command);
    }

    boolean archiveWorkflowCommands(String command){
        return writeToFile("archive_script.sh", command);
    }

    private boolean writeToFile(String filename, String command) {
        // for archive scripts
        final Path shortArchivePath = Paths.get(scriptsDir.getAbsolutePath(), filename);
        final Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString("rwxr-x---");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(posixFilePermissions);
        try {
            if (Files.notExists(shortArchivePath)) {
                Files.createFile(shortArchivePath, attr);
                StringBuilder sb = new StringBuilder("#!/usr/bin/env bash\nset -o errexit\nset -o pipefail\n\nexport "
                        + ConfigTools.SEQWARE_SETTINGS_PROPERTY + "=");
                sb.append(ConfigTools.getSettingsFilePath());
                sb.append("\ncd ");
                sb.append(oozieWorkingDir);
                sb.append("\n");
                Files.write(shortArchivePath, Lists.newArrayList(sb), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
            Files.write(shortArchivePath, Lists.newArrayList(command), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
