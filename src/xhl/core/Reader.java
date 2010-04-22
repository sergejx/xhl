package xhl.core;

import java.io.IOException;

import xhl.core.Token.TokenType;
import xhl.core.elements.*;

/**
 * XHL parser
 *
 * Grammar:
 * <pre>
 *   program  ::= { codelist }
 *   codelist ::= '(' { sexp } ')'
 *   datalist ::= '[' { sexp } ']'
 *   sexp ::= symbol | string | number | codelist | datalist
 * </pre>
 *
 * @author Sergej Chodarev
 */
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
            lists.add(codelist());
        }
        return lists;
    }

    private CodeList codelist() throws IOException {
        CodeList list = new CodeList(token.position);
        token = lexer.nextToken(); // (
        while (token.type != TokenType.PAR_CLOSE) {
            list.add(sexp());
        }
        token = lexer.nextToken(); // )
        return list;
    }

    private DataList datalist() throws IOException {
        DataList list = new DataList(token.position);
        token = lexer.nextToken(); // [
        while (token.type != TokenType.BRACKET_CLOSE) {
            list.add(sexp());
        }
        token = lexer.nextToken(); // ]
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
            sexp = new LString(token.stringValue, token.position);
            token = lexer.nextToken();
            break;
        case NUMBER:
            sexp = new LNumber(token.doubleValue, token.position);
            token = lexer.nextToken();
            break;
        case PAR_OPEN:
            sexp = codelist();
            break;
        case BRACKET_CLOSE:
            sexp = datalist();
            break;
        }
        return sexp;
    }
}
