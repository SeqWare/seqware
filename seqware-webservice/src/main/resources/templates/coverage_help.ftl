<html><body>
<h1>Query Engine</h1>
<h2>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/coverage">Coverage</a>/<a href="/queryengine/realtime/coverage/basecoverage">Base Coverage</a></h2>
<h3>Documentation</h3>
This URL requires parameters to render.
 <h4>Required</h4>
  <ul>
      <li>format=[wig|wig_verbose|ave_wig|form|html|igv.xml|help]: the output format</li>
        <ul>
          <li>wig:         returns variant data in WIGGLE format suitable for loading in the <a href=\"http://genome.ucsc.edu\">UCSC genome browser</a></li>
          <li>wig_verbose: returns variant data in WIGGLE format, includes positions with zero coverage</li>
          <li>ave_wig:     returns variant data in WIGGLE format, only one line per coverage block with the average coverage for that block reported as the count</li>
          <li>form:        returns an HTML form for constructing a query on this resource</li>
          <li>html:        returns an HTML document that links to the UCSC and other browsers</li>
          <li>igv.xml:     returns an XML session document used by the IGV genome browser that points to these results</li>
          <li>help:        prints this help documentation</li>
        </ul>
  </ul>
 <h4>Optional</h4>
  <ul>
      <li>filter.contig=[contig_name|all]: one or more contigs must be specified, multiple contigs are given as separate params</li>
      <li>track.name=string: the name for the track</li>
      <li>track.options=string: put options (key/values) for tracks in the genome browser here, name can be placed here as name=value or in track.name field</li>
  </ul>
 <h4>Example</h4>
 <pre>http://{hostname}:{port}/seqware/queryengine/realtime/coverage/basecoverages/{id}?format=wig&filter.contig=chr22&filter.contig=chr10</pre>
 This example queries a given base coverage resource, specified by id, and returns the coverages in WIG format for chr22 and chr10.
</body></html>