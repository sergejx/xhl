package xhl.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import xhl.core.Token.TokenType;
import xhl.core.elements.*;

import com.google.common.collect.ImmutableSet;

import static xhl.core.Token.TokenType.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * XHL parser
 *
 * Grammar:
 *
 * <pre>
 *   block       ::= { expression LINEEND | expression-with-block }
 *   expression  ::= combination { operator combination } | operator
 *   expression-with-block ::= combination ':' LINEEND INDENT block DEDENT
 *   combination ::= term { term }
 *   term        ::= literal | '(' expression ')'
 *   literal     ::= symbol | string | number | boolean | list | map | 'none'
 *   boolean     ::= 'true' | 'false'
 *   list        ::= '[]' | '[' expression { ',' expression } ']'
 *   map         ::= '{}' | '{' key-value { ',' key-value } '}'
 *   key-value   ::= expression ':' expression
 *   symbol      ::= plain-symbol { '.' plain-symbol }
 * </pre>
 *
 * @author Sergej Chodarev
 */
public class Reader {
    private Lexer lexer;
    private Token token;

    private static final ImmutableSet<TokenType> termH = ImmutableSet.of(
            SYMBOL, STRING, NUMBER, TRUE, FALSE, NONE, BRACKET_OPEN,
            BRACE_OPEN, PAR_OPEN);

    public static Block read(java.io.Reader input) throws IOException {
        return new Reader().parse(input);
    }

    public static Block read(String code) throws IOException {
        return read(new StringReader(code));
    }

    private Reader() {}

    private Block parse(java.io.Reader input) throws IOException {
        lexer = new Lexer(input);
        token = lexer.nextToken();
        return block();
    }

    private Block block() throws IOException {
        Block block = new Block(token.position);
        while (token != null && token.type != DEDENT) {
            block.add(expression(true, true));
        }
        return block;
    }

    private Expression expression(boolean withBlock, boolean colonAccepted)
            throws IOException {
        if (token.type == OPERATOR) { // Single operator can be used as a symbol
            Symbol op = new Symbol(token.stringValue, token.position);
            token = lexer.nextToken();
            return op;
        }
        Expression first = combination();
        while (token.type == OPERATOR) {
            if (isColon()
                    && (lexer.checkNextToken().type == LINEEND || !colonAccepted))
                break;
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
        else if (withBlock && isColon()) {
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

    private SList list() throws IOException {
        SList list = new SList(token.position);
        token = lexer.nextToken(); // [
        if (token.type == BRACKET_CLOSE) { // Empty list
            token = lexer.nextToken(); // ]
            return list;
        }
        // Non-empty list
        list.add(expression(false, true));
        while (token.type != TokenType.BRACKET_CLOSE) {
            token = lexer.nextToken(); // ,
            list.add(expression(false, true));
        }
        token = lexer.nextToken(); // ]
        return list;
    }

    private SMap map() throws IOException {
        SMap map = new SMap(token.position);
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

    private void keyValue(SMap map) throws IOException {
        Expression key = expression(false, false);
        token = lexer.nextToken(); // :
        Expression value = expression(false, false);
        map.put(key, value);
    }

    private Expression term() throws IOException {
        Expression sexp = null;
        switch (token.type) {
        case SYMBOL:
            sexp = symbol();
            break;
        case STRING:
            sexp = new SString(token.stringValue, token.position);
            token = lexer.nextToken();
            break;
        case NUMBER:
            sexp = new SNumber(token.doubleValue, token.position);
            token = lexer.nextToken();
            break;
        case TRUE:
            sexp = new SBoolean(true, token.position);
            token = lexer.nextToken();
            break;
        case FALSE:
            sexp = new SBoolean(false, token.position);
            token = lexer.nextToken();
            break;
        case PAR_OPEN:
            token = lexer.nextToken(); // (
            sexp = expression(false, true);
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

    private Expression symbol() {
        Position position = token.position;
        List<String> name = newArrayList(token.stringValue);
        token = lexer.nextToken();
        while (token.type == DOT) {
            lexer.nextToken(); // .
            name.add(token.stringValue);
            lexer.nextToken();
        }
        return new Symbol(name.toArray(new String[name.size()]), position);
    }

    private boolean isColon() {
        return token.type == OPERATOR && token.stringValue.equals(":");
    }
}
