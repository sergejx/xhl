package xhl.core;

import xhl.core.Token.TokenType;
import static xhl.core.Token.TokenType.*;
import xhl.core.elements.CodeElement.CodePosition;

/**
 * Lexical analyzer for XHL
 *
 * @author Sergej Chodarev
 */
%%

%class Lexer
%type Token
%apiprivate

%unicode
%line
%column

%{
    private Token lastToken = null;
    private int indentLevel = 0;
    private int bracketLevel = 0;

    /**  Get next token from input stream */
    public Token nextToken() throws java.io.IOException {
        Token token;
        if (lastToken == null) {
            // Set initial indentation level
            token = yylex();
            indentLevel = yycolumn;  // TODO: Correctly count indentation level
            return token;
        } else if (lastToken.type == NEWLINE) {
            // Check indentation level of new line
            int level = yylength() - 1; // TODO: Correctly count indentation level
            if (level > indentLevel) {
                indentLevel = level;
                return token(INDENT);
            }
            if (level < indentLevel) {
                indentLevel = level;
                return token(DEDENT);
            }
        }
        // Check indentation level if token is EOF
        token = yylex();
        if (token == null && indentLevel > 0) {
            indentLevel = 0;
            return token(DEDENT); // First DEDENT then EOF
        } else
            return token;
    }

    private void bropen() {
        bracketLevel++;
        yybegin(INBRACKET);
    }
    private void brclose() {
        bracketLevel--;
        if (bracketLevel <= 0)
            yybegin(YYINITIAL);
    }

    /** Get position of current token */
    private CodePosition position() { // TODO filename
        return new CodePosition("input", yyline, yycolumn);
    }

    /* Helper functions for generation tokens */
    private Token token(TokenType type) {
        lastToken = new Token(type, position());
        return lastToken;
    }

    private Token stringToken(TokenType type, String str) {
        lastToken = new Token(type, str, position());
        return lastToken;
    }

    private Token numberToken(TokenType type, double num) {
        lastToken = new Token(type, num, position());
        return lastToken;
    }
%}

NewLine = \n|\r|\r\n
Space = [ \t]

%state INBRACKET

%%

"("             { bropen();  return token(PAR_OPEN); }
")"             { brclose(); return token(PAR_CLOSE); }
"["             { bropen();  return token(BRACKET_OPEN); }
"]"             { brclose(); return token(BRACKET_CLOSE); }
"{"             { bropen();  return token(BRACE_OPEN); }
"}"             { brclose(); return token(BRACE_CLOSE); }
","             { return token(COMMA); }
":"             { return token(COLON); }
true            { return token(TRUE); }
false           { return token(FALSE); }
none            { return token(NONE); }

[:letter:]([:letter:]|[:digit:]|_)*     { return stringToken(SYMBOL, yytext()); }

("!" | "$" | "%" | "&" | "*" | "+" | "." | "/" | "<" | "=" | ">" | "?" | "@"
 | \\ | "^" | "|" | "-" | "~")* {
    return stringToken(OPERATOR, yytext());
}

[+-]?[:digit:]+("."[:digit:]*)? {
    return numberToken(TokenType.NUMBER, Double.valueOf(yytext()));
}

\".*\" {
    return stringToken(TokenType.STRING, yytext().substring(1, yylength()-1));
}

<YYINITIAL>{NewLine} {Space}*    { return token(NEWLINE); }
<INBRACKET>{NewLine}      { /* ignore */ }
{Space}      { /* ignore */ }

";" .*  { /* comment */ }
