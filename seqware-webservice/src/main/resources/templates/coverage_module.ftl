    <coverage>
      <#list data as item>
        <basecoverage>
          <swid>${item.swid}</swid>
          <filepath>${item.filePath}</filepath>
          <description>${item.desc}</description>
          <parameters>${item.params}</parameters>
          <uri>${item.uri}/queryengine/realtime/coverage/basecoverage/${item.swid}</uri>
        </basecoverage>
      </#list>      
    </coverage>