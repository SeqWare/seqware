grammar SeqWareQueryLanguage;

options{
    output=AST;
    language=Java;
    ASTLabelType=CommonTree;
}

@header {
package com.github.seqware.queryengine.kernel;
}

@lexer::header {
package com.github.seqware.queryengine.kernel;
}

@lexer::namespace { SeqWareQueryLanguage }
@parser::namespace { SeqWareQueryLanguage }

query
	:	low_precedence_constraint^
	;

low_precedence_constraint
	:	(high_precedence_constraint) (OR^ high_precedence_constraint)*
	;
	
high_precedence_constraint
	:	(nested_constraint) (AND^ nested_constraint)*
	;
	
nested_constraint
	:	BRACKET_OPEN! low_precedence_constraint BRACKET_CLOSE!
	|	constraint
	|	comment!
	|	NOT^ nested_constraint
	;

constraint
	:	identifier comparison^ constant
	|	constant comparison^ identifier
	|	two_param_function
	| 	three_param_function
//	| 	NOT^ constraint
	;

identifier
	:	ID
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

two_param_function
	:	NAMED_TWO_PARAM_FUNCTION^ BRACKET_OPEN! literal ',' literal BRACKET_CLOSE!
	;
	
three_param_function
	:	NAMED_THREE_PARAM_FUNCTION^ BRACKET_OPEN! literal ',' literal ',' literal BRACKET_CLOSE!
	;
	
comment
	:	COMMENT_EOL | COMMENT_INLINE
	;

NAMED_CONSTANT
	:	'STRAND_UNKNOWN' | 'NOT_STRANDED' | 'NEGATIVE_STRAND' | 'POSITIVE_STRAND'
	;
	
NAMED_TWO_PARAM_FUNCTION
	:	'tagOccurrence' | 'tagHierarchicalOccurrence'
	;
	
NAMED_THREE_PARAM_FUNCTION
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
	
