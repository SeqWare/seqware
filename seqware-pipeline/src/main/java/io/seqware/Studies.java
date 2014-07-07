package io.seqware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

public class Studies {

    private static void add(List<String> strs, Object o) {
        if (o == null)
            strs.add("");
        else
            strs.add(o.toString());
    }

    private static void tsv(StringBuilder sb, List<String> ss) {
        if (!ss.isEmpty()) {
            sb.append(ss.get(0));
            for (int i = 1; i < ss.size(); i++) {
                sb.append('\t');
                sb.append(ss.get(i));
            }
            sb.append('\n');
        }
    }

    public static String studiesTsv() {
        Metadata md = MetadataFactory.get(ConfigTools.getSettings());
        List<Study> studies = md.getAllStudies();
        StringBuilder sb = new StringBuilder();
        List<String> strs = new ArrayList<>(Arrays.asList("Title", "Description", "Creation Date", "SeqWare Accession", "Study Type",
                "Center Name", "Center Project Name"));
        tsv(sb, strs);

        for (Study s : studies) {
            strs.clear();
            add(strs, s.getTitle());
            add(strs, s.getDescription());
            add(strs, s.getCreateTimestamp());
            add(strs, s.getSwAccession());
            StudyType t = s.getExistingType();
            add(strs, t == null ? "" : t.getName() + " (" + t.getStudyTypeId() + ")");
            add(strs, s.getCenterName());
            add(strs, s.getCenterProjectName());
            tsv(sb, strs);
        }

        return sb.toString();
    }
}
