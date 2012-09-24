<?xml version="1.0" encoding="UTF-8"?>
  <xsl:stylesheet 
   version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns="http://www.w3.org/1999/xhtml">
 
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
 
  <xsl:template match="/queryengine/realtime">
    <html>
      <head> <title>QueryEngine</title> </head>
      <body>
        <h1>Query Engine</h1>
          <p>
            This is the SeqWare QueryEngine, for more information please see the <a href="http://seqware.sf.net">SeqWare site</a> hosted on SourceForge.
          </p>
        <h2>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a></h2>
          <p>
            These are tools designed for realtime, interactive querying of various genomic datasets.
          </p>

        <h3>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/variants">Variants</a></h3>
        <p>
          Variant events including SNVs, indels, translocations, and other genomic events typically identified through sequencing.
        </p>
        <h4>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/variants">Variants</a>/<a href="/queryengine/realtime/variants/mismatches">Mismatches</a></h4>
        <p>
          These include Single Nucleotide Variants (SNVs) and insertions/deletions (indels).
        </p>
          <xsl:apply-templates select="*/mismatches">
            <xsl:sort select="swid"/>
          </xsl:apply-templates>

        <!--h4>Translocations</h4>
        <p>
         These include both inter- and intrachromosomal translocation events. 
        </p>
        <h4>Copy Number Variations</h4>
        <p>
          These include copy number variants.
        </p>
        <h4>Inversions</h4>
        <p>
          These include inversion events.
        </p-->
        
        <h3>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/coverage">Coverage</a></h3>
          <p>
            Coverage includes tools for querying sequencing depth coverage.
          </p>
          <h4>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/coverage">Coverage</a>/<a href="/queryengine/realtime/coverage/basecoverage">Base Coverage</a></h4>
            <p>
              This represents base coverage at given positions on the genome.
            </p>
            <ul>
        <xsl:apply-templates select="*/basecoverage">
        <xsl:sort select="swid"/>
        </xsl:apply-templates>
            </ul>
          <!--h4>Clone Coverage</h4>
            <p>
              This represents clone coverage at given positions on the genome.
            </p-->
        
        <h3>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/consequences">Coding Consequence</a></h3>
          <p>
            Includes predictions of gene coding consequence changes from variants.
          </p>
          <h4>/<a href="/queryengine">Query Engine</a>/<a href="/queryengine/realtime">Realtime Analysis Tools</a>/<a href="/queryengine/realtime/consequences">Coding Consequence</a>/<a href="/queryengine/realtime/consequences/mismatchconsequences">Mismatch Consequences</a></h4>
            <p>
              Gene coding consequences for SNVs and indels.
            </p>
            <ul>
        <xsl:apply-templates select="*/mismatchconsequences">
        <xsl:sort select="swid"/>
        </xsl:apply-templates>
            </ul>
          <!--h4>Translocation Consequences</h4>
            <p>
              Gene coding consequences for translocations.
            </p>
          <h4>Copy Number Variation Consequences</h4>
            <p>
              Gene coding consequences for CNVs.
            </p>
          <h4>Inversion Consequences</h4>
            <p>
              Gene coding consequences for inversions.
            </p-->
      </body>
    </html>
  </xsl:template>
 
  <xsl:template match="*/mismatches">
        <ul>
    <li>
      Database Identifier: <xsl:value-of select="swid"/>
        <ul>
          <li>
            Description: <xsl:value-of select="description"/>
          </li>
          <li>
            Filepath: <xsl:value-of select="filepath"/>
          </li>
          <li>
            Parameters used: <xsl:value-of select="parameters"/>
          </li>
          <li>
            Query this database:
            <ul>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=help</xsl:attribute>Instructions for building a query programmatically</a>
              </li>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=form</xsl:attribute>Form for building a query interactively</a>
              </li>
            </ul>
          </li>
        </ul>
    </li>
    </ul>
  </xsl:template>
    
  <xsl:template match="*/basecoverage">
    <li>
      Database Identifier: <xsl:value-of select="swid"/>
        <ul>
          <li>
            Description: <xsl:value-of select="description"/>
          </li>
          <li>
            Filepath: <xsl:value-of select="filepath"/>
          </li>
          <li>
            Parameters used: <xsl:value-of select="parameters"/>
          </li>
          <li>
            Query this database:
            <ul>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=help</xsl:attribute>Instructions for building a query programmatically</a>
              </li>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=form</xsl:attribute>Form for building a query interactively</a>
              </li>
            </ul>
          </li>
        </ul>
    </li>
  </xsl:template>
    
  <xsl:template match="*/mismatchconsequences">
    <li>
      Database Identifier: <xsl:value-of select="swid"/>
        <ul>
          <li>
            Description: <xsl:value-of select="description"/>
          </li>
          <li>
            Filepath: <xsl:value-of select="filepath"/>
          </li>
          <li>
            Parameters used: <xsl:value-of select="parameters"/>
          </li>
          <li>
            Query this database:
            <ul>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=help</xsl:attribute>Instructions for building a query programmatically</a>
              </li>
              <li>
               <a> <xsl:attribute name="href"><xsl:value-of select="uri"/>?format=form</xsl:attribute>Form for building a query interactively</a>
              </li>
            </ul>
          </li>
        </ul>
    </li>
  </xsl:template>
 
</xsl:stylesheet>
