---
markdown:	advanced
toc_includes_sections:	true
is_dynamic:     true
title: File Attributes | SeqWare API
---

# File Attributes API

Management of file attributes via the API requires that you are
authenticated.

## List attributes for a file

    GET /file/:swa/attributes

### Response

<%= headers 200 %>
~~~
<?xml version="1.0" encoding="UTF-8" ?>
<list>
    <net.sourceforge.seqware.webservice.dto.AttributeDto>
        <entityUrl>http://localhost:8889/seqware-webservice/files/12056</entityUrl>
        <url>http://localhost:8889/seqware-webservice/file/12056/attribute/11</url>
        <name>mime-type</name>
        <value>application/json</value>
    </net.sourceforge.seqware.webservice.dto.AttributeDto>
    <net.sourceforge.seqware.webservice.dto.AttributeDto>
        <entityUrl>http://localhost:8889/seqware-webservice/files/12056</entityUrl>
        <url>http://localhost:8889/seqware-webservice/file/12056/attribute/6</url>
        <name>region</name>
        <value>1</value>
    </net.sourceforge.seqware.webservice.dto.AttributeDto>
</list>
~~~

## Add file attribute

    POST /file/:swa/attribute

### Input

You can post a single file attribute:

<%= headers 0,
      :'Content-Type' =>
'application/xml'  %>
~~~
<?xml version="1.0" encoding="UTF-8" ?>
<net.sourceforge.seqware.webservice.dto.AttributeDto>
    <name>mime-type</name>
    <value>application/xml</value>
</net.sourceforge.seqware.webservice.dto.AttributeDto>
~~~


### Response

<%= headers 201 %>

## Get a file attribute

    GET /file/:swa/attribute/:id

### Response

<%= headers 200 %>
~~~
<?xml version="1.0" encoding="UTF-8" ?>
<net.sourceforge.seqware.webservice.dto.AttributeDto>
    <entityUrl>http://localhost:8889/seqware-webservice/files/12056</entityUrl>
    <url>http://localhost:8889/seqware-webservice/file/12056/attribute/1</url>
    <name>location</name>
    <value>/archives/</value>
</net.sourceforge.seqware.webservice.dto.AttributeDto>
~~~

## Update a file attribute

    PUT /file/:swa/attribute/:id

### Input

You can update a single file attribute:

<%= headers 0,
      :'Content-Type' =>
'application/xml'  %>
~~~
<?xml version="1.0" encoding="UTF-8" ?>
<net.sourceforge.seqware.webservice.dto.AttributeDto>
    <name>mime-type</name>
    <value>application/xml</value>
</net.sourceforge.seqware.webservice.dto.AttributeDto>
~~~

### Response

<%= headers 200,
      :Location =>
'http://localhost:8889/seqware-webservice/file/12056/attribute/8'  %>


## Delete a file attribute

    DELETE /file/:swa/attribute/:id

### Response

<%= headers 200 %>
