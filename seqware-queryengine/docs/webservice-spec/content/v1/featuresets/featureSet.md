---
title: Feature Sets | Generic Feature Store API
---

# Feature Sets API

Feature sets are collections of features (variants) that have been loaded from files (GVF, VCF) or have been created via analysis events such as queries. 

Things to keep in mind:

* Unlike other sets, in the back-end, the feature set is implemented as a "light" object that is only referenced by individual features

In the document below the examples are loosly progressive, so things like versions increment in the document.

## Authentication

Management of feature sets via the API requires that you are
authenticated.

## List all feature sets

    GET /featureSets
    
Gets a list of feature sets.

## Create a new feature set

    POST /featureSet?format=:fileFormat&ad_hoc_tagset=:rowkey1&tagset=:rowkey2&batch=10000&reference=rowkey3
    
This resource lets you create a new feature set, attach the tags in it to a given tagset, populate an ad hoc tagset if necessary, specify a file format, and a batch size. 

### Input

You can post a VCF, GVF, or other file formats that are supported.  You will need to apply tags and other information using those seperate resources, this just creates the core object.

<blockquote>
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
21	26960070	rs116645811	G	A	.	.	Ensembl::Details=A|ENSG00000260583|ENST00000567517|Transcript|5KB_upstream_variant||||||rs116645811||,A|ENSG00000154719|ENST00000352957|Transcript|intron_variant||||||rs116645811||,A|ENSG00000154719|ENST00000307301|Transcript|non_synonymous_codon|1043|1001|334|T/M|aCg/aTg|rs116645811|0.001|0.05;SO:0001023::allele=A;SO:0000704::gene=ENSG00000260583,ENSG00000154719;SO:0000110::sequence_feature=ENST00000567517,ENST00000352957,ENST00000307301;Ensembl::FeatureType=Transcript;5KB_upstream_variant=SO:0001635;SO:0001635=5KB_upstream_variant;intron_variant=SO:0001627;SO:0001627=intron_variant;non_synonymous_codon=SO:0001818;SO:0001818=non_synonymous_codon;Ensembl::cDNARelativePosition=1043;Ensembl::CDSRelativePosition=1001;Ensembl::ProteinRelativePosition=334;SO:0001385::modified_amino_acid_feature=T/M;Ensembl::AlternateCodons=aCg/aTg;Ensembl::ExistingVariation=rs116645811;Ensembl::PolyPhen=0.001;Ensembl::SIFT=0.05
21	26965148	rs1135638	G	A	.	.	Ensembl::Details=A|ENSG00000154719|ENST00000419219|Transcript|synonymous_codon|876|867|289|G|ggC/ggT|rs1135638||,A|ENSG00000154719|ENST00000352957|Transcript|synonymous_codon|939|897|299|G|ggC/ggT|rs1135638||,A|ENSG00000154719|ENST00000307301|Transcript|synonymous_codon|939|897|299|G|ggC/ggT|rs1135638||;SO:0001023::allele=A;SO:0000704::gene=ENSG00000154719;SO:0000110::sequence_feature=ENST00000419219,ENST00000352957,ENST00000307301;Ensembl::FeatureType=Transcript;synonymous_codon=SO:0001819;SO:0001819=synonymous_codon;Ensembl::cDNARelativePosition=876,939;Ensembl::CDSRelativePosition=867,897;Ensembl::ProteinRelativePosition=289,299;SO:0001385::modified_amino_acid_feature=G;Ensembl::AlternateCodons=ggC/ggT;Ensembl::ExistingVariation=rs1135638;;
21	26965172	rs10576	T	C	.	.	Ensembl::Details=C|ENSG00000154719|ENST00000419219|Transcript|synonymous_codon|852|843|281|P|ccA/ccG|rs10576||,C|ENSG00000154719|ENST00000352957|Transcript|synonymous_codon|915|873|291|P|ccA/ccG|rs10576||,C|ENSG00000154719|ENST00000307301|Transcript|synonymous_codon|915|873|291|P|ccA/ccG|rs10576||;SO:0001023::allele=C;SO:0000704::gene=ENSG00000154719;SO:0000110::sequence_feature=ENST00000419219,ENST00000352957,ENST00000307301;Ensembl::FeatureType=Transcript;synonymous_codon=SO:0001819;SO:0001819=synonymous_codon;Ensembl::cDNARelativePosition=852,915;Ensembl::CDSRelativePosition=843,873;Ensembl::ProteinRelativePosition=281,291;SO:0001385::modified_amino_acid_feature=P;Ensembl::AlternateCodons=ccA/ccG;Ensembl::ExistingVariation=rs10576;;
21	26965205	rs1057885	T	C	.	.	Ensembl::Details=C|ENSG00000154719|ENST00000419219|Transcript|synonymous_codon|819|810|270|V|gtA/gtG|rs1057885||,C|ENSG00000154719|ENST00000352957|Transcript|synonymous_codon|882|840|280|V|gtA/gtG|rs1057885||,C|ENSG00000154719|ENST00000307301|Transcript|synonymous_codon|882|840|280|V|gtA/gtG|rs1057885||;SO:0001023::allele=C;SO:0000704::gene=ENSG00000154719;SO:0000110::sequence_feature=ENST00000419219,ENST00000352957,ENST00000307301;Ensembl::FeatureType=Transcript;synonymous_codon=SO:0001819;SO:0001819=synonymous_codon;Ensembl::cDNARelativePosition=819,882;Ensembl::CDSRelativePosition=810,840;Ensembl::ProteinRelativePosition=270,280;SO:0001385::modified_amino_acid_feature=V;Ensembl::AlternateCodons=gtA/gtG;Ensembl::ExistingVariation=rs1057885;;
</blockquote>

### Response

You just get the freshly created resource back. Notice the collections inside it are empty.

<%= headers 201 %>
<%= json({
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" => "http://server/gfs/0.1/referenceSets/1/version/1",
  "description" => "This feature set describes the mutations that create a bat man",
  "tags" => [

  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [

  ],
  "reference" => "hg_19",
  "create_tstmp" => "2012-05-22 23:45:00",
  "ttl" => "FOREVER",
  "updates" => [

  ]
}) %>


**Content below is place-holder content**
---------------------------------------


### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/3",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## List a Single Reference Set

    GET /referenceSets/:id
    
Gets a single references set record.

### Response

<%= headers 200 %>
<%= json({
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/3",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
) %>

## Create a new reference set

    POST /referenceSet
    
This resource lets you create a new reference set and include information about
the source organism etc. The default TTL for referenceSets is "FOREVER". 

### Input

You can post a resource set descriptor.  You will need to apply tags and other information using those seperate resources, this just creates the core object.

<%= json({
   "name" => "Human", "organism" => "Homo Sapiens Typo"   
}) %>

### Response

You just get the freshly created resource back. Notice the collections inside it are empty.

<%= headers 201 %>
<%= json({
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/1",
  "name" => "Human",
  "organism" => "Homo Sapiens Typo",
  "tags" => [],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [],
  "references" => [],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => []
}) %>

## Modify an existing reference set

    PUT /referenceSet/:id
    
This resource lets you update an existing reference set.

### Input

TODO: there are several other fields we want to update. For example TTL.

You can specify reference set values, the collections are accessed with different resources:

<%= json({"name" => "Human", "organism" => "Homo Sapiens"}) %>

### Response

<%= headers 200 %>
<%= json ({
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/1",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [],
  "references" => [],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => []
})%>

## Delete an existing reference set

    DELETE /referenceSet/:id
    
This resource lets you delete an existing reference set.
Deleting this reference set will cause any associate reference, 
feature sets and features to also be deleted. This is very destructive!

### Response

<%= headers 204 %>

## Search by Tags

    GET /referenceSets/tags?key=released
    
Gets a list of references sets that have a tag called "released" in this case regardless of value.

QUESTION: do we alow searching on the value too?

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/3",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Search for Tags

    GET /referenceSets/:id/tags
    
Gets a list of tags for a particular references set.

### Response

<%= headers 200 %>
<%= json([
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
]) %>

## Adding a Tag to the Reference Set

    PUT /referenceSets/:id/tag/:tag_id
    
Associate a given tag (subject) to a referenceSets, this will have a null value
(object) and null predicate.  The response includes the full referenceSet
object, in this case the result is show when tag ID 92019 is used in the
request. Each tag has a unique ID and this can be found by doing queries on the
/tagSets resource.  Notice this process causes a new version of the object to
be created.

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/4",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" },
     { "id" => "92019", "key" => "in_production", "predicate" => "equals", "value" => "true" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Adding a Tag and Value to the Reference Set

    PUT /referenceSets/:id/tag/:tag_id/:value
    
Associate a given tag (subject) to a referenceSets, this will have the value (object) specified and a default predicate of "equals".
The response includes the full referenceSet object, in this case the result is show when tag ID 92019 with value "correct" is used
in the request.

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/4",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" },
     { "id" => "92019", "key" => "in_production", "predicate" => "equals", "value" => "correct" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Adding a Tag, Value and Predicate to the Reference Set

    PUT /referenceSets/:id/tag/:tag_id/:value/:predicate
    
Associate a given tag (subject) to a referenceSet, this will have the value
(object) and predicate specified in the request.  The response includes the
full referenceSet object, in this case the result is show when tag ID 92019
with value "2012-03-27 22:48:00" and predicate "since" is used.

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/4",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" },
     { "id" => "92019", "key" => "in_production", "predicate" => "since", "value" => "2012-03-27 22:48:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-22 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Delete a Tag from a ReferenceSet

    DELETE /referenceSet/:id/tag/:tag_id
    
This resource lets you delete a tag from a reference set.  Notice that a new
version is created by this request and the tag (ID 92019) has been removed in
this example.

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/5",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-27 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/4", "update_tstmp" => "2012-05-22 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Get Permissions

    GET /referenceSets/:id/permissions
    
Gets a list of ACL permissions for this referenceSet. Note that permissions apply to this and all previous versions of the object. So if you tighten access previous versions will also be protected by this ACL change.

TODO: maybe we do not need this since this data can be found in main GET

### Response

<%= headers 200 %>
<%= json([
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST", "DELETE"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => ["GET", "POST"] }
]) %>

## Update Permissions

    PUT /referenceSets/:id/permissions
    
Updates the premissions on this object by providing a list of ACL permissions. Note that permissions apply to this and all previous versions of the object. So if you tighten access previous versions will also be protected by this ACL change. The URL is used to identify the entity you are granting or removing permissions for.  If a previous URL is not mentioned in the PUT nothing will be changed.  To delete permissions completely for an entry you pass an empty array as in the second entry below.  Otherwise the permissions field controls whether you are adding or removing permissions.

### Response

<%= headers 200 %>
<%= json([
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST"] },
    { "entity_type" => "user", "url" => "http://server/gfs/0.1/users/56", "permissions" => [] }
]) %>

Now doing a GET request we see that the version of the object has incremented (and you could see previous permissions by issuing a GET on a previous version). But the current permissions now control access to the current and all previous versions of this object.

    GET /referenceSets/:id
    
### Response

<%= headers 200 %>
<%= json({
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/6",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "id" => "1232", "key" => "released", "predicate" => "equals", "value" => "true" },
     { "id" => "29318", "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
  ],
  "owner" => "http://server/gfs/0.1/users/23",
  "acl" => [
    { "entity_type" => "group", "url" => "http://server/gfs/0.1/groups/14", "permissions" => ["GET", "PUT", "POST"] },
  ],
  "references" => [
    "http://server/gfs/0.1/referenceSets/1/references/345", "http://server/gfs/0.1/referenceSets/1/references/829", "http://server/gfs/0.1/referenceSets/1/references/573"
  ],
  "create_tstmp" => '2012-05-28 23:45:00',
  "ttl" => 'FOREVER',
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/5", "update_tstmp" => "2012-05-27 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/4", "update_tstmp" => "2012-05-22 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
) %>

## Get Versions

    GET /referenceSets/:id/versions
    
Gets a list of versions for this object. This returns all versions including those in the "updates" section of the doc along with the current version.

TODO: maybe we do not need this since this data can be found in main GET

### Response

<%= headers 200 %>
<%= json([
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/6", "update_tstmp" => "2012-05-28 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/5", "update_tstmp" => "2012-05-27 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/4", "update_tstmp" => "2012-05-22 23:45:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/3", "update_tstmp" => "2012-05-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }

]) %>


