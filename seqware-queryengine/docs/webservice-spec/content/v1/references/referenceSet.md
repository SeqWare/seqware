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

Most responses (unless noted) should have the following fields or nested data structures:

* id: a numeric identifier unique across the whole backend and not just this particular resource... use a UUID library
* URL: a resolvable URL for this document, can be used as an alternative identifier
* tags: a nested structure that lists out all the tags this entity has associated
* owner: the core owner, almost every resource has a direct owner. While other groups and users may have permissions this owner is the person that created this resource. An owner must be a user and not a group
* acl: ACL-style documents manage the permissions non-owners have on this resource, this includes both individual users and groups
* create_tstmp: a creation time for this resource, when a new version is created (through a edits) this create_tstmp applies to the version. Previous versions will indicate their create_tstmp as the update_tstmp field.  To find the original creation time look at the oldest version for its update_tstmp.
* updates: an array describing the version history for this resource and URLs to retrieve particular previous versions. Most resources will track their versions however very large resources like feature sets and features will use a copy on write approach instead and the analysis set and analysis resources to track versions. Since all resources but features are "cheap" to store we can afford to store all previous versions of them (some backend types give this functionality for free such as HBase).
* version: a URL for this resource that includes the version string. For the most recent version of a resource this is just an alternative URL for accessing it.
* the owner has full control (can do a GET, POST, PUT, DELETE and possibly others if the resource supports it) while permissions for other users and groups is controlled via the ACL.  There is one group, though, that is reserved in this system and that is the "admin" group.  If a user is added to this group they can manipulate any resource using any method it supports.
* most entities in the DB have a TTL that, by default for most objects, is set to FOREVER otherwise the lifetime in seconds. An object may be around that longer than this value but it is guarenteed to not be deleted until after the TTL has expired. The TTL is handy for "expensive" objects like features and feature sets that may have many copies made and we want to delete these as we go to recover extra space.
* deletes of reference sets and other set objects in the database, can we just mark these as delete rather than actually delete?  With the exception of maybe features which we would really like to delete to save space?  Really, it would be best to make this configurable.  So if the DB is being used for clinical then it would not allow for deletions or TTL deletions at all. Things would only be marked as delete and TTL would always be FOREVER. In another DB that is used for research maybe allows for deletes of all objects and a delete of a reference set or reference would delete all features.  I think we need this flexibility since some applications really need a paper trail whereas others do not.

In the document below the examples are loosly progressive, so things like versions increment in the document.

## Authentication

Management of reference sets via the API requires that you are
authenticated.

## List all reference sets

    GET /referenceSets
    
Gets a list of references sets, these are typically different species like mouse and human that particular references (builds of a genome) can be associated with.

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


