     <#list data as item>
        <tags>
          <swid>${item.swid}</swid>
          <filepath>${item.filePath}</filepath>
          <description>${item.desc}</description>
          <uri>${item.uri}/queryengine/static/variants/tags/${item.swid}</uri>
        </tags>
      </#list>