---
title: Reference Sets | Generic Feature Store API
---

# Reference Sets API

Reference sets are collections of references which are a collection of contigs and coordinates. Think of the reference set as "homo sapiens" and a particular reference as a build version like "hg19" or "hg18".

Things to keep in mind:

* A reference is associated with one reference set at a time.
* A feature set exist cannot exist without a reference.  If the reference/reference set is not a common one the user can just create them as needed in order to associate with a feature set.
* Feature set can only be associated with 1 reference at a time. Cannot be changed once associated (but in the future an analysis component could convert a feature set to a new feature set with transformed coordinates).

## General Thoughts

Move this to the top document:

* the API should be self-documenting, a user should be able to do a get and look at the response and know how to navigate to the next level.  The details of how to query may not be clear and require the developer to look at the API documentation but the core URL traversal process should be totally obvious looking at the documents returned.

* most responses (unless noted) should have the following fields or nested data structures:
** id: a numeric identifier unique across the whole backend and not just this particular resource... use a UUID library
** URL: a resolvable URL for this document, can be used as an alternative identifier
** tags: a nested structure that lists out all the tags this entity has associated
** owner: the core owner, almost every resource has a direct owner. While other groups and users may have permissions this owner is the person that created this resource. An owner must be a user and not a group
** acl: ACL-style documents manage the permissions non-owners have on this resource, this includes both individual users and groups
** create_tstmp: a creation time for this resource
** updates: an array describing the version history for this resource and URLs to retrieve particular previous versions. Most resources will track their versions however very large resources like feature sets and features will use a copy on write approach instead and the analysis set and analysis resources to track versions. Since all resources but features are "cheap" to store we can afford to store all previous versions of them (some backend types give this functionality for free such as HBase).
** version: a URL for this resource that includes the version string. For the most recent version of a resource this is just an alternative URL for accessing it.
** the owner has full control (can do a GET, POST, PUT, DELETE and possibly others if the resource supports it) while permissions for other users and groups is controlled via the ACL.  There is one group, though, that is reserved in this system and that is the "admin" group.  If a user is added to this group they can manipulate any resource using any method it supports.

## Authentication

Management of reference sets via the API requires that you are
authenticated.

## List all reference sets

    GET /referenceSets
    
Gets a list of references sets. These are typically builds of particular
genomes such as hg19 from UCSC or b37 from NCBI. 

### Response

<%= headers 200 %>
<%= json([{
  "id" => "1",
  "url" => "http://server/gfs/0.1/referenceSets/1",
  "version" =>  "http://server/gfs/0.1/referenceSets/1/version/3",
  "name" => "Human",
  "organism" => "Homo Sapiens",
  "tags" => [
     { "key" => "released", "predicate" => "equals", "value" => "true" },
     { "key" => "release_date", "predicate" => "equals", "value" => "2010-08-23 12:34:00" }
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
  "updates" => [
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/2", "update_tstmp" => "2011-08-05 12:34:00" },
     { "editor" => "http://server/gfs/0.1/users/23", "version" => "http://server/gfs/0.1/referenceSets/1/version/1", "update_tstmp" => "2011-08-05 12:34:00" }
  ]
 }
]) %>

## Create a new reference set

    POST /referenceSet
    
This resource lets you create a new reference set and include information about
the source organism etc.

### Input

You can post a resource set descriptor.  You will need to apply tags and other information using those seperate resources, this just creates the core object.

<%= json({
   "name" => "Human", "organism" => "Homo Sapiens"   
}) %>

### Response

You just get the freshly created resource back.

<%= headers 201 %>
<%= json({
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
  "updates" => []
}) %>

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


