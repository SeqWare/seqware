The SeqWare Query Language
==========================

The SeqWare Query Language provides a human interface to the SeqWare Query Engine, where it simplifies the creation of query constraints in Reverse Polish Notation (RPN) by allowing query constraints to be formulated in a Java-like boolean expression syntax. For example, `seqid == "chr16" && strand == NEGATIVE_STRAND` constrains query results to only return features on chromosome 16 that are located on its negative (reverse) strand.[^spaces]

Syntax and Semantics
--------------------

Query strings are built using one or more propositional statements that can be combined using Boolean operators. Propositional statements determine truth values on a per-feature basis, which are then amalgamated by the Boolean operations to determine the truth value of the complete query string.

### Propositional Statements

Propositional statements are indivisable parts of a query whose truth value can only be `true` or `false`. A propositional statement is either of the form "_identifier_ _comparison_ _constant_" (or the other way around) or it is a predicate.

Statements of the form "_identifier_ _comparison_ _constant_" are comparing the value of a named feature property to the given constant. For example, `seqid == "chr16"` compares the features `seqid` property, which denotes a features broader location such as the chromosome, to the given string `chr16` that needs to be equal (`==`) in order for the comparison to evaluate to `true`. The function tagValue can also be used to retrieve the values of tags given a tagset row key. Other comparison operators are `!=` (not equals), `<` (less than), `<=` (less or equal than), `>` (greater than), `>=` (greater or equal than), where the last four operators can only applied to numeric feature properties. Constants can be either a string (in double quotes), an integer (digits only), a rational number (digits separated by a single dot), or a named constant. Currently supported named constants are `STRAND_UNKNOWN` (denoting that the feature's genomic strand is undetermined), `NOT_STRANDED` (strand is not applicable), `NEGATIVE_STRAND` (negative/reverse strand), `POSITIVE_STRAND` (positive/forward strand).

Predicates are truth functions, i.e. they only return true or false. It is possible to parametrize predicates using additional parameters, for example, `tagOccurrence("tagset" , "tag key")` determines whether a feature is tagged with a tag key "tag key" from "tagset". Predicates are stateless, so that it is guaranteed that the truth value is consistent and independent of when it is evaluated in a query.

#### Summary

Feature property identifiers:

*  named identifier (sequence of letters, digits, underlines)
*  `tagValue(tagset , tagkey)` (retrieve the value of 'tagkey' from the tagset with the key 'tagset')

Comparators:

*  `==` (equals)
*  `!=` (not equals)
*  `<` (less than)
*  `<=` (less or equal than)
*  `>` (greater than)
*  `>=` (greater or equal than)

Constants:

*  `"chr16"` (string)
*  `123` (integer)
*  `12.3` (rational number)
*  `STRAND_UNKNOWN` (named constant)

Predicates:

*  `tagOccurrence(tagset, tagkey)` (select features that are tagged with `tagkey`)
*  `tagHierarchicalOccurrence(tagset, tagkey)` (select features that are tagged with `tagkey`, or whose tag's parent is `tagkey`; _not translated into RPNStack parameter yet_)
*  `tagValuePresence(tagset, tagkey)` (select features that have a certain tag/value pair combination)

### Boolean Operators

There are two boolean operators for combining propositional statements to form complex queries: `&&` (and) as well as `||` (or).

Negation, `!` (not) is also available.

The order of operations can be changed by using brackets, `(` and `)`, which ensure that the content inside the brackets is evaluated independently.

#### Summary

Boolean operators in order of precedence:

*  `&&` (logical "and")
*  `||` (logical "or")

Adjusting the order of evaluation:

*  `(` and `)` can be used to ensure the contents inside the brackets are evaluated on their own, before the result is then used as an argument to a Boolean operator

Lexer and Parser Generation
---------------------------

The SeqWare Query Language is written as an ANTLR 3 Grammar using [ANTLRWorks 1.4.3](http://www.antlr.org), [@Bovet2008; @Parr2011]. ANTLRWorks compiles the grammar into a Java lexer and parser pair as follows:

1.  menu 'File' - 'Open...' - select `SeqWareQueryLanguage.g`
2.  menu 'Generate' - 'Generate Code'
3.  on the command line, copy the files
    -  `seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/output/*.java`
       to
    - `seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel`

It would be possible to omit the last step by putting the generated files into the appropriate Java package using:

    @header {
      package com.github.seqware.queryengine.kernel.output;
    }

However, the manual task of copying the generated files makes it nearly impossible that the lexer and parser are accidentally replaced when working with ANTLRWorks, which does not warn you when overwriting these files.

<!-- 
[^spaces]: Whitespace is not part of the query languge per se, i.e., the actual example query would be correctly formatted as `seqid=="chr16"&&strand==NEGATIVE_STRAND`. Since the removal of whitespace can be carried out on the interface level, for example as part of a RESTful query interface, I make use of whitespace in this documentation to increase readability. 
-->

# References

