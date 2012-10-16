// $ANTLR 3.4 /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g 2012-10-04 15:30:02

package com.github.seqware.queryengine.kernel.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


/**
 * <p>SeqWareQueryLanguageParser class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
@SuppressWarnings({"all", "warnings", "unchecked"})
public class SeqWareQueryLanguageParser extends Parser {
    /** Constant <code>tokenNames="new String[] {<invalid>, <EOR>, <DOWN>,"{trunked}</code> */
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "BRACKET_CLOSE", "BRACKET_OPEN", "COMMENT_EOL", "COMMENT_INLINE", "EQUALS", "FLOAT", "GT", "GTEQ", "ID", "INT", "LT", "LTEQ", "NAMED_CONSTANT", "NAMED_FUNCTION", "NAMED_THREE_PARAM_PREDICATE", "NAMED_TWO_PARAM_PREDICATE", "NOT", "NOTEQUALS", "OR", "STRING", "WHITESPACE", "','"
    };

    /** Constant <code>EOF=-1</code> */
    public static final int EOF=-1;
    /** Constant <code>T__26=26</code> */
    public static final int T__26=26;
    /** Constant <code>AND=4</code> */
    public static final int AND=4;
    /** Constant <code>BRACKET_CLOSE=5</code> */
    public static final int BRACKET_CLOSE=5;
    /** Constant <code>BRACKET_OPEN=6</code> */
    public static final int BRACKET_OPEN=6;
    /** Constant <code>COMMENT_EOL=7</code> */
    public static final int COMMENT_EOL=7;
    /** Constant <code>COMMENT_INLINE=8</code> */
    public static final int COMMENT_INLINE=8;
    /** Constant <code>EQUALS=9</code> */
    public static final int EQUALS=9;
    /** Constant <code>FLOAT=10</code> */
    public static final int FLOAT=10;
    /** Constant <code>GT=11</code> */
    public static final int GT=11;
    /** Constant <code>GTEQ=12</code> */
    public static final int GTEQ=12;
    /** Constant <code>ID=13</code> */
    public static final int ID=13;
    /** Constant <code>INT=14</code> */
    public static final int INT=14;
    /** Constant <code>LT=15</code> */
    public static final int LT=15;
    /** Constant <code>LTEQ=16</code> */
    public static final int LTEQ=16;
    /** Constant <code>NAMED_CONSTANT=17</code> */
    public static final int NAMED_CONSTANT=17;
    /** Constant <code>NAMED_FUNCTION=18</code> */
    public static final int NAMED_FUNCTION=18;
    /** Constant <code>NAMED_THREE_PARAM_PREDICATE=19</code> */
    public static final int NAMED_THREE_PARAM_PREDICATE=19;
    /** Constant <code>NAMED_TWO_PARAM_PREDICATE=20</code> */
    public static final int NAMED_TWO_PARAM_PREDICATE=20;
    /** Constant <code>NOT=21</code> */
    public static final int NOT=21;
    /** Constant <code>NOTEQUALS=22</code> */
    public static final int NOTEQUALS=22;
    /** Constant <code>OR=23</code> */
    public static final int OR=23;
    /** Constant <code>STRING=24</code> */
    public static final int STRING=24;
    /** Constant <code>WHITESPACE=25</code> */
    public static final int WHITESPACE=25;

    // delegates
    /**
     * <p>getDelegates.</p>
     *
     * @return an array of {@link org.antlr.runtime.Parser} objects.
     */
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    /**
     * <p>Constructor for SeqWareQueryLanguageParser.</p>
     *
     * @param input a {@link org.antlr.runtime.TokenStream} object.
     */
    public SeqWareQueryLanguageParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    /**
     * <p>Constructor for SeqWareQueryLanguageParser.</p>
     *
     * @param input a {@link org.antlr.runtime.TokenStream} object.
     * @param state a {@link org.antlr.runtime.RecognizerSharedState} object.
     */
    public SeqWareQueryLanguageParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

/**
 * <p>setTreeAdaptor.</p>
 *
 * @param adaptor a {@link org.antlr.runtime.tree.TreeAdaptor} object.
 */
public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
/**
 * <p>getTreeAdaptor.</p>
 *
 * @return a {@link org.antlr.runtime.tree.TreeAdaptor} object.
 */
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    /**
     * <p>Getter for the field <code>tokenNames</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getTokenNames() { return SeqWareQueryLanguageParser.tokenNames; }
    /**
     * <p>getGrammarFileName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGrammarFileName() { return "/home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g"; }


    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "query"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:20:1: query : low_precedence_constraint ^ EOF !;
    /**
     * <p>query.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.query_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.query_return query() throws RecognitionException {
        SeqWareQueryLanguageParser.query_return retval = new SeqWareQueryLanguageParser.query_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token EOF2=null;
        SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint1 =null;


        CommonTree EOF2_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:2: ( low_precedence_constraint ^ EOF !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:21:4: low_precedence_constraint ^ EOF !
            {
            root_0 = (CommonTree)adaptor.nil();


            pushFollow(FOLLOW_low_precedence_constraint_in_query75);
            low_precedence_constraint1=low_precedence_constraint();

            state._fsp--;

            root_0 = (CommonTree)adaptor.becomeRoot(low_precedence_constraint1.getTree(), root_0);

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_query78); 

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
    /**
     * <p>low_precedence_constraint.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.low_precedence_constraint_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.low_precedence_constraint_return retval = new SeqWareQueryLanguageParser.low_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token OR4=null;
        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint3 =null;

        SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint5 =null;


        CommonTree OR4_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:2: ( ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )* )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint ) ( OR ^ high_precedence_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:4: ( high_precedence_constraint )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:25:5: high_precedence_constraint
            {
            pushFollow(FOLLOW_high_precedence_constraint_in_low_precedence_constraint91);
            high_precedence_constraint3=high_precedence_constraint();

            state._fsp--;

            adaptor.addChild(root_0, high_precedence_constraint3.getTree());

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
            	    OR4=(Token)match(input,OR,FOLLOW_OR_in_low_precedence_constraint95); 
            	    OR4_tree = 
            	    (CommonTree)adaptor.create(OR4)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(OR4_tree, root_0);


            	    pushFollow(FOLLOW_high_precedence_constraint_in_low_precedence_constraint98);
            	    high_precedence_constraint5=high_precedence_constraint();

            	    state._fsp--;

            	    adaptor.addChild(root_0, high_precedence_constraint5.getTree());

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
    /**
     * <p>high_precedence_constraint.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.high_precedence_constraint_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.high_precedence_constraint_return high_precedence_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.high_precedence_constraint_return retval = new SeqWareQueryLanguageParser.high_precedence_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token AND7=null;
        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint6 =null;

        SeqWareQueryLanguageParser.nested_constraint_return nested_constraint8 =null;


        CommonTree AND7_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:2: ( ( nested_constraint ) ( AND ^ nested_constraint )* )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint ) ( AND ^ nested_constraint )*
            {
            root_0 = (CommonTree)adaptor.nil();


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:4: ( nested_constraint )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:29:5: nested_constraint
            {
            pushFollow(FOLLOW_nested_constraint_in_high_precedence_constraint113);
            nested_constraint6=nested_constraint();

            state._fsp--;

            adaptor.addChild(root_0, nested_constraint6.getTree());

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
            	    AND7=(Token)match(input,AND,FOLLOW_AND_in_high_precedence_constraint117); 
            	    AND7_tree = 
            	    (CommonTree)adaptor.create(AND7)
            	    ;
            	    root_0 = (CommonTree)adaptor.becomeRoot(AND7_tree, root_0);


            	    pushFollow(FOLLOW_nested_constraint_in_high_precedence_constraint120);
            	    nested_constraint8=nested_constraint();

            	    state._fsp--;

            	    adaptor.addChild(root_0, nested_constraint8.getTree());

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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:32:1: nested_constraint : ( ( NOT ^)? BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint );
    /**
     * <p>nested_constraint.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.nested_constraint_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.nested_constraint_return nested_constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.nested_constraint_return retval = new SeqWareQueryLanguageParser.nested_constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NOT9=null;
        Token BRACKET_OPEN10=null;
        Token BRACKET_CLOSE12=null;
        SeqWareQueryLanguageParser.low_precedence_constraint_return low_precedence_constraint11 =null;

        SeqWareQueryLanguageParser.constraint_return constraint13 =null;


        CommonTree NOT9_tree=null;
        CommonTree BRACKET_OPEN10_tree=null;
        CommonTree BRACKET_CLOSE12_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:2: ( ( NOT ^)? BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !| constraint )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==BRACKET_OPEN||LA4_0==NOT) ) {
                alt4=1;
            }
            else if ( (LA4_0==FLOAT||(LA4_0 >= ID && LA4_0 <= INT)||(LA4_0 >= NAMED_CONSTANT && LA4_0 <= NAMED_TWO_PARAM_PREDICATE)||LA4_0==STRING) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:4: ( NOT ^)? BRACKET_OPEN ! low_precedence_constraint BRACKET_CLOSE !
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:4: ( NOT ^)?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==NOT) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:33:5: NOT ^
                            {
                            NOT9=(Token)match(input,NOT,FOLLOW_NOT_in_nested_constraint135); 
                            NOT9_tree = 
                            (CommonTree)adaptor.create(NOT9)
                            ;
                            root_0 = (CommonTree)adaptor.becomeRoot(NOT9_tree, root_0);


                            }
                            break;

                    }


                    BRACKET_OPEN10=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_nested_constraint140); 

                    pushFollow(FOLLOW_low_precedence_constraint_in_nested_constraint143);
                    low_precedence_constraint11=low_precedence_constraint();

                    state._fsp--;

                    adaptor.addChild(root_0, low_precedence_constraint11.getTree());

                    BRACKET_CLOSE12=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_nested_constraint145); 

                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:34:4: constraint
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constraint_in_nested_constraint151);
                    constraint13=constraint();

                    state._fsp--;

                    adaptor.addChild(root_0, constraint13.getTree());

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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:38:1: constraint : ( identifier comparison ^ constant | constant comparison ^ identifier | two_param_predicate | three_param_predicate );
    /**
     * <p>constraint.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.constraint_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.constraint_return constraint() throws RecognitionException {
        SeqWareQueryLanguageParser.constraint_return retval = new SeqWareQueryLanguageParser.constraint_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        SeqWareQueryLanguageParser.identifier_return identifier14 =null;

        SeqWareQueryLanguageParser.comparison_return comparison15 =null;

        SeqWareQueryLanguageParser.constant_return constant16 =null;

        SeqWareQueryLanguageParser.constant_return constant17 =null;

        SeqWareQueryLanguageParser.comparison_return comparison18 =null;

        SeqWareQueryLanguageParser.identifier_return identifier19 =null;

        SeqWareQueryLanguageParser.two_param_predicate_return two_param_predicate20 =null;

        SeqWareQueryLanguageParser.three_param_predicate_return three_param_predicate21 =null;



        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:2: ( identifier comparison ^ constant | constant comparison ^ identifier | two_param_predicate | three_param_predicate )
            int alt5=4;
            switch ( input.LA(1) ) {
            case ID:
            case NAMED_FUNCTION:
                {
                alt5=1;
                }
                break;
            case FLOAT:
            case INT:
            case NAMED_CONSTANT:
            case STRING:
                {
                alt5=2;
                }
                break;
            case NAMED_TWO_PARAM_PREDICATE:
                {
                alt5=3;
                }
                break;
            case NAMED_THREE_PARAM_PREDICATE:
                {
                alt5=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }

            switch (alt5) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:39:4: identifier comparison ^ constant
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_identifier_in_constraint163);
                    identifier14=identifier();

                    state._fsp--;

                    adaptor.addChild(root_0, identifier14.getTree());

                    pushFollow(FOLLOW_comparison_in_constraint165);
                    comparison15=comparison();

                    state._fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(comparison15.getTree(), root_0);

                    pushFollow(FOLLOW_constant_in_constraint168);
                    constant16=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant16.getTree());

                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:40:4: constant comparison ^ identifier
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_constant_in_constraint173);
                    constant17=constant();

                    state._fsp--;

                    adaptor.addChild(root_0, constant17.getTree());

                    pushFollow(FOLLOW_comparison_in_constraint175);
                    comparison18=comparison();

                    state._fsp--;

                    root_0 = (CommonTree)adaptor.becomeRoot(comparison18.getTree(), root_0);

                    pushFollow(FOLLOW_identifier_in_constraint178);
                    identifier19=identifier();

                    state._fsp--;

                    adaptor.addChild(root_0, identifier19.getTree());

                    }
                    break;
                case 3 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:41:4: two_param_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_two_param_predicate_in_constraint183);
                    two_param_predicate20=two_param_predicate();

                    state._fsp--;

                    adaptor.addChild(root_0, two_param_predicate20.getTree());

                    }
                    break;
                case 4 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:42:5: three_param_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_three_param_predicate_in_constraint189);
                    three_param_predicate21=three_param_predicate();

                    state._fsp--;

                    adaptor.addChild(root_0, three_param_predicate21.getTree());

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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:45:1: identifier : ( ID | key_value_function );
    /**
     * <p>identifier.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.identifier_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.identifier_return identifier() throws RecognitionException {
        SeqWareQueryLanguageParser.identifier_return retval = new SeqWareQueryLanguageParser.identifier_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token ID22=null;
        SeqWareQueryLanguageParser.key_value_function_return key_value_function23 =null;


        CommonTree ID22_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:46:2: ( ID | key_value_function )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ID) ) {
                alt6=1;
            }
            else if ( (LA6_0==NAMED_FUNCTION) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:46:4: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    ID22=(Token)match(input,ID,FOLLOW_ID_in_identifier200); 
                    ID22_tree = 
                    (CommonTree)adaptor.create(ID22)
                    ;
                    adaptor.addChild(root_0, ID22_tree);


                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:47:5: key_value_function
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_key_value_function_in_identifier206);
                    key_value_function23=key_value_function();

                    state._fsp--;

                    adaptor.addChild(root_0, key_value_function23.getTree());

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
    // $ANTLR end "identifier"


    public static class comparison_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comparison"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:50:1: comparison : ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ );
    /**
     * <p>comparison.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.comparison_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.comparison_return comparison() throws RecognitionException {
        SeqWareQueryLanguageParser.comparison_return retval = new SeqWareQueryLanguageParser.comparison_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set24=null;

        CommonTree set24_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:51:2: ( EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set24=(Token)input.LT(1);

            if ( input.LA(1)==EQUALS||(input.LA(1) >= GT && input.LA(1) <= GTEQ)||(input.LA(1) >= LT && input.LA(1) <= LTEQ)||input.LA(1)==NOTEQUALS ) {
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
    // $ANTLR end "comparison"


    public static class constant_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "constant"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:54:1: constant : ( literal | NAMED_CONSTANT );
    /**
     * <p>constant.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.constant_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.constant_return constant() throws RecognitionException {
        SeqWareQueryLanguageParser.constant_return retval = new SeqWareQueryLanguageParser.constant_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_CONSTANT26=null;
        SeqWareQueryLanguageParser.literal_return literal25 =null;


        CommonTree NAMED_CONSTANT26_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:55:2: ( literal | NAMED_CONSTANT )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==FLOAT||LA7_0==INT||LA7_0==STRING) ) {
                alt7=1;
            }
            else if ( (LA7_0==NAMED_CONSTANT) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:55:4: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    pushFollow(FOLLOW_literal_in_constant249);
                    literal25=literal();

                    state._fsp--;

                    adaptor.addChild(root_0, literal25.getTree());

                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:56:4: NAMED_CONSTANT
                    {
                    root_0 = (CommonTree)adaptor.nil();


                    NAMED_CONSTANT26=(Token)match(input,NAMED_CONSTANT,FOLLOW_NAMED_CONSTANT_in_constant254); 
                    NAMED_CONSTANT26_tree = 
                    (CommonTree)adaptor.create(NAMED_CONSTANT26)
                    ;
                    adaptor.addChild(root_0, NAMED_CONSTANT26_tree);


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
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:59:1: literal : ( INT | FLOAT | STRING );
    /**
     * <p>literal.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.literal_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.literal_return literal() throws RecognitionException {
        SeqWareQueryLanguageParser.literal_return retval = new SeqWareQueryLanguageParser.literal_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set27=null;

        CommonTree set27_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:60:2: ( INT | FLOAT | STRING )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set27=(Token)input.LT(1);

            if ( input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==STRING ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set27)
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


    public static class key_value_function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "key_value_function"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:63:1: key_value_function : NAMED_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !;
    /**
     * <p>key_value_function.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.key_value_function_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.key_value_function_return key_value_function() throws RecognitionException {
        SeqWareQueryLanguageParser.key_value_function_return retval = new SeqWareQueryLanguageParser.key_value_function_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_FUNCTION28=null;
        Token BRACKET_OPEN29=null;
        Token char_literal31=null;
        Token BRACKET_CLOSE33=null;
        SeqWareQueryLanguageParser.literal_return literal30 =null;

        SeqWareQueryLanguageParser.literal_return literal32 =null;


        CommonTree NAMED_FUNCTION28_tree=null;
        CommonTree BRACKET_OPEN29_tree=null;
        CommonTree char_literal31_tree=null;
        CommonTree BRACKET_CLOSE33_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:64:2: ( NAMED_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:64:4: NAMED_FUNCTION ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_FUNCTION28=(Token)match(input,NAMED_FUNCTION,FOLLOW_NAMED_FUNCTION_in_key_value_function285); 
            NAMED_FUNCTION28_tree = 
            (CommonTree)adaptor.create(NAMED_FUNCTION28)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_FUNCTION28_tree, root_0);


            BRACKET_OPEN29=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_key_value_function288); 

            pushFollow(FOLLOW_literal_in_key_value_function291);
            literal30=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal30.getTree());

            char_literal31=(Token)match(input,26,FOLLOW_26_in_key_value_function293); 
            char_literal31_tree = 
            (CommonTree)adaptor.create(char_literal31)
            ;
            adaptor.addChild(root_0, char_literal31_tree);


            pushFollow(FOLLOW_literal_in_key_value_function295);
            literal32=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal32.getTree());

            BRACKET_CLOSE33=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_key_value_function297); 

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
    // $ANTLR end "key_value_function"


    public static class two_param_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "two_param_predicate"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:67:1: two_param_predicate : NAMED_TWO_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !;
    /**
     * <p>two_param_predicate.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.two_param_predicate_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.two_param_predicate_return two_param_predicate() throws RecognitionException {
        SeqWareQueryLanguageParser.two_param_predicate_return retval = new SeqWareQueryLanguageParser.two_param_predicate_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_TWO_PARAM_PREDICATE34=null;
        Token BRACKET_OPEN35=null;
        Token char_literal37=null;
        Token BRACKET_CLOSE39=null;
        SeqWareQueryLanguageParser.literal_return literal36 =null;

        SeqWareQueryLanguageParser.literal_return literal38 =null;


        CommonTree NAMED_TWO_PARAM_PREDICATE34_tree=null;
        CommonTree BRACKET_OPEN35_tree=null;
        CommonTree char_literal37_tree=null;
        CommonTree BRACKET_CLOSE39_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:68:2: ( NAMED_TWO_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:68:4: NAMED_TWO_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_TWO_PARAM_PREDICATE34=(Token)match(input,NAMED_TWO_PARAM_PREDICATE,FOLLOW_NAMED_TWO_PARAM_PREDICATE_in_two_param_predicate309); 
            NAMED_TWO_PARAM_PREDICATE34_tree = 
            (CommonTree)adaptor.create(NAMED_TWO_PARAM_PREDICATE34)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_TWO_PARAM_PREDICATE34_tree, root_0);


            BRACKET_OPEN35=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_two_param_predicate312); 

            pushFollow(FOLLOW_literal_in_two_param_predicate315);
            literal36=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal36.getTree());

            char_literal37=(Token)match(input,26,FOLLOW_26_in_two_param_predicate317); 
            char_literal37_tree = 
            (CommonTree)adaptor.create(char_literal37)
            ;
            adaptor.addChild(root_0, char_literal37_tree);


            pushFollow(FOLLOW_literal_in_two_param_predicate319);
            literal38=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal38.getTree());

            BRACKET_CLOSE39=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_two_param_predicate321); 

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
    // $ANTLR end "two_param_predicate"


    public static class three_param_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "three_param_predicate"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:71:1: three_param_predicate : NAMED_THREE_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !;
    /**
     * <p>three_param_predicate.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.three_param_predicate_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.three_param_predicate_return three_param_predicate() throws RecognitionException {
        SeqWareQueryLanguageParser.three_param_predicate_return retval = new SeqWareQueryLanguageParser.three_param_predicate_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token NAMED_THREE_PARAM_PREDICATE40=null;
        Token BRACKET_OPEN41=null;
        Token char_literal43=null;
        Token char_literal45=null;
        Token BRACKET_CLOSE47=null;
        SeqWareQueryLanguageParser.literal_return literal42 =null;

        SeqWareQueryLanguageParser.literal_return literal44 =null;

        SeqWareQueryLanguageParser.literal_return literal46 =null;


        CommonTree NAMED_THREE_PARAM_PREDICATE40_tree=null;
        CommonTree BRACKET_OPEN41_tree=null;
        CommonTree char_literal43_tree=null;
        CommonTree char_literal45_tree=null;
        CommonTree BRACKET_CLOSE47_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:72:2: ( NAMED_THREE_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !)
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:72:4: NAMED_THREE_PARAM_PREDICATE ^ BRACKET_OPEN ! literal ',' literal ',' literal BRACKET_CLOSE !
            {
            root_0 = (CommonTree)adaptor.nil();


            NAMED_THREE_PARAM_PREDICATE40=(Token)match(input,NAMED_THREE_PARAM_PREDICATE,FOLLOW_NAMED_THREE_PARAM_PREDICATE_in_three_param_predicate334); 
            NAMED_THREE_PARAM_PREDICATE40_tree = 
            (CommonTree)adaptor.create(NAMED_THREE_PARAM_PREDICATE40)
            ;
            root_0 = (CommonTree)adaptor.becomeRoot(NAMED_THREE_PARAM_PREDICATE40_tree, root_0);


            BRACKET_OPEN41=(Token)match(input,BRACKET_OPEN,FOLLOW_BRACKET_OPEN_in_three_param_predicate337); 

            pushFollow(FOLLOW_literal_in_three_param_predicate340);
            literal42=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal42.getTree());

            char_literal43=(Token)match(input,26,FOLLOW_26_in_three_param_predicate342); 
            char_literal43_tree = 
            (CommonTree)adaptor.create(char_literal43)
            ;
            adaptor.addChild(root_0, char_literal43_tree);


            pushFollow(FOLLOW_literal_in_three_param_predicate344);
            literal44=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal44.getTree());

            char_literal45=(Token)match(input,26,FOLLOW_26_in_three_param_predicate346); 
            char_literal45_tree = 
            (CommonTree)adaptor.create(char_literal45)
            ;
            adaptor.addChild(root_0, char_literal45_tree);


            pushFollow(FOLLOW_literal_in_three_param_predicate348);
            literal46=literal();

            state._fsp--;

            adaptor.addChild(root_0, literal46.getTree());

            BRACKET_CLOSE47=(Token)match(input,BRACKET_CLOSE,FOLLOW_BRACKET_CLOSE_in_three_param_predicate350); 

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
    // $ANTLR end "three_param_predicate"


    public static class comment_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "comment"
    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:75:1: comment : ( COMMENT_EOL | COMMENT_INLINE );
    /**
     * <p>comment.</p>
     *
     * @return a {@link com.github.seqware.queryengine.kernel.output.SeqWareQueryLanguageParser.comment_return} object.
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final SeqWareQueryLanguageParser.comment_return comment() throws RecognitionException {
        SeqWareQueryLanguageParser.comment_return retval = new SeqWareQueryLanguageParser.comment_return();
        retval.start = input.LT(1);


        CommonTree root_0 = null;

        Token set48=null;

        CommonTree set48_tree=null;

        try {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:76:2: ( COMMENT_EOL | COMMENT_INLINE )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            {
            root_0 = (CommonTree)adaptor.nil();


            set48=(Token)input.LT(1);

            if ( (input.LA(1) >= COMMENT_EOL && input.LA(1) <= COMMENT_INLINE) ) {
                input.consume();
                adaptor.addChild(root_0, 
                (CommonTree)adaptor.create(set48)
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


 

    /** Constant <code>FOLLOW_low_precedence_constraint_in_query75</code> */
    public static final BitSet FOLLOW_low_precedence_constraint_in_query75 = new BitSet(new long[]{0x0000000000000000L});
    /** Constant <code>FOLLOW_EOF_in_query78</code> */
    public static final BitSet FOLLOW_EOF_in_query78 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_high_precedence_constraint_in_low_precedence_constraint91</code> */
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint91 = new BitSet(new long[]{0x0000000000800002L});
    /** Constant <code>FOLLOW_OR_in_low_precedence_constraint95</code> */
    public static final BitSet FOLLOW_OR_in_low_precedence_constraint95 = new BitSet(new long[]{0x00000000013E6440L});
    /** Constant <code>FOLLOW_high_precedence_constraint_in_low_precedence_constraint98</code> */
    public static final BitSet FOLLOW_high_precedence_constraint_in_low_precedence_constraint98 = new BitSet(new long[]{0x0000000000800002L});
    /** Constant <code>FOLLOW_nested_constraint_in_high_precedence_constraint113</code> */
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint113 = new BitSet(new long[]{0x0000000000000012L});
    /** Constant <code>FOLLOW_AND_in_high_precedence_constraint117</code> */
    public static final BitSet FOLLOW_AND_in_high_precedence_constraint117 = new BitSet(new long[]{0x00000000013E6440L});
    /** Constant <code>FOLLOW_nested_constraint_in_high_precedence_constraint120</code> */
    public static final BitSet FOLLOW_nested_constraint_in_high_precedence_constraint120 = new BitSet(new long[]{0x0000000000000012L});
    /** Constant <code>FOLLOW_NOT_in_nested_constraint135</code> */
    public static final BitSet FOLLOW_NOT_in_nested_constraint135 = new BitSet(new long[]{0x0000000000000040L});
    /** Constant <code>FOLLOW_BRACKET_OPEN_in_nested_constraint140</code> */
    public static final BitSet FOLLOW_BRACKET_OPEN_in_nested_constraint140 = new BitSet(new long[]{0x00000000013E6440L});
    /** Constant <code>FOLLOW_low_precedence_constraint_in_nested_constraint143</code> */
    public static final BitSet FOLLOW_low_precedence_constraint_in_nested_constraint143 = new BitSet(new long[]{0x0000000000000020L});
    /** Constant <code>FOLLOW_BRACKET_CLOSE_in_nested_constraint145</code> */
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_nested_constraint145 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_constraint_in_nested_constraint151</code> */
    public static final BitSet FOLLOW_constraint_in_nested_constraint151 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_identifier_in_constraint163</code> */
    public static final BitSet FOLLOW_identifier_in_constraint163 = new BitSet(new long[]{0x0000000000419A00L});
    /** Constant <code>FOLLOW_comparison_in_constraint165</code> */
    public static final BitSet FOLLOW_comparison_in_constraint165 = new BitSet(new long[]{0x0000000001024400L});
    /** Constant <code>FOLLOW_constant_in_constraint168</code> */
    public static final BitSet FOLLOW_constant_in_constraint168 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_constant_in_constraint173</code> */
    public static final BitSet FOLLOW_constant_in_constraint173 = new BitSet(new long[]{0x0000000000419A00L});
    /** Constant <code>FOLLOW_comparison_in_constraint175</code> */
    public static final BitSet FOLLOW_comparison_in_constraint175 = new BitSet(new long[]{0x0000000000042000L});
    /** Constant <code>FOLLOW_identifier_in_constraint178</code> */
    public static final BitSet FOLLOW_identifier_in_constraint178 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_two_param_predicate_in_constraint183</code> */
    public static final BitSet FOLLOW_two_param_predicate_in_constraint183 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_three_param_predicate_in_constraint189</code> */
    public static final BitSet FOLLOW_three_param_predicate_in_constraint189 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_ID_in_identifier200</code> */
    public static final BitSet FOLLOW_ID_in_identifier200 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_key_value_function_in_identifier206</code> */
    public static final BitSet FOLLOW_key_value_function_in_identifier206 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_literal_in_constant249</code> */
    public static final BitSet FOLLOW_literal_in_constant249 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_NAMED_CONSTANT_in_constant254</code> */
    public static final BitSet FOLLOW_NAMED_CONSTANT_in_constant254 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_NAMED_FUNCTION_in_key_value_function285</code> */
    public static final BitSet FOLLOW_NAMED_FUNCTION_in_key_value_function285 = new BitSet(new long[]{0x0000000000000040L});
    /** Constant <code>FOLLOW_BRACKET_OPEN_in_key_value_function288</code> */
    public static final BitSet FOLLOW_BRACKET_OPEN_in_key_value_function288 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_key_value_function291</code> */
    public static final BitSet FOLLOW_literal_in_key_value_function291 = new BitSet(new long[]{0x0000000004000000L});
    /** Constant <code>FOLLOW_26_in_key_value_function293</code> */
    public static final BitSet FOLLOW_26_in_key_value_function293 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_key_value_function295</code> */
    public static final BitSet FOLLOW_literal_in_key_value_function295 = new BitSet(new long[]{0x0000000000000020L});
    /** Constant <code>FOLLOW_BRACKET_CLOSE_in_key_value_function297</code> */
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_key_value_function297 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_NAMED_TWO_PARAM_PREDICATE_in_two_param_predicate309</code> */
    public static final BitSet FOLLOW_NAMED_TWO_PARAM_PREDICATE_in_two_param_predicate309 = new BitSet(new long[]{0x0000000000000040L});
    /** Constant <code>FOLLOW_BRACKET_OPEN_in_two_param_predicate312</code> */
    public static final BitSet FOLLOW_BRACKET_OPEN_in_two_param_predicate312 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_two_param_predicate315</code> */
    public static final BitSet FOLLOW_literal_in_two_param_predicate315 = new BitSet(new long[]{0x0000000004000000L});
    /** Constant <code>FOLLOW_26_in_two_param_predicate317</code> */
    public static final BitSet FOLLOW_26_in_two_param_predicate317 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_two_param_predicate319</code> */
    public static final BitSet FOLLOW_literal_in_two_param_predicate319 = new BitSet(new long[]{0x0000000000000020L});
    /** Constant <code>FOLLOW_BRACKET_CLOSE_in_two_param_predicate321</code> */
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_two_param_predicate321 = new BitSet(new long[]{0x0000000000000002L});
    /** Constant <code>FOLLOW_NAMED_THREE_PARAM_PREDICATE_in_three_param_predicate334</code> */
    public static final BitSet FOLLOW_NAMED_THREE_PARAM_PREDICATE_in_three_param_predicate334 = new BitSet(new long[]{0x0000000000000040L});
    /** Constant <code>FOLLOW_BRACKET_OPEN_in_three_param_predicate337</code> */
    public static final BitSet FOLLOW_BRACKET_OPEN_in_three_param_predicate337 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_three_param_predicate340</code> */
    public static final BitSet FOLLOW_literal_in_three_param_predicate340 = new BitSet(new long[]{0x0000000004000000L});
    /** Constant <code>FOLLOW_26_in_three_param_predicate342</code> */
    public static final BitSet FOLLOW_26_in_three_param_predicate342 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_three_param_predicate344</code> */
    public static final BitSet FOLLOW_literal_in_three_param_predicate344 = new BitSet(new long[]{0x0000000004000000L});
    /** Constant <code>FOLLOW_26_in_three_param_predicate346</code> */
    public static final BitSet FOLLOW_26_in_three_param_predicate346 = new BitSet(new long[]{0x0000000001004400L});
    /** Constant <code>FOLLOW_literal_in_three_param_predicate348</code> */
    public static final BitSet FOLLOW_literal_in_three_param_predicate348 = new BitSet(new long[]{0x0000000000000020L});
    /** Constant <code>FOLLOW_BRACKET_CLOSE_in_three_param_predicate350</code> */
    public static final BitSet FOLLOW_BRACKET_CLOSE_in_three_param_predicate350 = new BitSet(new long[]{0x0000000000000002L});

}
