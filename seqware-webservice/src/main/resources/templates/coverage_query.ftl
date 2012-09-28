<html>
<form name="input" action="${url}" method="get">
<!--input type="hidden" name="format" value="wig"/-->
<h1>Query Engine</h1>
<h2>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/coverage">Coverage</a>/<a href="/queryengine/realtime/coverage/basecoverage">Base Coverage</a></h2>
<h3>Contigs</h3>
<p>This can be "chr22", "chr22,chr10", or "chr22:12121-13243"</p>
<input size="60" type="text" name="filter.contig"/>
<h3>UCSC Track Name</h3>
<p>The name for display in the UCSC browser.</p>
<input type="text" name="track.name" value=""/>
<p/>
<h3>UCSC Track Options</h3>
<p>Key/value pairs for track options in the UCSC browser, see <a href="http://genome.ucsc.edu/goldenPath/help/customTrack.html"></a> for more information.</p>
<input type="text" name="track.options" value=""/>
<p/>
<h3>Output Format</h3>
<p>Chooese an output format type.  The WIG format can be used in a variety of genome browsers or
 within your own programs. "Verbose WIG" is generally not useful, it includes every position within the range
 specified whether or not the position has a non-zero coverage. This results in very large output files,
 use sparingly.  "WIG Average by Blocks" presents the average coverage over fixed-width blocks in the genome. 
 The block size is specified when the database was created. "Browser Links" takes you to a page
 where you can dynamically load the result in one of the genome browsers with one click. Finally, 
 "IGV Browser Session File" returns an XML session file for use with IGV (the "Browser Links" is
 a more automated way of loading this file in IGV). You can merge multiple session files from multiple
 queries for bulk loading of the tracks in IGV.
 </p>
 <p>
<select type="select" name="format">
  <option value="wig" selected>WIG</option>
  <option value="wig_verbose">Verbose WIG</option>
  <option value="ave_wig">WIG Averaged by Blocks</option>
  <option value="html">Browser Links</option>
  <option value="igv.xml">IGV Browser Session File</option>
</select>
</p>
<input type="submit"/>
<input type="reset"/>
</form>
</html>
