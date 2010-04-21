package xhl.core;

import java.io.IOException;

import xhl.core.Token.TokenType;

public class Reader {
    private Lexer lexer;
    private Token token;

    public CodeList read(java.io.Reader input) throws IOException {
        lexer = new Lexer(input);
        token = lexer.nextToken();
        return program();
    }

    private CodeList program() throws IOException {
        CodeList lists = new CodeList(token.position);
        while (token != null && token.type == TokenType.PAR_OPEN) {
            lists.add(list());
        }
        return lists;
    }

    private CodeList list() throws IOException {
        CodeList list = new CodeList(token.position);
        token = lexer.nextToken(); // (
        while (token.type != TokenType.PAR_CLOSE) {
            list.add(sexp());
        }
        token = lexer.nextToken(); // )
        return list;
    }

    private Object sexp() throws IOException {
        Object sexp = null;
        switch (token.type) {
        case SYMBOL:
            sexp = new Symbol(token.stringValue, token.position);
            token = lexer.nextToken();
            break;
        case STRING:
            sexp = token.stringValue;
            token = lexer.nextToken();
            break;
        case NUMBER:
            sexp = token.doubleValue;
            token = lexer.nextToken();
            break;
        case PAR_OPEN:
            sexp = list();
            break;
        }
        return sexp;
    }
}
