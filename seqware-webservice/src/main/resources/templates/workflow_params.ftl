<?xml version="1.0"?>
<?xml-stylesheet href="${rooturl}/queryengine/static/xslt/workflow_params.xsl" type="text/xsl" ?>
<queryengine>
  <asynchronous>
    <workflows>
        <workflow uri="${rooturl}/queryengine/asynchronous/workflow/${workflowId}">
          <workflow_params>
          <#list data as item>
              <param <#list item?keys as name>
              ${name}="${item[name]}" </#list>/>
          </#list>
          </workflow_params>
        </workflow>
    </workflows>
  </asynchronous>
</queryengine>