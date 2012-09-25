// $ANTLR 3.4 /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g 2012-09-25 16:15:17

package com.github.seqware.queryengine.kernel;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class SeqWareQueryLanguageParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "BRACKET_CLOSE", "BRACKET_OPEN", "COMMENT_EOL", "COMMENT_INLINE", "EQUALS", "FLOAT", "GT", "GTEQ", "ID", "INT", "LT", "LTEQ", "NAMED_CONSTANT", "NAMED_FUNCTION", "NOTEQUALS", "OR", "STRING"
    };

    public static final int EOF=-1;
    public static final int AND=4;
    public static final int BRACKET_CLOSE=5;
    public static final int BRACKET_OPEN=6;
    public static final int COMMENT_EOL=7;
    public static final int COMMENT_INLINE=8;
    public static final int EQUALS=9;
    public static final int FLOAT=10;
    public static final int GT=11;
    public static final int GTEQ=12;
    public static final int ID=13;
    public static final int INT=14;
    public static final int LT=15;
    public static final int LTEQ=16;
    public static final int NAMED_CONSTANT=17;
    public static final int NAMED_FUNCTION=18;
    public static final int NOTEQUALS=19;
    public static final int OR=20;
    public static final int STRING=21;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public SeqWareQueryLanguageParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public SeqWareQueryLanguageParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return SeqWareQueryLanguageParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g"; }


    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "query"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:20:1: query : low_precedence_constraint ^;
    public final SeqWareQueryLanguageParser.query_return query() throws RecognitionException {
        SeqWareQueryLanguageParser.query_return retval = new SeqWareQueryLanguageParser.query_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint1 =null;



        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:2: ( low_precedence_constraint ^)
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:4: low_precedence_constraint ^
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_low_precedence_constraint_in_query75);
            low_precedence_constraint1=low_precedence_constraint();

            state._fsp--;

            root_0 = (CommonTree)adaptor.becomeRoot(low_precedence_constraint1.getTree(), root_0);

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "query"


    public static class low_precedence_constraint_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "low_precedence_constraint"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:24:1: low_precedence_constraint : ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )* ;
    public final SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.low_precedence_constraint_return retval = new SeqWareQueryLanguageParser.low_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OR3=null;
        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint2 =null;

        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint4 =null;


        CommonTree OR3_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:2: ( ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )* )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:5: high_precedence_constraint
            {
            pushFollow(FOLLOW_high_precedence_constraint_in_low_precedence_constraint88);
            high_precedence_constraint2=high_precedence_constraint();

            state._fsp--;

            adaptor.addChild(root_0, high_precedence_constraint2.getTree());

            }


            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:33: ( OR ^ high_precedence_constraint )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:34: OR ^ high_precedence_constraint
            	    {
            	    OR3=(Token)match(input,OR,FOLLOW_OR_in_low_precedence_constraint92); 
            	    OR3_tree = 
            	    (CommonTree)adaptor.create(OR3)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(OR3_tree, root_0);


            	    pushFollow(FOLLOW_high_precedence_constraint_in_low_precedence_constraint95);
            	    high_precedence_constraint4=high_precedence_constraint();

            	    state._fsp--;

            	    adaptor.addChild(root_0, high_precedence_constraint4.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "low_precedence_constraint"


    public static class high_precedence_constraint_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "high_precedence_constraint"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:28:1: high_precedence_constraint : ( nested_constraint ) ( AND ^ nested_constraint )* ;
    public final SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.high_precedence_constraint_return retval = new SeqWareQueryLanguageParser.high_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AND6=null;
        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint5 =null;

        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint7 =null;


        CommonTree AND6_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:2: ( ( nested_constraint ) ( AND ^ nested_constraint )* )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint ) ( AND ^ nested_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:5: nested_constraint
            {
            pushFollow(FOLLOW_nested_constraint_in_high_precedence_constraint110);
            nested_constraint5=nested_constraint();

            state._fsp--;

            adaptor.addChild(root_0, nested_constraint5.getTree());

            }


            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:24: ( AND ^ nested_constraint )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:25: AND ^ nested_constraint
            	    {
            	    AND6=(Token)match(input,AND,FOLLOW_AND_in_high_precedence_constraint114); 
            	    AND6_tree = 
            	    (CommonTree)adaptor.create(AND6)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(AND6_tree, root_0);


            	    pushFollow(FOLLOW_nested_constraint_in_high_precedence_constraint117);
            	    nested_constraint7=nested_constraint();

            	    state._fsp--;

            	    adaptor.addChild(root_0, nested_constraint7.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "high_precedence_constraint"


    public static class nested_constraint_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "nested_constraint"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:32:1: nested_constraint : ( BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint | comment !);
    public final SeqWareQueryLanguageParser.nested_constraint_return nested_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.nested_constraint_return retval = new SeqWareQueryLanguageParser.nested_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token BRACKET_OPEN8=null;
        Token BRACKET_CLOSE10=null;
        SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint9 =null;

        SeqWareQueryLanguageParser.constraint_return constraint11 =null;

        SeqWareQueryLanguageParser.comment_return comment12 =null;


        CommonTree BRACKET_OPEN8_tree=null;
        CommonTree BRACKET_CLOSE10_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:2: ( BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint | comment !)
            int alt3=3;
            switch ( input.LA(1) ) {
            case BRACKET_OPEN:
                {
                alt3=1;
                }
                break;
            case FLOAT:
            case ID:
            case INT:
            case NAMED_CONSTANT:
            case NAMED_FUNCTION:
            case STRING:
                {
                alt3=2;
                }
                break;
            case COMMENT_EOL:
            case COMMENT_INLINE:
                {
                alt3=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:4: BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    BRACKET_OPEN8=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_nested_constraint131); 

                    pushFollow(FOLLOW_low_precedence_constraint_in_nested_constraint134);
                    low_precedence_constraint9=low_precedence_constraint();

                    state._fsp--;

                    adaptor.addChild(root_0, low_precedence_constraint9.getTree());

                    BRACKET_CLOSE10=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_nested_constraint136); 

                    }
                    break;
                case 2 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:34:4: constraint
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constraint_in_nested_constraint142);
                    constraint11=constraint();

                    state._fsp--;

                    adaptor.addChild(root_0, constraint11.getTree());

                    }
                    break;
                case 3 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:35:4: comment !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_comment_in_nested_constraint147);
                    comment12=comment();

                    state._fsp--;


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "nested_constraint"


    public static class constraint_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constraint"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:38:1: constraint : ( identifier comparison ^ constant | constant comparison ^ identifier | function );
    public final SeqWareQueryLanguageParser.constraint_return constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.constraint_return retval = new SeqWareQueryLanguageParser.constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        SeqWareQueryLanguageParser.identifier_return identifier13 =null;

        SeqWareQueryLanguageParser.comparison_return comparison14 =null;

        SeqWareQueryLanguageParser.constant_return constant15 =null;

        SeqWareQueryLanguageParser.constant_return constant16 =null;

        SeqWareQueryLanguageParser.comparison_return comparison17 =null;

        SeqWareQueryLanguageParser.identifier_return identifier18 =null;

        SeqWareQueryLanguageParser.function_return function19 =null;



        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:2: ( identifier comparison ^ constant | constant comparison ^ identifier | function )
            int alt4=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt4=1;
                }
                break;
            case FLOAT:
            case INT:
            case NAMED_CONSTANT:
            case STRING:
                {
                alt4=2;
                }
                break;
            case NAMED_FUNCTION:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:4: identifier comparison ^ constant
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_identifier_in_constraint159);
                    identifier13=identifier();

                    state._fsp--;

                    adaptor.addChild(root_0, identifier13.getTree());

                    pushFollow(FOLLOW_comparison_in_constraint161);
                    comparison14=comparison();

                    state._fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(comparison14.getTree(), root_0);

                    pushFollow(FOLLOW_constant_in_constraint164);
                    constant15=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant15.getTree());

                    }
                    break;
                case 2 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:40:4: constant comparison ^ identifier
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constant_in_constraint169);
                    constant16=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant16.getTree());

                    pushFollow(FOLLOW_comparison_in_constraint171);
                    comparison17=comparison();

                    state._fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(comparison17.getTree(), root_0);

                    pushFollow(FOLLOW_identifier_in_constraint174);
                    identifier18=identifier();

                    state._fsp--;

                    adaptor.addChild(root_0, identifier18.getTree());

                    }
                    break;
                case 3 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:41:4: function
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_function_in_constraint179);
                    function19=function();

                    state._fsp--;

                    adaptor.addChild(root_0, function19.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "constraint"


    public static class identifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "identifier"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:44:1: identifier : ID ;
    public final SeqWareQueryLanguageParser.identifier_return identifier() throws RecognitionException {
        SeqWareQueryLanguageParser.identifier_return retval = new SeqWareQueryLanguageParser.identifier_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID20=null;

        CommonTree ID20_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:45:2: ( ID )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:45:4: ID
            {
            root_0 = (CommonTree)adaptor.nil();


            ID20=(Token)match(input,ID,FOLLOW_ID_in_identifier190); 
            ID20_tree = 
            (CommonTree)adaptor.create(ID20)
            ;
            adaptor.addChild(root_0, ID20_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "identifier"


    public static class comparison_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comparison"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:48:1: comparison : ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ );
    public final SeqWareQueryLanguageParser.comparison_return comparison() throws RecognitionException {
        SeqWareQueryLanguageParser.comparison_return retval = new SeqWareQueryLanguageParser.comparison_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set21=null;

        CommonTree set21_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:49:2: ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set21=(Token)input.LT(1);

            if ( input.LA(1)==EQUALS||(input.LA(1) >= GT && input.LA(1) <= GTEQ)||(input.LA(1) >= LT && input.LA(1) <= LTEQ)||input.LA(1)==NOTEQUALS ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set21)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "comparison"


    public static class constant_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constant"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:52:1: constant : ( literal | NAMED_CONSTANT );
    public final SeqWareQueryLanguageParser.constant_return constant() throws RecognitionException {
        SeqWareQueryLanguageParser.constant_return retval = new SeqWareQueryLanguageParser.constant_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_CONSTANT23=null;
        SeqWareQueryLanguageParser.literal_return literal22 =null;


        CommonTree NAMED_CONSTANT23_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:53:2: ( literal | NAMED_CONSTANT )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==FLOAT||LA5_0==INT||LA5_0==STRING) ) {
                alt5=1;
            }
            else if ( (LA5_0==NAMED_CONSTANT) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:53:4: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_literal_in_constant233);
                    literal22=literal();

                    state._fsp--;

                    adaptor.addChild(root_0, literal22.getTree());

                    }
                    break;
                case 2 :
                    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:54:4: NAMED_CONSTANT
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NAMED_CONSTANT23=(Token)match(input,NAMED_CONSTANT,FOLLOW_NAMED_CONSTANT_in_constant238); 
                    NAMED_CONSTANT23_tree = 
                    (CommonTree)adaptor.create(NAMED_CONSTANT23)
                    ;
                    adaptor.addChild(root_0, NAMED_CONSTANT23_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "constant"


    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:57:1: literal : ( INT | FLOAT | STRING );
    public final SeqWareQueryLanguageParser.literal_return literal() throws RecognitionException {
        SeqWareQueryLanguageParser.literal_return retval = new SeqWareQueryLanguageParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set24=null;

        CommonTree set24_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:58:2: ( INT | FLOAT | STRING )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set24=(Token)input.LT(1);

            if ( input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==STRING ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set24)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal"


    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "function"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:61:1: function : NAMED_FUNCTION ^ BRACKET_OPEN ! literal BRACKET_CLOSE !;
    public final SeqWareQueryLanguageParser.function_return function() throws RecognitionException {
        SeqWareQueryLanguageParser.function_return retval = new SeqWareQueryLanguageParser.function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_FUNCTION25=null;
        Token BRACKET_OPEN26=null;
        Token BRACKET_CLOSE28=null;
        SeqWareQueryLanguageParser.literal_return literal27 =null;


        CommonTree NAMED_FUNCTION25_tree=null;
        CommonTree BRACKET_OPEN26_tree=null;
        CommonTree BRACKET_CLOSE28_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:62:2: ( NAMED_FUNCTION ^ BRACKET_OPEN ! literal BRACKET_CLOSE !)
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:62:4: NAMED_FUNCTION ^ BRACKET_OPEN ! literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_FUNCTION25=(Token)match(input,NAMED_FUNCTION,FOLLOW_NAMED_FUNCTION_in_function269); 
            NAMED_FUNCTION25_tree = 
            (CommonTree)adaptor.create(NAMED_FUNCTION25)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_FUNCTION25_tree, root_0);


            BRACKET_OPEN26=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_function272); 

            pushFollow(FOLLOW_literal_in_function275);
            literal27=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal27.getTree());

            BRACKET_CLOSE28=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_function277); 

            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "function"


    public static class comment_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comment"
    // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:65:1: comment : ( COMMENT_EOL | COMMENT_INLINE );
    public final SeqWareQueryLanguageParser.comment_return comment() throws RecognitionException {
        SeqWareQueryLanguageParser.comment_return retval = new SeqWareQueryLanguageParser.comment_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set29=null;

        CommonTree set29_tree=null;

        try {
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:66:2: ( COMMENT_EOL | COMMENT_INLINE )
            // /Users/jbaran/src/seqware/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set29=(Token)input.LT(1);

            if ( (input.LA(1) >= COMMENT_EOL && input.LA(1) <= COMMENT_INLINE) ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set29)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "comment"

    // Delegated rules


 

    public static final BitSet FOLLOW_low_precedence_constraint_in_query75 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint88 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_OR_in_low_precedence_constraint92 = new BitSet(new long[]{0x00000000002665C0L});
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint95 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint110 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AND_in_high_precedence_constraint114 = new BitSet(new long[]{0x00000000002665C0L});
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint117 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_BRACKET_OPEN_in_nested_constraint131 = new BitSet(new long[]{0x00000000002665C0L});
    public static final BitSet FOLLOW_low_precedence_constraint_in_nested_constraint134 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_nested_constraint136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_nested_constraint142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comment_in_nested_constraint147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_constraint159 = new BitSet(new long[]{0x0000000000099A00L});
    public static final BitSet FOLLOW_comparison_in_constraint161 = new BitSet(new long[]{0x0000000000224400L});
    public static final BitSet FOLLOW_constant_in_constraint164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_constraint169 = new BitSet(new long[]{0x0000000000099A00L});
    public static final BitSet FOLLOW_comparison_in_constraint171 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_identifier_in_constraint174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_constraint179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_CONSTANT_in_constant238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_FUNCTION_in_function269 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_BRACKET_OPEN_in_function272 = new BitSet(new long[]{0x0000000000204400L});
    public static final BitSet FOLLOW_literal_in_function275 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_function277 = new BitSet(new long[]{0x0000000000000002L});

}