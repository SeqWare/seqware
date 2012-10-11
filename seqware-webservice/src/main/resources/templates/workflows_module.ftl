    <workflows>
      <#list data as item>
        <workflow>
          <swid>${item.sw_accession}</swid>
          <name>${item.name}</name>
          <description>${item.description}</description>
          <version>${item.version}</version>
          <seqware_version>${item.seqware_version}</seqware_version>
        </workflow>
      </#list>      
    </workflows>