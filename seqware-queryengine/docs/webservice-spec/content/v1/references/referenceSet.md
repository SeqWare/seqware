---
title: Reference Sets | Generic Feature Store API
---

# Reference Sets API

Management of reference sets via the API requires that you are
authenticated.

## List all reference sets

    GET /referenceSet
    
Gets a list of references sets that variants may be associated with. These are 
typically builds of particular genomes such as hg19 from UCSC or b37 from NCBI. 
Features can only be associated with one reference.

### Response

<%= headers 200 %>
<%= json({"identifier" => "GPS"}) %>

## Create a new reference set

    POST /referenceSet
    
This resource lets you create a new reference set and include information about
the source organism etc.

### Input

You can post a single study descriptor:

<%= json({"identifier" => "GPS"}) %>

### Response

<%= headers 201 %>
<%= json({"identifier" => "GPS", "dateCreated" => "2011-01-26T19:06:43Z"}) %>

## Modify an existing reference set

    PUT /referenceSet/:referenceSet
    
This resource lets you update an existing reference set.

### Input

You can specify study values:

<%= json({"identifier" => "NEW"}) %>

### Response

<%= headers 200 %>
<%= json({"identifier" => "NEW", "dateCreated" => "2011-01-26T19:06:43Z"}) %>

## Delete an existing reference set

    DELETE /referenceSet/:referenceSet
    
This resource lets you delete an existing reference set.
Deleting this reference set will cause any associate reference, 
feature sets and features to also be deleted.

### Response

<%= headers 200 %>
<%= json({"identifier" => "NEW", "dateCreated" => "2011-01-26T19:06:43Z"}) %>

