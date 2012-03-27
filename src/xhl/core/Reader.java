package xhl.core;

import static xhl.core.Token.TokenType.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import xhl.core.Token.TokenType;
import xhl.core.elements.*;

/**
 * XHL parser
 *
 * Grammar:
 *
 * <pre>
 *   block       ::= { expression LINEEND | expression-with-block }
 *   expression  ::= combination { operator combination }
 *   expression-with-block ::= combination ':' LINEEND INDENT block DEDENT
 *   combination ::= term { term }
 *   term        ::= literal | '(' expression ')'
 *   literal     ::= symbol | string | number | boolean | list | map | 'none'
 *   boolean     ::= 'true' | 'false'
 *   list        ::= '[]' | '[' expression { ',' expression } ']'
 *   map         ::= '{}' | '{' key-value { ',' key-value } '}'
 *   key-value   ::= expression ':' expression
 * </pre>
 *
 * @author Sergej Chodarev
 */
public class Reader {
    private Lexer lexer;
    private Token token;

    private static final Set<TokenType> termH;
    static {
        TokenType elements[] =
                { SYMBOL, STRING, NUMBER, TRUE, FALSE, NONE, BRACKET_OPEN,
                        BRACE_OPEN, PAR_OPEN };
        termH = new HashSet<TokenType>(Arrays.asList(elements));
    }

    public Block read(java.io.Reader input) throws IOException {
        lexer = new Lexer(input);
        token = lexer.nextToken();
        return block();
    }

    public Block read(String code) throws IOException {
        return read(new StringReader(code));
    }

    private Block block() throws IOException {
        Block block = new Block(token.position);
        while (token != null && token.type != DEDENT) {
            block.add(expression(true));
        }
        return block;
    }

    private Expression expression(boolean withBlock)
            throws IOException {
        Expression first = combination();
        while (token.type == OPERATOR) {
            Combination exp = new Combination(token.position);
            Symbol op = new Symbol(token.stringValue, token.position);
            token = lexer.nextToken();
            Expression second = combination();
            exp.add(op);
            exp.add(first);
            exp.add(second);
            first = exp;
        }
        if (token.type == LINEEND)
            token = lexer.nextToken();
        else if (withBlock && token.type == COLON) {
            token = lexer.nextToken(); // :
            token = lexer.nextToken(); // \n
            token = lexer.nextToken(); // INDENT FIXME: Add checks
            Block block = block();
            token = lexer.nextToken(); // DEDENT FIXME: Add checks
            // If block header is not a combination -- create combination
            if (!(first instanceof Combination)) {
                Combination head = new Combination(first.getPosition());
                head.add(first);
                first = head;
            }
            ((Combination) first).add(block);
        }
        return first;
    }

    private Expression combination() throws IOException {
        Combination list = new Combination(token.position);
        while (termH.contains(token.type)) {
            list.add(term());
        }
        if (list.size() == 1)
            return list.head();
        else
            return list;
    }

    private LList list() throws IOException {
        LList list = new LList(token.position);
        token = lexer.nextToken(); // [
        if (token.type == BRACKET_CLOSE) { // Empty list
            token = lexer.nextToken(); // ]
            return list;
        }
        // Non-empty list
        list.add(term());
        while (token.type != TokenType.BRACKET_CLOSE) {
            token = lexer.nextToken(); // ,
            list.add(term());
        }
        token = lexer.nextToken(); // ]
        return list;
    }

    private LMap map() throws IOException {
        LMap map = new LMap(token.position);
        token = lexer.nextToken(); // {
        if (token.type == BRACE_CLOSE) { // Empty map
            token = lexer.nextToken(); // }
            return map;
        }
        // Non-empty map
        keyValue(map);
        while (token.type != TokenType.BRACE_CLOSE) {
            token = lexer.nextToken(); // ,
            keyValue(map);
        }
        token = lexer.nextToken(); // }
        return map;
    }

    private void keyValue(LMap map) throws IOException {
        Expression key = term();
        token = lexer.nextToken(); // :
        Expression value = term();
        map.put(key, value);
    }

    private Expression term() throws IOException {
        Expression sexp = null;
        switch (token.type) {
        case SYMBOL:
            sexp = new Symbol(token.stringValue, token.position);
            token = lexer.nextToken();
            break;
        case STRING:
            sexp = new LString(token.stringValue, token.position);
            token = lexer.nextToken();
            break;
        case NUMBER:
            sexp = new LNumber(token.doubleValue, token.position);
            token = lexer.nextToken();
            break;
        case TRUE:
            sexp = new LBoolean(true, token.position);
            token = lexer.nextToken();
            break;
        case FALSE:
            sexp = new LBoolean(false, token.position);
            token = lexer.nextToken();
            break;
        case PAR_OPEN:
            token = lexer.nextToken(); // (
            sexp = expression(false);
            token = lexer.nextToken(); // )
            break;
        case BRACKET_OPEN:
            sexp = list();
            break;
        case BRACE_OPEN:
            sexp = map();
            break;
        }
        return sexp;
    }
}
