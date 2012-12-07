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

import sk.tuke.xhl.core.elements.Position;

/**
 * Token from lexical analysis
 */
public class Token {

    public static enum TokenType {
        PAR_OPEN, PAR_CLOSE,
        BRACKET_OPEN, BRACKET_CLOSE,
        BRACE_OPEN, BRACE_CLOSE,
        COMMA, DOT, TRUE, FALSE, NULL,
        SYMBOL, OPERATOR, STRING, NUMBER,
        INDENT, DEDENT, LINEEND, EOF
    }

    public final TokenType type;
    public final double doubleValue;
    public final String stringValue;
    public final Position position;

    public Token(TokenType t, Position p) {
        this(t, 0, null, p);
    }

    public Token(TokenType t, double n, Position p) {
        this(t, n, null, p);
    }

    public Token(TokenType t, String s, Position p) {
        this(t, 0, s, p);
    }

    private Token(TokenType t, double n, String s, Position p) {
        type = t;
        doubleValue = n;
        stringValue = s;
        position = p;
    }
}
