<html><body>
<h1>Query Engine</h1>
<h2>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/consequences">Consequences</a>/<a href="/queryengine/realtime/consequences/mismatchconsequences">Mismatch Consequences</a></h2>

<h3>Documentation</h3>
This URL requires parameters to render.
 <h4>Required</h4>
  <ul>
      <li>format=[tab|form|help]: the output format</li>
        <ul>
          <li>tab:         returns variant consequence data in tab-delimited format</li>
          <li>form:        returns an HTML form for constructing a query on this resource</li>
          <li>help:        prints this help documentation</li>
        </ul>
  </ul>
  
 <h4>Optional</h4>
  <ul>
      <li>filter.contig=[contig_name|all]: one or more contigs must be specified, multiple contigs are given as separate params</li>
  </ul>
 <h4>Example</h4>
 <pre>http://{hostname}:{port}/seqware/queryengine/realtime/consequence/mismatchconsequences/{id}?format=tab&filter.contig=chr22&filter.contig=chr10</pre>
 This example queries a given mismatch consequence resource, specified by id, and returns the consequences in tab-delimited format for chr22 and chr10.
</body></html>