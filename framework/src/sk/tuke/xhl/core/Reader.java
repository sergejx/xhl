/* XHL - Extensible Host Language
 * Copyright 2012 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.tuke.xhl.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import sk.tuke.xhl.core.Token.TokenType;
import sk.tuke.xhl.core.elements.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static sk.tuke.xhl.core.MaybeError.fail;
import static sk.tuke.xhl.core.MaybeError.succeed;
import static sk.tuke.xhl.core.Token.TokenType.*;

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
 *   literal     ::= symbol | string | number | boolean | list | map | 'null'
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
    private PeekingIterator<Token> tokens;
    private Token token;

    private static final ImmutableSet<TokenType> termH = ImmutableSet.of(
            SYMBOL, STRING, NUMBER, TRUE, FALSE, NULL, BRACKET_OPEN,
            BRACE_OPEN, PAR_OPEN);

    private final String filename;
    private final List<Error> errors = new ArrayList<>();

    public static MaybeError<Block> read(java.io.Reader input, String filename) throws
            IOException {
        return new Reader(filename).parse(input);
    }

    public static MaybeError<Block> read(String code) throws IOException {
        return read(new StringReader(code), "<input>");
    }

    private Reader(String filename) {
        this.filename = filename;
    }

    private MaybeError<Block> parse(java.io.Reader input) throws IOException {
        MaybeError<List<Token>> tokensOrErrors = Lexer.readTokens(input, filename);
        tokens = Iterators.peekingIterator(tokensOrErrors.get().iterator());
        token = tokens.next();
        if (errors.isEmpty())
            return succeed(block());
        else
            return fail(errors);
    }

    private Block block() throws IOException {
        Block block = new Block(token.position);
        while (token.type != EOF && token.type != DEDENT) {
            block.add(expression(true, true));
        }
        return block;
    }

    private Expression expression(boolean withBlock, boolean colonAccepted)
            throws IOException {
        if (token.type == OPERATOR) { // Single operator can be used as a symbol
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            return op;
        }
        Expression first = combination();
        while (token.type == OPERATOR) {
            if (isColon()
                    && (tokens.peek().type == LINEEND || !colonAccepted))
                break;
            Combination exp = new Combination(token.position);
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            Expression second = combination();
            exp.add(op);
            exp.add(first);
            exp.add(second);
            first = exp;
        }
        if (token.type == LINEEND)
            token = tokens.next();
        else if (withBlock && isColon()) {
            token = tokens.next(); // :
            token = tokens.next(); // \n
            token = tokens.next(); // INDENT FIXME: Add checks
            Block block = block();
            token = tokens.next(); // DEDENT FIXME: Add checks
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
        token = tokens.next(); // [
        if (token.type == BRACKET_CLOSE) { // Empty list
            token = tokens.next(); // ]
            return list;
        }
        // Non-empty list
        list.add(expression(false, true));
        while (token.type != TokenType.BRACKET_CLOSE) {
            token = tokens.next(); // ,
            list.add(expression(false, true));
        }
        token = tokens.next(); // ]
        return list;
    }

    private SMap map() throws IOException {
        SMap map = new SMap(token.position);
        token = tokens.next(); // {
        if (token.type == BRACE_CLOSE) { // Empty map
            token = tokens.next(); // }
            return map;
        }
        // Non-empty map
        keyValue(map);
        while (token.type != TokenType.BRACE_CLOSE) {
            token = tokens.next(); // ,
            keyValue(map);
        }
        token = tokens.next(); // }
        return map;
    }

    private void keyValue(SMap map) throws IOException {
        Expression key = expression(false, false);
        token = tokens.next(); // :
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
            token = tokens.next();
            break;
        case NUMBER:
            sexp = new SNumber(token.doubleValue, token.position);
            token = tokens.next();
            break;
        case TRUE:
            sexp = new SBoolean(true, token.position);
            token = tokens.next();
            break;
        case FALSE:
            sexp = new SBoolean(false, token.position);
            token = tokens.next();
            break;
        case PAR_OPEN:
            token = tokens.next(); // (
            sexp = expression(false, true);
            token = tokens.next(); // )
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
        token = tokens.next();
        while (token.type == DOT) {
            tokens.next(); // .
            name.add(token.stringValue);
            tokens.next();
        }
        return new Symbol(name.toArray(new String[name.size()]), position);
    }

    private boolean isColon() {
        return token.type == OPERATOR && token.stringValue.equals(":");
    }
}
