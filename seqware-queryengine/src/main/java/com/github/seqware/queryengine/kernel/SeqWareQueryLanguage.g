grammar SeqWareQueryLanguage;

options{
    output=AST;
    language=Java;
    ASTLabelType=CommonTree;
}

@header {
package com.github.seqware.queryengine.kernel.output;
}

@lexer::header {
package com.github.seqware.queryengine.kernel.output;
}

@lexer::namespace { SeqWareQueryLanguage }
@parser::namespace { SeqWareQueryLanguage }

query
	:	low_precedence_constraint^ EOF!
	;

low_precedence_constraint
	:	(high_precedence_constraint) (OR^ high_precedence_constraint)*
	;
	
high_precedence_constraint
	:	(nested_constraint) (AND^ nested_constraint)*
	;
	
nested_constraint
	:	(NOT^)? BRACKET_OPEN! low_precedence_constraint BRACKET_CLOSE!
	|	constraint
//	|	comment!
	;

constraint
	:	identifier comparison^ constant
	|	constant comparison^ identifier
	|	two_param_predicate
	| 	three_param_predicate
	;

identifier
	:	ID
	| 	key_value_function
	;
	
comparison
	:	EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ
	;

constant
	:	literal
	|	NAMED_CONSTANT
	;
	
literal
	:	INT | FLOAT | STRING
	;

key_value_function
	:	NAMED_FUNCTION^ BRACKET_OPEN! literal ',' literal BRACKET_CLOSE!
	;

two_param_predicate
	:	NAMED_TWO_PARAM_PREDICATE^ BRACKET_OPEN! literal ',' literal BRACKET_CLOSE!
	;
	
three_param_predicate
	:	NAMED_THREE_PARAM_PREDICATE^ BRACKET_OPEN! literal ',' literal ',' literal BRACKET_CLOSE!
	;
	
comment
	:	COMMENT_EOL | COMMENT_INLINE
	;

NAMED_CONSTANT
	:	'STRAND_UNKNOWN' | 'NOT_STRANDED' | 'NEGATIVE_STRAND' | 'POSITIVE_STRAND'
	;
	
NAMED_FUNCTION
	:	'tagValue'
	;
	
NAMED_TWO_PARAM_PREDICATE
	:	'tagOccurrence' | 'tagHierarchicalOccurrence'
	;
	
NAMED_THREE_PARAM_PREDICATE
	:	'tagValuePresence'
	;
			
ID
	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;

INT
	:	'0'..'9'+
	;

FLOAT
    	:	('0'..'9')+ '.' ('0'..'9')*
    	|	'.' ('0'..'9')+
    	;

COMMENT_EOL
	:	'//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	;
	
COMMENT_INLINE
	:	'/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
	;

STRING
    :  '"' ( ~('\\'|'"') )* '"'
    ;

NOT
	:	'!'
	;

OR
	:	'||'
	;
	
AND
	:	'&&'
	;
	
EQUALS
	:	'=='
	;
	
NOTEQUALS
	:	'!='
	;
	
LT
	:	'<'
	;
	
LTEQ
	:	'<='
	;
	
GT
	:	'>'
	;
	
GTEQ
	:	'>='
	;
	
BRACKET_OPEN
	:	'('
	;
	
BRACKET_CLOSE
	:	')'
	;
	
WHITESPACE 
	: ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+  
	{ $channel=HIDDEN;} ;
	
