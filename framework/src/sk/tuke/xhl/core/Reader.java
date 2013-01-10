/*
 * XHL - Extensible Host Language
 * Copyright 2012-2013 Sergej Chodarev
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

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.union;
import static sk.tuke.xhl.core.MaybeError.fail;
import static sk.tuke.xhl.core.MaybeError.succeed;
import static sk.tuke.xhl.core.Token.TokenType.*;

/**
 * XHL parser
 *
 * Grammar:
 *
 * <pre>
 *   block       ::= { expression LINE_END | expression-with-block }
 *   expression  ::= combination { operator combination } | operator
 *   expression-with-block ::= combination ':' LINE_END INDENT block DEDENT
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

    private static final Set<TokenType> literalH = set(SYMBOL, BRACKET_OPEN,
            BRACE_OPEN, STRING, NUMBER, TRUE, FALSE, NULL);
    private static final Set<TokenType> termH = set(literalH, PAR_OPEN);
    private static final Set<TokenType> expressionH = termH;
    private static final Set<TokenType> blockH = expressionH;

    private final String filename;
    private final List<Error> errors = new ArrayList<>();

    public static MaybeError<Block> read(File file) throws IOException {
        InputStreamReader input = new InputStreamReader(
                new FileInputStream(file));
        return new Reader(file.getName()).parse(input);
    }

    public static MaybeError<Block> read(java.io.Reader input, String filename)
            throws IOException {
        return new Reader(filename).parse(input);
    }

    public static MaybeError<Block> read(String code) throws IOException {
        return read(new StringReader(code), "<input>");
    }

    private Reader(String filename) {
        this.filename = filename;
    }

    private MaybeError<Block> parse(java.io.Reader input) throws IOException {
        // Get results from lexical analyzer
        MaybeError<Iterator<Token>> tokensOrErrors =
                Lexer.readTokens(input, filename);
        if(!tokensOrErrors.hasResult())
            return fail(tokensOrErrors.getErrors());
        if (!tokensOrErrors.succeed())
            errors.addAll(tokensOrErrors.getErrors());
        tokens = Iterators.peekingIterator(tokensOrErrors.get());

        token = tokens.next();
        Block expressions = block(set(EOF));
        if (errors.isEmpty())
            return succeed(expressions);
        else
            return fail(errors);
    }

    private Block block(Set<TokenType> keys) throws IOException {
        Block block = new Block(token.position);
        while (token.type != EOF && token.type != DEDENT) {
            block.add(topLevelExpression(keys));
        }
        return block;
    }

    /**
     * Syntax analyzer for expression at a top level of the block.
     *
     * @param keys Key symbols for error recovery.
     * @return Read expression.
     */
    private Expression topLevelExpression(Set<TokenType> keys)
            throws IOException {
        if (token.type == OPERATOR) { // Single operator can be used as a symbol
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            return op;
        }
        Set<TokenType> k = set(blockH, SYMBOL, LINE_END, INDENT, DEDENT);
        Expression first = combination(union(k, keys));
        while (token.type == OPERATOR) {
            if (isColon() && (tokens.peek().type == LINE_END))
                break; // Colon is not an operator here, but introduces a block
            Combination exp = new Combination(token.position);
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            Expression second = combination(union(k, keys));
            exp.add(op);
            exp.add(first);
            exp.add(second);
            first = exp;
        }
        if (isColon() // Check if next line is indented (colon may be missing)
                || (token.type == LINE_END && tokens.peek().type == INDENT)) {
            if (isColon())
                token = tokens.next(); // :
            else
                error("Colon before a block missing.", set(keys, blockH,
                        LINE_END, INDENT, DEDENT));
            checkAndRead(LINE_END, "Line end before block missing.",
                    set(keys, blockH, INDENT, DEDENT));
            checkAndRead(INDENT, "Block must be indented.",
                    set(keys, blockH, DEDENT));
            Block block = block(set(keys, DEDENT));
            checkAndRead(DEDENT, "End of block expected.", keys);
            // If block header is not a combination -- create combination
            if (!(first instanceof Combination)) {
                Combination head = new Combination(first.getPosition());
                head.add(first);
                first = head;
            }
            ((Combination) first).add(block);
        } else {
            checkAndRead(LINE_END, "Unexpected symbol.",
                    keys);
        }
        return first;
    }

    /**
     * Syntax analyzer for expression
     *
     * @param colonAccepted Can the expression contain the colon operator
     *                      directly? For example inside maps it can not.
     * @param keys          Key symbols for error recovery.
     * @return Read expression.
     */
    private Expression expression(boolean colonAccepted,
                                  Set<TokenType> keys)
            throws IOException {
        if (token.type == OPERATOR) { // Single operator can be used as a symbol
            if (isColon() && !colonAccepted) {
                error("Colon operator is not allowed here.", keys);
                return null;
            }
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            return op;
        }
        Set<TokenType> k = set(OPERATOR);
        Expression first = combination(union(k, keys));
        while (token.type == OPERATOR) {
            if (isColon() && !colonAccepted) {
                // Colon is not an operator here, but introduces a block or
                // value in a map
                break;
            }
            Combination exp = new Combination(token.position);
            Symbol op = new Symbol(token.stringValue, token.position);
            token = tokens.next();
            Expression second = combination(union(k, keys));
            exp.add(op);
            exp.add(first);
            exp.add(second);
            first = exp;
        }
        return first;
    }

    private Expression combination(Set<TokenType> keys) throws IOException {
        Combination list = new Combination(token.position);
        while (termH.contains(token.type)) {
            list.add(term(keys));
        }
        if (list.size() == 1)
            return list.head();
        else
            return list;
    }

    private SList list(Set<TokenType> keys) throws IOException {
        SList list = new SList(token.position);
        token = tokens.next(); // [
        if (token.type == BRACKET_CLOSE) { // Empty list
            token = tokens.next(); // ]
            return list;
        }
        // Non-empty list
        list.add(expression(true, set(keys, BRACKET_CLOSE)));
        while (token.type == TokenType.COMMA) {
            token = tokens.next(); // ,
            list.add(expression(true, set(keys, COMMA, BRACKET_CLOSE)));
        }
        checkAndRead(BRACKET_CLOSE, "Closing bracket missing.", keys);
        return list;
    }

    private SMap map(Set<TokenType> keys) throws IOException {
        SMap map = new SMap(token.position);
        token = tokens.next(); // {
        if (token.type == BRACE_CLOSE) { // Empty map
            token = tokens.next(); // }
            return map;
        }
        Set<TokenType> kc = set(expressionH, COMMA, OPERATOR);
        Set<TokenType> k = set(kc, BRACE_CLOSE);
        // Non-empty map
        keyValue(map, union(k, keys));
        while (kc.contains(token.type)) {
            checkAndRead(COMMA, "Comma missing", union(k, keys));
            keyValue(map, union(k, keys));
        }
        checkAndRead(BRACE_CLOSE, "Closing brace missing.", keys);
        return map;
    }

    private void keyValue(SMap map, Set<TokenType> keys) throws IOException {
        Expression key = expression(false, set(keys, expressionH, OPERATOR));
        if (isColon())
            token = tokens.next(); // :
        else
            error("Expected colon ':'.", union(keys, expressionH));
        Expression value = expression(false, keys);
        map.put(key, value);
    }

    private Expression term(Set<TokenType> keys) throws IOException {
        Expression sexp = null;
        switch (token.type) {
        case SYMBOL:
            sexp = symbol(keys);
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
            sexp = expression(true, set(keys, PAR_CLOSE));
            checkAndRead(PAR_CLOSE, "Closing parenthesis expected.", keys);
            break;
        case BRACKET_OPEN:
            sexp = list(keys);
            break;
        case BRACE_OPEN:
            sexp = map(keys);
            break;
        }
        return sexp;
    }

    private Expression symbol(Set<TokenType> keys) {
        Position position = token.position;
        List<String> name = newArrayList(token.stringValue);
        token = tokens.next();
        while (token.type == DOT) {
            token = tokens.next(); // .
            String component = token.stringValue;
            checkAndRead(SYMBOL, "Symbol expected", keys);
            name.add(component);
        }
        return new Symbol(name.toArray(new String[name.size()]), position);
    }

    private boolean isColon() {
        return token.type == OPERATOR && token.stringValue.equals(":");
    }

    private void checkAndRead(TokenType type, String msg, Set<TokenType> keys) {
        if (token.type == type)
            token = tokens.next(); // \n
        else
            error(msg, keys);
    }

    /**
     * Report error end skip tokens while one of the key tokens is not found.
     * @param msg  Error message.
     * @param keys A set of recovery tokens. At these tokens it is possible
     *             to recover syntax analysis process.
     */
    private void error(String msg, Set<TokenType> keys) {
        errors.add(new Error(token.position, msg));
        while (!keys.contains(token.type)) {
            token = tokens.next();
        }
    }

    private static Set<TokenType> set(TokenType... types) {
        return ImmutableSet.copyOf(types);
    }

    private static Set<TokenType> set(Set<TokenType> s1, TokenType... types) {
        return union(set(types), s1);
    }

    private static Set<TokenType> set(Set<TokenType> s1, Set<TokenType> s2,
                                      TokenType... types) {
        return union(set(types), union(s1, s2));
    }
}
