---
title: Libraries API
---

Libraries are Samples that are capable of being run on an instrument.

## Get a single library

    GET /x/library/:swa
    
The Library is a leaf in a tree. The ancestors of the library represent steps taken in 
the handling and in the wet lab that led to the creation of the library. All the attributes
from all of the libraries ancestors are collected and displayed along with the library.

### Response

<%= headers 200 %>
<%= json :library %>


## Get a list of libraries

    GET /x/libraries/:swas
    
List all the libraries or a subset of libraries. Use the `attributes` parameter to list
a set of libraries that match a specific list of attributes.

### Input

You can provide a list of library swas to dislay.

<pre class="highlight"><code class="language-javascript">
182467,186014,25877
</code></pre>

If no swas are provided all libraries will be displayed.

### Parameters

attributes
: _String_ of comma separated attributes. Equal signs and spaces must be escaped.
  Example: attributes=geo_tissue_region%3D1,geo_tissue_preparation%3DFresh%20Frozen


### Response

<%= headers 200 %>
<pre class="highlight">
[
    {
        "url":"http://localhost:8888/seqware-webservice/library/186430",
        "name":"CPCG_0184_Pr_P_PE_309_WG",
        "description":"CPCG_0184_P1 Lib 2",
        "create_time_stamp":"2012-04-09T14:17:29-04:00",
        "update_time_stamp":"2012-04-09T14:17:44-04:00",
        "owner":
        {
            "email":"john.hurt@oicr.on.ca",
            "first_name":"John",
            "last_name":"Hurt",
            "institution":"Ontario Institue for Cancer Research"
        },
        "organism":
        {
            "name":"Homo sapiens",
            "code":"Homo_sapiens",
            "ncbi_taxonomy_id": 9606
        }
    },
    {
        "url":"http://localhost:8888/seqware-webservice/library/186428",
        "name":"CPCG_0184_Pr_P_PE_304_WG",
        "description":"CPCG_0184_P1 Lib 1",
        "create_time_stamp":"2012-04-09T14:15:15-04:00",
        "update_time_stamp":"2012-04-09T14:15:28-04:00",
        "owner":
        {
            "email":"veronica.cartwright@oicr.on.ca",
            "first_name":"Veronica",
            "last_name":"Cartwright",
            "institution":"Ontario Institue for Cancer Research"
        },
        "organism":
        {
            "name":"Homo sapiens",
            "code":"Homo_sapiens",
            "ncbi_taxonomy_id": 9606
        }
    }
]
</code></pre>
