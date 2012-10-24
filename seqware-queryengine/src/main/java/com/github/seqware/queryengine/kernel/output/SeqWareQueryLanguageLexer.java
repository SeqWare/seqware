// $ANTLR 3.4 /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g 2012-10-04 15:30:03

package com.github.seqware.queryengine.kernel.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>SeqWareQueryLanguageLexer class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
@SuppressWarnings({"all", "warnings", "unchecked"})
public class SeqWareQueryLanguageLexer extends Lexer {
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
    // delegators
    /**
     * <p>getDelegates.</p>
     *
     * @return an array of {@link org.antlr.runtime.Lexer} objects.
     */
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    /**
     * <p>Constructor for SeqWareQueryLanguageLexer.</p>
     */
    public SeqWareQueryLanguageLexer() {} 
    /**
     * <p>Constructor for SeqWareQueryLanguageLexer.</p>
     *
     * @param input a {@link org.antlr.runtime.CharStream} object.
     */
    public SeqWareQueryLanguageLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    /**
     * <p>Constructor for SeqWareQueryLanguageLexer.</p>
     *
     * @param input a {@link org.antlr.runtime.CharStream} object.
     * @param state a {@link org.antlr.runtime.RecognizerSharedState} object.
     */
    public SeqWareQueryLanguageLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    /**
     * <p>getGrammarFileName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGrammarFileName() { return "/home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g"; }

    // $ANTLR start "T__26"
    /**
     * <p>mT__26.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:12:7: ( ',' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:12:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "NAMED_CONSTANT"
    /**
     * <p>mNAMED_CONSTANT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNAMED_CONSTANT() throws RecognitionException {
        try {
            int _type = NAMED_CONSTANT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:2: ( 'STRAND_UNKNOWN' | 'NOT_STRANDED' | 'NEGATIVE_STRAND' | 'POSITIVE_STRAND' )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 'S':
                {
                alt1=1;
                }
                break;
            case 'N':
                {
                int LA1_2 = input.LA(2);

                if ( (LA1_2=='O') ) {
                    alt1=2;
                }
                else if ( (LA1_2=='E') ) {
                    alt1=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 2, input);

                    throw nvae;

                }
                }
                break;
            case 'P':
                {
                alt1=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:4: 'STRAND_UNKNOWN'
                    {
                    match("STRAND_UNKNOWN"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:23: 'NOT_STRANDED'
                    {
                    match("NOT_STRANDED"); 



                    }
                    break;
                case 3 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:40: 'NEGATIVE_STRAND'
                    {
                    match("NEGATIVE_STRAND"); 



                    }
                    break;
                case 4 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:60: 'POSITIVE_STRAND'
                    {
                    match("POSITIVE_STRAND"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_CONSTANT"

    // $ANTLR start "NAMED_FUNCTION"
    /**
     * <p>mNAMED_FUNCTION.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNAMED_FUNCTION() throws RecognitionException {
        try {
            int _type = NAMED_FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:84:2: ( 'tagValue' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:84:4: 'tagValue'
            {
            match("tagValue"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_FUNCTION"

    // $ANTLR start "NAMED_TWO_PARAM_PREDICATE"
    /**
     * <p>mNAMED_TWO_PARAM_PREDICATE.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNAMED_TWO_PARAM_PREDICATE() throws RecognitionException {
        try {
            int _type = NAMED_TWO_PARAM_PREDICATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:2: ( 'tagOccurrence' | 'tagHierarchicalOccurrence' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='t') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='a') ) {
                    int LA2_2 = input.LA(3);

                    if ( (LA2_2=='g') ) {
                        int LA2_3 = input.LA(4);

                        if ( (LA2_3=='O') ) {
                            alt2=1;
                        }
                        else if ( (LA2_3=='H') ) {
                            alt2=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 2, 3, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 2, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:4: 'tagOccurrence'
                    {
                    match("tagOccurrence"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:22: 'tagHierarchicalOccurrence'
                    {
                    match("tagHierarchicalOccurrence"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_TWO_PARAM_PREDICATE"

    // $ANTLR start "NAMED_THREE_PARAM_PREDICATE"
    /**
     * <p>mNAMED_THREE_PARAM_PREDICATE.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNAMED_THREE_PARAM_PREDICATE() throws RecognitionException {
        try {
            int _type = NAMED_THREE_PARAM_PREDICATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:92:2: ( 'tagValuePresence' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:92:4: 'tagValuePresence'
            {
            match("tagValuePresence"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_THREE_PARAM_PREDICATE"

    // $ANTLR start "ID"
    /**
     * <p>mID.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')||(LA3_0 >= 'A' && LA3_0 <= 'Z')||LA3_0=='_'||(LA3_0 >= 'a' && LA3_0 <= 'z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    /**
     * <p>mINT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:2: ( ( '0' .. '9' )+ )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:4: ( '0' .. '9' )+
            {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:4: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    /**
     * <p>mFLOAT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:6: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                alt8=1;
            }
            else if ( (LA8_0=='.') ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:8: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:8: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    match('.'); 

                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:24: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:105:8: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:105:12: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "COMMENT_EOL"
    /**
     * <p>mCOMMENT_EOL.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mCOMMENT_EOL() throws RecognitionException {
        try {
            int _type = COMMENT_EOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:2: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:4: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 



            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:9: (~ ( '\\n' | '\\r' ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\f')||(LA9_0 >= '\u000E' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:23: ( '\\r' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\r') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:23: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT_EOL"

    // $ANTLR start "COMMENT_INLINE"
    /**
     * <p>mCOMMENT_INLINE.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mCOMMENT_INLINE() throws RecognitionException {
        try {
            int _type = COMMENT_INLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:9: ( options {greedy=false; } : . )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0=='*') ) {
                    int LA11_1 = input.LA(2);

                    if ( (LA11_1=='/') ) {
                        alt11=2;
                    }
                    else if ( ((LA11_1 >= '\u0000' && LA11_1 <= '.')||(LA11_1 >= '0' && LA11_1 <= '\uFFFF')) ) {
                        alt11=1;
                    }


                }
                else if ( ((LA11_0 >= '\u0000' && LA11_0 <= ')')||(LA11_0 >= '+' && LA11_0 <= '\uFFFF')) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:37: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT_INLINE"

    // $ANTLR start "STRING"
    /**
     * <p>mSTRING.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:5: ( '\"' (~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:8: '\"' (~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:12: (~ ( '\\\\' | '\"' ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0 >= '\u0000' && LA12_0 <= '!')||(LA12_0 >= '#' && LA12_0 <= '[')||(LA12_0 >= ']' && LA12_0 <= '\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "NOT"
    /**
     * <p>mNOT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:121:2: ( '!' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:121:4: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "OR"
    /**
     * <p>mOR.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:125:2: ( '||' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:125:4: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    /**
     * <p>mAND.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:129:2: ( '&&' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:129:4: '&&'
            {
            match("&&"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "EQUALS"
    /**
     * <p>mEQUALS.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:133:2: ( '==' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:133:4: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "NOTEQUALS"
    /**
     * <p>mNOTEQUALS.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mNOTEQUALS() throws RecognitionException {
        try {
            int _type = NOTEQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:137:2: ( '!=' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:137:4: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQUALS"

    // $ANTLR start "LT"
    /**
     * <p>mLT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:141:2: ( '<' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:141:4: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "LTEQ"
    /**
     * <p>mLTEQ.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mLTEQ() throws RecognitionException {
        try {
            int _type = LTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:145:2: ( '<=' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:145:4: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LTEQ"

    // $ANTLR start "GT"
    /**
     * <p>mGT.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:149:2: ( '>' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:149:4: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "GTEQ"
    /**
     * <p>mGTEQ.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mGTEQ() throws RecognitionException {
        try {
            int _type = GTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:153:2: ( '>=' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:153:4: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GTEQ"

    // $ANTLR start "BRACKET_OPEN"
    /**
     * <p>mBRACKET_OPEN.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mBRACKET_OPEN() throws RecognitionException {
        try {
            int _type = BRACKET_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:157:2: ( '(' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:157:4: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BRACKET_OPEN"

    // $ANTLR start "BRACKET_CLOSE"
    /**
     * <p>mBRACKET_CLOSE.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mBRACKET_CLOSE() throws RecognitionException {
        try {
            int _type = BRACKET_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:161:2: ( ')' )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:161:4: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BRACKET_CLOSE"

    // $ANTLR start "WHITESPACE"
    /**
     * <p>mWHITESPACE.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:165:2: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:165:4: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:165:4: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0 >= '\t' && LA13_0 <= '\n')||(LA13_0 >= '\f' && LA13_0 <= '\r')||LA13_0==' ') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


             _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    /**
     * <p>mTokens.</p>
     *
     * @throws org.antlr.runtime.RecognitionException if any.
     */
    public void mTokens() throws RecognitionException {
        // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:8: ( T__26 | NAMED_CONSTANT | NAMED_FUNCTION | NAMED_TWO_PARAM_PREDICATE | NAMED_THREE_PARAM_PREDICATE | ID | INT | FLOAT | COMMENT_EOL | COMMENT_INLINE | STRING | NOT | OR | AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | BRACKET_OPEN | BRACKET_CLOSE | WHITESPACE )
        int alt14=23;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:10: T__26
                {
                mT__26(); 


                }
                break;
            case 2 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:16: NAMED_CONSTANT
                {
                mNAMED_CONSTANT(); 


                }
                break;
            case 3 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:31: NAMED_FUNCTION
                {
                mNAMED_FUNCTION(); 


                }
                break;
            case 4 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:46: NAMED_TWO_PARAM_PREDICATE
                {
                mNAMED_TWO_PARAM_PREDICATE(); 


                }
                break;
            case 5 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:72: NAMED_THREE_PARAM_PREDICATE
                {
                mNAMED_THREE_PARAM_PREDICATE(); 


                }
                break;
            case 6 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:100: ID
                {
                mID(); 


                }
                break;
            case 7 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:103: INT
                {
                mINT(); 


                }
                break;
            case 8 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:107: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 9 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:113: COMMENT_EOL
                {
                mCOMMENT_EOL(); 


                }
                break;
            case 10 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:125: COMMENT_INLINE
                {
                mCOMMENT_INLINE(); 


                }
                break;
            case 11 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:140: STRING
                {
                mSTRING(); 


                }
                break;
            case 12 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:147: NOT
                {
                mNOT(); 


                }
                break;
            case 13 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:151: OR
                {
                mOR(); 


                }
                break;
            case 14 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:154: AND
                {
                mAND(); 


                }
                break;
            case 15 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:158: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 16 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:165: NOTEQUALS
                {
                mNOTEQUALS(); 


                }
                break;
            case 17 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:175: LT
                {
                mLT(); 


                }
                break;
            case 18 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:178: LTEQ
                {
                mLTEQ(); 


                }
                break;
            case 19 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:183: GT
                {
                mGT(); 


                }
                break;
            case 20 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:186: GTEQ
                {
                mGTEQ(); 


                }
                break;
            case 21 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:191: BRACKET_OPEN
                {
                mBRACKET_OPEN(); 


                }
                break;
            case 22 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:204: BRACKET_CLOSE
                {
                mBRACKET_CLOSE(); 


                }
                break;
            case 23 :
                // /home/dyuen/seqware_github/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:218: WHITESPACE
                {
                mWHITESPACE(); 


                }
                break;

        }

    }


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\2\uffff\4\6\1\uffff\1\31\3\uffff\1\35\3\uffff\1\37\1\41\3\uffff"+
        "\5\6\11\uffff\45\6\1\117\7\6\1\uffff\21\6\1\150\6\6\1\uffff\3\6"+
        "\1\162\1\6\1\150\3\6\1\uffff\1\6\2\150\2\6\1\172\1\6\1\uffff\10"+
        "\6\1\162";
    static final String DFA14_eofS =
        "\u0084\uffff";
    static final String DFA14_minS =
        "\1\11\1\uffff\1\124\1\105\1\117\1\141\1\uffff\1\56\1\uffff\1\52"+
        "\1\uffff\1\75\3\uffff\2\75\3\uffff\1\122\1\124\1\107\1\123\1\147"+
        "\11\uffff\1\101\1\137\1\101\1\111\1\110\1\116\1\123\2\124\1\141"+
        "\1\143\1\151\1\104\1\124\2\111\1\154\1\143\1\145\1\137\1\122\2\126"+
        "\2\165\1\162\1\125\1\101\2\105\1\145\1\162\1\141\2\116\2\137\1\60"+
        "\2\162\1\113\1\104\2\123\1\162\1\uffff\1\145\1\143\1\116\1\105\2"+
        "\124\1\145\1\156\1\150\1\117\1\104\2\122\1\163\1\143\1\151\1\127"+
        "\1\60\2\101\2\145\1\143\1\116\1\uffff\2\116\1\156\1\60\1\141\1\60"+
        "\2\104\1\143\1\uffff\1\154\2\60\1\145\1\117\1\60\1\143\1\uffff\1"+
        "\143\1\165\2\162\1\145\1\156\1\143\1\145\1\60";
    static final String DFA14_maxS =
        "\1\174\1\uffff\1\124\2\117\1\141\1\uffff\1\71\1\uffff\1\57\1\uffff"+
        "\1\75\3\uffff\2\75\3\uffff\1\122\1\124\1\107\1\123\1\147\11\uffff"+
        "\1\101\1\137\1\101\1\111\1\126\1\116\1\123\2\124\1\141\1\143\1\151"+
        "\1\104\1\124\2\111\1\154\1\143\1\145\1\137\1\122\2\126\2\165\1\162"+
        "\1\125\1\101\2\105\1\145\1\162\1\141\2\116\2\137\1\172\2\162\1\113"+
        "\1\104\2\123\1\162\1\uffff\1\145\1\143\1\116\1\105\2\124\1\145\1"+
        "\156\1\150\1\117\1\104\2\122\1\163\1\143\1\151\1\127\1\172\2\101"+
        "\2\145\1\143\1\116\1\uffff\2\116\1\156\1\172\1\141\1\172\2\104\1"+
        "\143\1\uffff\1\154\2\172\1\145\1\117\1\172\1\143\1\uffff\1\143\1"+
        "\165\2\162\1\145\1\156\1\143\1\145\1\172";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\4\uffff\1\6\1\uffff\1\10\1\uffff\1\13\1\uffff\1\15"+
        "\1\16\1\17\2\uffff\1\25\1\26\1\27\5\uffff\1\7\1\11\1\12\1\20\1\14"+
        "\1\22\1\21\1\24\1\23\55\uffff\1\3\30\uffff\1\2\11\uffff\1\4\7\uffff"+
        "\1\5\11\uffff";
    static final String DFA14_specialS =
        "\u0084\uffff}>";
    static final String[] DFA14_transitionS = {
            "\2\23\1\uffff\2\23\22\uffff\1\23\1\13\1\12\3\uffff\1\15\1\uffff"+
            "\1\21\1\22\2\uffff\1\1\1\uffff\1\10\1\11\12\7\2\uffff\1\17\1"+
            "\16\1\20\2\uffff\15\6\1\3\1\6\1\4\2\6\1\2\7\6\4\uffff\1\6\1"+
            "\uffff\23\6\1\5\6\6\1\uffff\1\14",
            "",
            "\1\24",
            "\1\26\11\uffff\1\25",
            "\1\27",
            "\1\30",
            "",
            "\1\10\1\uffff\12\7",
            "",
            "\1\33\4\uffff\1\32",
            "",
            "\1\34",
            "",
            "",
            "",
            "\1\36",
            "\1\40",
            "",
            "",
            "",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\55\6\uffff\1\54\6\uffff\1\53",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\12\6\7\uffff\17\6\1\116\12\6\4\uffff\1\6\1\uffff\32\6",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "",
            "\1\157",
            "\1\160",
            "\1\161",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\1\163",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\1\164",
            "\1\165",
            "\1\166",
            "",
            "\1\167",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\1\170",
            "\1\171",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6",
            "\1\173",
            "",
            "\1\174",
            "\1\175",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\12\6\7\uffff\32\6\4\uffff\1\6\1\uffff\32\6"
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__26 | NAMED_CONSTANT | NAMED_FUNCTION | NAMED_TWO_PARAM_PREDICATE | NAMED_THREE_PARAM_PREDICATE | ID | INT | FLOAT | COMMENT_EOL | COMMENT_INLINE | STRING | NOT | OR | AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | BRACKET_OPEN | BRACKET_CLOSE | WHITESPACE );";
        }
    }
 

}
