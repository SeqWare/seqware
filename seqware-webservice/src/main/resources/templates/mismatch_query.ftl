<html>
<head> 
  <meta charset="utf-8"> 
  <title></title> 

  <link rel="stylesheet" href="/queryengine/static/css/jquery-ui-1.8.9.custom.css"> 
  <script src="/queryengine/static/js/jquery-1.4.4.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.core.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.widget.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.position.js"></script> 
  <script src="/queryengine/static/js/jquery.ui.autocomplete.js"></script> 
  <link rel="stylesheet" href="/queryengine/static/css/demos.css"> 

  <style> 
  .ui-autocomplete-loading { background: white url('/queryengine/static/images/ui-anim_basic_16x16.gif') right center no-repeat; }
  </style> 
  <script> 
  $(function() {
    function split( val ) {
      return val.split( /,\s*/ );
    }
    function extractLast( term ) {
      return split( term ).pop();
    }
 
    $( "#tags" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {
          $.getJSON( "/queryengine/static/variants/tags/6", {
            term: extractLast( request.term )
          }, response );
        },
        search: function() {
          // custom minLength
          var term = extractLast( this.value );
          if ( term.length < 3 ) {
            return false;
          }
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          return false;
        }
      });
 
    $( "#tags2" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {
          $.getJSON( "/queryengine/static/variants/tags/6", {
            term: extractLast( request.term )
          }, response );
        },
        search: function() {
          // custom minLength
          var term = extractLast( this.value );
          if ( term.length < 3 ) {
            return false;
          }
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          return false;
        }
      });

    $( "#tags3" )
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        source: function( request, response ) {
          $.getJSON( "/queryengine/static/variants/tags/6", {
            term: extractLast( request.term )
          }, response );
        },
        search: function() {
          // custom minLength
          var term = extractLast( this.value );
          if ( term.length < 3 ) {
            return false;
          }
        },
        focus: function() {
          // prevent value inserted on focus
          return false;
        },
        select: function( event, ui ) {
          var terms = split( this.value );
          // remove the current input
          terms.pop();
          // add the selected item
          terms.push( ui.item.value );
          // add placeholder to get the comma-and-space at the end
          terms.push( "" );
          this.value = terms.join( ", " );
          return false;
        }
      });

  });
  </script> 
</head> 
<body>
<form name="input" action="${url}" method="get">
<!-- input type="hidden" name="format" value="bed"/ -->
<h1>Query Engine</h1>
<h2>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/variants">Variants</a>/<a href="/queryengine/realtime/variants/mismatches">Mismatches</a></h2>
<h3>Contigs</h3>
<p>This can be "chr22", "chr22,chr10", or "chr22:12121-13243"</p>
<input size="60" type="text" name="filter.contig"/>
<h3>Tags (AND)</h3>
<p>Tags that variants must contain (multiple tags are treated as AND). This can be "isDbSNP", "notDbSNP", "snp131", "heterozygous", "homozygous", "SNV", "indel",
"inOmimGene", "intronic", "ncRNA", "UTR3", "UTR5", "exonic", "exonic;splicing", "splicing", "downstream", "upstream", "upstream;downstream", "nonsynonymous",
"synonymous", "frameshift", "nonframeshift", "stopgain", or gene identifier tags. These gene identifier tags are loaded for refGene models from UCSC.
Multiple tags can be separated by commas. 
The tag expected to reduce the result set the most should be listed first, this will reduce your query time since the list of variants is filtered in order 
the tags are entered here.
The tags listed here are ordered with most common tags listed first, you should
pick a tag toward the end of the list for the first item in your query if possible.</p>
<div class="ui-widget"> 
<input size="100" type="text" id="tags" name="filter.tag"/>
</div>

<h3>Tags (AND NOT)</h3>
<p>To match the variant must not contain any of these tags.</p>
<div class="ui-widget"> 
<input size="100" type="text" id="tags2" name="filter.tag.and.not"/>
</div> 

<h3>Tags (OR)</h3>
<p>To match the variant must contain at least one of these tags.</p>
<div class="ui-widget"> 
<input size="100" type="text" id="tags3" name="filter.tag.or"/>
</div>

<h3>Size</h3>
<p>For indels, the size range, inclusive.</p>
<input type="text" name="filter.size"/>
<h3>Min Coverage</h3>
<p>Minimum coverage at a given position, default is 0</p>
<input type="text" name="filter.minCoverage"/>
<h3>Max Coverage</h3>
<p>Maximum coverage at a given position, default is 2147483647</p>
<input type="text" name="filter.maxCoverage"/>
<h3>Min Variant Phred</h3>
<p>Minimum phred score for the mismatch call, default is 0</p>
<input type="text" name="filter.minPhred"/>
<h3>Min Observations</h3>
<p>Minimum observations for the mismatch call, default is 0</p>
<input type="text" name="filter.minObservations"/>
<h3>Min Observations Per Strand</h3>
<p>Minimum number of times a mismatch must be seen at a given position on each strand, default is 0</p>
<input type="text" name="filter.minObservationsPerStrand"/>
<h3>Minimum Percent</h3>
<p>Minum percentage of time a mismatch must be seen at a given position, default is 0</p>
<input type="text" name="filter.minPercent"/>
<h3>Include SNVs</h3>
<p>[true|false]: indicates whether to include single nucleotide variants, default is true</p>
<input type="text" name="filter.includeSNVs" value="true"/>
<h3>Include Indels</h3>
<p>[true|false]: indicates whether to include small insertions/deletions, default is true</p>
<input type="text" name="filter.includeIndels" value="true"/>
<p/>
<h3>UCSC Track Name</h3>
<p>The name for display in the UCSC browser.</p>
<input type="text" name="track.name" value=""/>
<p/>
<h3>UCSC Track Options</h3>
<p>Key/value pairs for track options in the UCSC browser, see <a href="http://genome.ucsc.edu/goldenPath/help/customTrack.html"></a> for more information.</p>
<input type="text" name="track.options" value=""/>
<p/>
<h3>Output Format</h3>
<p>Chooese an output format type.  The BED format can be used in a variety of genome browsers or
 within your own programs. "Browser Links" takes you to a page
 where you can dynamically load the result in one of the genome browsers with one click. "Tags" will
 results in a non-redundant list of tags associated with the queried variants. Finally, 
 "IGV Browser Session File" returns an XML session file for use with IGV (the "Browser Links" is
 a more automated way of loading this file in IGV). You can merge multiple session files from multiple
 queries for bulk loading of the tracks in IGV.
 </p>
 <p>
<select type="select" name="format">
  <option value="bed" selected>BED</option>
  <option value="tags">Tags</option>
  <option value="html">Browser Links</option>
  <option value="igv.xml">IGV Browser Session File</option>
</select>
</p>
<input type="submit"/>
<input type="reset"/>
</form>
</body>
</html>
