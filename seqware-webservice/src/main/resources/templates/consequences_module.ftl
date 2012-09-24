    <consequences>
      <#list data as item>
        <mismatchconsequences>
          <swid>${item.swid}</swid>
          <filepath>${item.filePath}</filepath>
          <description>${item.desc}</description>
          <parameters>${item.params}</parameters>
          <uri>${item.uri}/queryengine/realtime/consequences/mismatchconsequences/${item.swid}</uri>
        </mismatchconsequences>
      </#list>      
    </consequences>