<?xml version="1.0" encoding="UTF-8"?>
  <xsl:stylesheet 
   version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns="http://www.w3.org/1999/xhtml">
 
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
 
  <xsl:template match="/queryengine/asynchronous">
    <html>
      <head> <title>SeqWare Web Service</title> </head>
      <body>
        <h1>Asynchronous Workflow Submission Tool</h1>
          <p>
            This is the SeqWare Web Service, part of the <a href="http://seqware.sf.net">SeqWare open source project</a> hosted on SourceForge.
            The web service presented here lets you trigger analytical workflows using a simple RESTful API.  The process is designed to be 
            asynchronous since some of the workflows may take days to run.  The service will return a tracking ID after you submit your
            workflow request which you can use to monitor the status of the workflow and, ultimate, request the results back.
          </p>
          <xsl:apply-templates select="*/workflow">
            <xsl:sort select="uri"/>
          </xsl:apply-templates>
      </body>
    </html>
  </xsl:template>
 
  <xsl:template match="*/workflow">
    <ul>
      <form method="post" enctype="multipart/form-data">
      <xsl:attribute name="action"><xsl:value-of select="@uri"/></xsl:attribute>
      <li> Workflow: <xsl:value-of select="@uri"/>
        <ul>
          <xsl:for-each select="workflow_params/param">
          <xsl:choose>
              <xsl:when test="@display = 't'">
            <li>Name: <xsl:value-of select="@key"/></li>
            <li>Type: <xsl:value-of select="@type"/></li>
            <li>Default Value: <xsl:value-of select="@default_value"/></li>
            <li>File Metatype: <xsl:value-of select="@file_meta_type"/></li>
            <li>
            <xsl:choose>
              <xsl:when test="@type = 'file'"><input type="file"><xsl:attribute name="name"><xsl:value-of select="@key"/></xsl:attribute></input></xsl:when>
              <xsl:otherwise><input type="text"><xsl:attribute name="name"><xsl:value-of select="@key"/></xsl:attribute></input></xsl:otherwise>
            </xsl:choose>
            </li>
            </xsl:when>
           </xsl:choose>
          </xsl:for-each>
          <input type="submit"/>
        </ul>
      </li>
      </form>
    </ul>
  </xsl:template>
 
</xsl:stylesheet>
