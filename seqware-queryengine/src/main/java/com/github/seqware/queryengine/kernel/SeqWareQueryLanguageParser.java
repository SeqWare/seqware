// $ANTLR 3.4 /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g 2012-10-01 16:06:16

package com.github.seqware.queryengine.kernel;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class SeqWareQueryLanguageParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "BRACKET_CLOSE", "BRACKET_OPEN", "COMMENT_EOL", "COMMENT_INLINE", "EQUALS", "FLOAT", "GT", "GTEQ", "ID", "INT", "LT", "LTEQ", "NAMED_CONSTANT", "NAMED_THREE_PARAM_FUNCTION", "NAMED_TWO_PARAM_FUNCTION", "NOTEQUALS", "OR", "STRING", "','"
    };

    public static final int EOF=-1;
    public static final int T__23=23;
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
    public static final int NAMED_THREE_PARAM_FUNCTION=18;
    public static final int NAMED_TWO_PARAM_FUNCTION=19;
    public static final int NOTEQUALS=20;
    public static final int OR=21;
    public static final int STRING=22;

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
    public String getGrammarFileName() { return "/home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g"; }


    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "query"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:20:1: query : low_precedence_constraint ^;
    public final SeqWareQueryLanguageParser.query_return query() throws RecognitionException {
        SeqWareQueryLanguageParser.query_return retval = new SeqWareQueryLanguageParser.query_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint1 =null;



        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:2: ( low_precedence_constraint ^)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:4: low_precedence_constraint ^
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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:24:1: low_precedence_constraint : ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )* ;
    public final SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.low_precedence_constraint_return retval = new SeqWareQueryLanguageParser.low_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OR3=null;
        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint2 =null;

        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint4 =null;


        CommonTree OR3_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:2: ( ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )* )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:5: high_precedence_constraint
            {
            pushFollow(FOLLOW_high_precedence_constraint_in_low_precedence_constraint88);
            high_precedence_constraint2=high_precedence_constraint();

            state._fsp--;

            adaptor.addChild(root_0, high_precedence_constraint2.getTree());

            }


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:33: ( OR ^ high_precedence_constraint )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:34: OR ^ high_precedence_constraint
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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:28:1: high_precedence_constraint : ( nested_constraint ) ( AND ^ nested_constraint )* ;
    public final SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.high_precedence_constraint_return retval = new SeqWareQueryLanguageParser.high_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AND6=null;
        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint5 =null;

        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint7 =null;


        CommonTree AND6_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:2: ( ( nested_constraint ) ( AND ^ nested_constraint )* )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint ) ( AND ^ nested_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:5: nested_constraint
            {
            pushFollow(FOLLOW_nested_constraint_in_high_precedence_constraint110);
            nested_constraint5=nested_constraint();

            state._fsp--;

            adaptor.addChild(root_0, nested_constraint5.getTree());

            }


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:24: ( AND ^ nested_constraint )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==AND) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:25: AND ^ nested_constraint
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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:32:1: nested_constraint : ( BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint | comment !);
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
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:2: ( BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint | comment !)
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
            case NAMED_THREE_PARAM_FUNCTION:
            case NAMED_TWO_PARAM_FUNCTION:
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
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:4: BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !
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
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:34:4: constraint
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constraint_in_nested_constraint142);
                    constraint11=constraint();

                    state._fsp--;

                    adaptor.addChild(root_0, constraint11.getTree());

                    }
                    break;
                case 3 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:35:4: comment !
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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:38:1: constraint : ( identifier comparison ^ constant | constant comparison ^ identifier | two_param_function | three_param_function );
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

        SeqWareQueryLanguageParser.two_param_function_return two_param_function19 =null;

        SeqWareQueryLanguageParser.three_param_function_return three_param_function20 =null;



        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:2: ( identifier comparison ^ constant | constant comparison ^ identifier | two_param_function | three_param_function )
            int alt4=4;
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
            case NAMED_TWO_PARAM_FUNCTION:
                {
                alt4=3;
                }
                break;
            case NAMED_THREE_PARAM_FUNCTION:
                {
                alt4=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:4: identifier comparison ^ constant
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
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:40:4: constant comparison ^ identifier
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
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:41:4: two_param_function
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_two_param_function_in_constraint179);
                    two_param_function19=two_param_function();

                    state._fsp--;

                    adaptor.addChild(root_0, two_param_function19.getTree());

                    }
                    break;
                case 4 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:42:5: three_param_function
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_three_param_function_in_constraint185);
                    three_param_function20=three_param_function();

                    state._fsp--;

                    adaptor.addChild(root_0, three_param_function20.getTree());

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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:45:1: identifier : ID ;
    public final SeqWareQueryLanguageParser.identifier_return identifier() throws RecognitionException {
        SeqWareQueryLanguageParser.identifier_return retval = new SeqWareQueryLanguageParser.identifier_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID21=null;

        CommonTree ID21_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:46:2: ( ID )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:46:4: ID
            {
            root_0 = (CommonTree)adaptor.nil();


            ID21=(Token)match(input,ID,FOLLOW_ID_in_identifier196); 
            ID21_tree = 
            (CommonTree)adaptor.create(ID21)
            ;
            adaptor.addChild(root_0, ID21_tree);


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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:49:1: comparison : ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ );
    public final SeqWareQueryLanguageParser.comparison_return comparison() throws RecognitionException {
        SeqWareQueryLanguageParser.comparison_return retval = new SeqWareQueryLanguageParser.comparison_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set22=null;

        CommonTree set22_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:50:2: ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set22=(Token)input.LT(1);

            if ( input.LA(1)==EQUALS||(input.LA(1) >= GT && input.LA(1) <= GTEQ)||(input.LA(1) >= LT && input.LA(1) <= LTEQ)||input.LA(1)==NOTEQUALS ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set22)
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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:53:1: constant : ( literal | NAMED_CONSTANT );
    public final SeqWareQueryLanguageParser.constant_return constant() throws RecognitionException {
        SeqWareQueryLanguageParser.constant_return retval = new SeqWareQueryLanguageParser.constant_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_CONSTANT24=null;
        SeqWareQueryLanguageParser.literal_return literal23 =null;


        CommonTree NAMED_CONSTANT24_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:54:2: ( literal | NAMED_CONSTANT )
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
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:54:4: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_literal_in_constant239);
                    literal23=literal();

                    state._fsp--;

                    adaptor.addChild(root_0, literal23.getTree());

                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:55:4: NAMED_CONSTANT
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NAMED_CONSTANT24=(Token)match(input,NAMED_CONSTANT,FOLLOW_NAMED_CONSTANT_in_constant244); 
                    NAMED_CONSTANT24_tree = 
                    (CommonTree)adaptor.create(NAMED_CONSTANT24)
                    ;
                    adaptor.addChild(root_0, NAMED_CONSTANT24_tree);


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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:58:1: literal : ( INT | FLOAT | STRING );
    public final SeqWareQueryLanguageParser.literal_return literal() throws RecognitionException {
        SeqWareQueryLanguageParser.literal_return retval = new SeqWareQueryLanguageParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set25=null;

        CommonTree set25_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:59:2: ( INT | FLOAT | STRING )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set25=(Token)input.LT(1);

            if ( input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==STRING ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set25)
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


    public static class two_param_function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "two_param_function"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:62:1: two_param_function : NAMED_TWO_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !;
    public final SeqWareQueryLanguageParser.two_param_function_return two_param_function() throws RecognitionException {
        SeqWareQueryLanguageParser.two_param_function_return retval = new SeqWareQueryLanguageParser.two_param_function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_TWO_PARAM_FUNCTION26=null;
        Token BRACKET_OPEN27=null;
        Token char_literal29=null;
        Token BRACKET_CLOSE31=null;
        SeqWareQueryLanguageParser.literal_return literal28 =null;

        SeqWareQueryLanguageParser.literal_return literal30 =null;


        CommonTree NAMED_TWO_PARAM_FUNCTION26_tree=null;
        CommonTree BRACKET_OPEN27_tree=null;
        CommonTree char_literal29_tree=null;
        CommonTree BRACKET_CLOSE31_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:63:2: ( NAMED_TWO_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:63:4: NAMED_TWO_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_TWO_PARAM_FUNCTION26=(Token)match(input,NAMED_TWO_PARAM_FUNCTION,FOLLOW_NAMED_TWO_PARAM_FUNCTION_in_two_param_function275); 
            NAMED_TWO_PARAM_FUNCTION26_tree = 
            (CommonTree)adaptor.create(NAMED_TWO_PARAM_FUNCTION26)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_TWO_PARAM_FUNCTION26_tree, root_0);


            BRACKET_OPEN27=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_two_param_function278); 

            pushFollow(FOLLOW_literal_in_two_param_function281);
            literal28=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal28.getTree());

            char_literal29=(Token)match(input,23,FOLLOW_23_in_two_param_function283); 
            char_literal29_tree = 
            (CommonTree)adaptor.create(char_literal29)
            ;
            adaptor.addChild(root_0, char_literal29_tree);


            pushFollow(FOLLOW_literal_in_two_param_function285);
            literal30=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal30.getTree());

            BRACKET_CLOSE31=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_two_param_function287); 

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
    // $ANTLR end "two_param_function"


    public static class three_param_function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "three_param_function"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:66:1: three_param_function : NAMED_THREE_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !;
    public final SeqWareQueryLanguageParser.three_param_function_return three_param_function() throws RecognitionException {
        SeqWareQueryLanguageParser.three_param_function_return retval = new SeqWareQueryLanguageParser.three_param_function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_THREE_PARAM_FUNCTION32=null;
        Token BRACKET_OPEN33=null;
        Token char_literal35=null;
        Token char_literal37=null;
        Token BRACKET_CLOSE39=null;
        SeqWareQueryLanguageParser.literal_return literal34 =null;

        SeqWareQueryLanguageParser.literal_return literal36 =null;

        SeqWareQueryLanguageParser.literal_return literal38 =null;


        CommonTree NAMED_THREE_PARAM_FUNCTION32_tree=null;
        CommonTree BRACKET_OPEN33_tree=null;
        CommonTree char_literal35_tree=null;
        CommonTree char_literal37_tree=null;
        CommonTree BRACKET_CLOSE39_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:67:2: ( NAMED_THREE_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:67:4: NAMED_THREE_PARAM_FUNCTION ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_THREE_PARAM_FUNCTION32=(Token)match(input,NAMED_THREE_PARAM_FUNCTION,FOLLOW_NAMED_THREE_PARAM_FUNCTION_in_three_param_function300); 
            NAMED_THREE_PARAM_FUNCTION32_tree = 
            (CommonTree)adaptor.create(NAMED_THREE_PARAM_FUNCTION32)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_THREE_PARAM_FUNCTION32_tree, root_0);


            BRACKET_OPEN33=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_three_param_function303); 

            pushFollow(FOLLOW_literal_in_three_param_function306);
            literal34=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal34.getTree());

            char_literal35=(Token)match(input,23,FOLLOW_23_in_three_param_function308); 
            char_literal35_tree = 
            (CommonTree)adaptor.create(char_literal35)
            ;
            adaptor.addChild(root_0, char_literal35_tree);


            pushFollow(FOLLOW_literal_in_three_param_function310);
            literal36=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal36.getTree());

            char_literal37=(Token)match(input,23,FOLLOW_23_in_three_param_function312); 
            char_literal37_tree = 
            (CommonTree)adaptor.create(char_literal37)
            ;
            adaptor.addChild(root_0, char_literal37_tree);


            pushFollow(FOLLOW_literal_in_three_param_function314);
            literal38=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal38.getTree());

            BRACKET_CLOSE39=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_three_param_function316); 

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
    // $ANTLR end "three_param_function"


    public static class comment_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comment"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:70:1: comment : ( COMMENT_EOL | COMMENT_INLINE );
    public final SeqWareQueryLanguageParser.comment_return comment() throws RecognitionException {
        SeqWareQueryLanguageParser.comment_return retval = new SeqWareQueryLanguageParser.comment_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set40=null;

        CommonTree set40_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:71:2: ( COMMENT_EOL | COMMENT_INLINE )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set40=(Token)input.LT(1);

            if ( (input.LA(1) >= COMMENT_EOL && input.LA(1) <= COMMENT_INLINE) ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set40)
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
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint88 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_OR_in_low_precedence_constraint92 = new BitSet(new long[]{0x00000000004E65C0L});
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint95 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint110 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AND_in_high_precedence_constraint114 = new BitSet(new long[]{0x00000000004E65C0L});
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint117 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_BRACKET_OPEN_in_nested_constraint131 = new BitSet(new long[]{0x00000000004E65C0L});
    public static final BitSet FOLLOW_low_precedence_constraint_in_nested_constraint134 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_nested_constraint136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_nested_constraint142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comment_in_nested_constraint147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_constraint159 = new BitSet(new long[]{0x0000000000119A00L});
    public static final BitSet FOLLOW_comparison_in_constraint161 = new BitSet(new long[]{0x0000000000424400L});
    public static final BitSet FOLLOW_constant_in_constraint164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_constraint169 = new BitSet(new long[]{0x0000000000119A00L});
    public static final BitSet FOLLOW_comparison_in_constraint171 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_identifier_in_constraint174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_two_param_function_in_constraint179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_three_param_function_in_constraint185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_CONSTANT_in_constant244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_TWO_PARAM_FUNCTION_in_two_param_function275 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_BRACKET_OPEN_in_two_param_function278 = new BitSet(new long[]{0x0000000000404400L});
    public static final BitSet FOLLOW_literal_in_two_param_function281 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_two_param_function283 = new BitSet(new long[]{0x0000000000404400L});
    public static final BitSet FOLLOW_literal_in_two_param_function285 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_two_param_function287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_THREE_PARAM_FUNCTION_in_three_param_function300 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_BRACKET_OPEN_in_three_param_function303 = new BitSet(new long[]{0x0000000000404400L});
    public static final BitSet FOLLOW_literal_in_three_param_function306 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_three_param_function308 = new BitSet(new long[]{0x0000000000404400L});
    public static final BitSet FOLLOW_literal_in_three_param_function310 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_three_param_function312 = new BitSet(new long[]{0x0000000000404400L});
    public static final BitSet FOLLOW_literal_in_three_param_function314 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_three_param_function316 = new BitSet(new long[]{0x0000000000000002L});

}