---

title:                 "ProcessingDataStructure2Dot"
toc_includes_sections: true
markdown:              basic
is_dynamic:		true

---

The ProcessingDataStructure2Dot plugin allows you to convert a section of the processing hierarchy into a dot file which can be converted into a graphics format of your choice. This allows you to visualize the processing hierarchy created by a particular workflow.  

##Examples

Extract the processing hierarchy rooted at the processing SWID 5174:

    $java -jar ~/.seqware/self-installs/seqware-distribution-1.1.0-alpha.2-SNAPSHOT-full.jar -p net.sourceforge.seqware.pipeline.plugins.ProcessingDataStructure2Dot -- --parent-accession 5174
    Writing dot file to output.dot

    $dot -Tpng output.dot > output.png

Note for the latter step, you may need to install graphviz or a dot file viewer of your choice. 

