    <variants>
      <#list data as item>
        <mismatches>
          <swid>${item.swid}</swid>
          <filepath>${item.filePath}</filepath>
          <description>${item.desc}</description>
          <parameters>${item.params}</parameters>
          <uri>${item.uri}/queryengine/realtime/variants/mismatches/${item.swid}</uri>
        </mismatches>
      </#list>      
    </variants>