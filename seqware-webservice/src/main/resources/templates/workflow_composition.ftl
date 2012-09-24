<?xml version="1.0"?>
<?xml-stylesheet href="/queryengine/static/xslt/queryengine.xsl" type="text/xsl" ?>
<queryengine>
  <static>
    <variants>
      <#list data as item>
        <tags>
          <swid>${item.swid}</swid>
          <filepath>${item.filePath}</filepath>
          <description>${item.desc}</description>
          <uri>${item.uri}/queryengine/static/variants/tags/${item.swid}</uri>
        </tags>
      </#list>
    </variants>
  </static>
</queryengine>