package com.example.kernlang.interpreter.frontend.parser;

import com.example.kernlang.interpreter.Interpreter;
import com.example.kernlang.interpreter.frontend.lexer.Token;
import com.example.kernlang.interpreter.frontend.lexer.TokenType;
import com.example.kernlang.interpreter.frontend.parser.expressions.*;
import com.example.kernlang.interpreter.frontend.parser.statements.ReturnStmt;
import com.example.kernlang.interpreter.frontend.parser.statements.Stmt;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    /** Parse functions **/

    private Stmt parseAssignment() {
        Stmt result = null;

        return result;
    }

    public Expr parseExpression() {
        return parseEquality();
    }

    private Expr parseEquality() {
        Expr expr = parseComparison();

        while (match(TokenType.TOK_BANG_EQUAL, TokenType.TOK_EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = parseComparison();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr parseComparison() {
        Expr expr = parseTerm();

        while (match(TokenType.TOK_PLUS, TokenType.TOK_MINUS)) {
            Token operator = previous();
            Expr right = parseTerm();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr parseTerm() {
        Expr expr = parseFactor();

        while (match(TokenType.TOK_STAR, TokenType.TOK_SLASH)) {
            Token operator = previous();
            Expr right = parseFactor();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr parseFactor() {
        return parseUnary();
    }

    private Expr parseUnary() {
        Expr e = null;

        switch (peek().tokenType()) {
            case TOK_UNIT, TOK_TRUE, TOK_FALSE, TOK_CHAR, TOK_NUMBER -> { return parseLiteral(); }
            case TOK_BANG, TOK_AMPERSAND, TOK_DOLLAR_SIGN -> {
                    Token operator = advance();
                    e = new UnaryExpr(operator, parseUnary());
            }
            case TOK_OPEN_CURLY -> { return parseRecord(); }
            case TOK_BACKSLASH -> { return parseFunctionLiteral(); }
        }

        return e;
    }

    private LiteralExpr parseLiteral() {
        return new LiteralExpr(advance());
    }

    private RecordExpr parseRecord() {
        advance(); // skip over open curly {

        RecordExpr result = new RecordExpr();

        while (match(TokenType.TOK_IDENTIFIER)) {
            String identifier = previous().lexeme();
            if (match(TokenType.TOK_LEFT_ARROW)) {
                result.addRecordField(identifier, parseExpression());
            } else {
                error(peek(), "leftarrow expected after identifier in record definition");
            }
        }

        if (!match(TokenType.TOK_CLOSE_CURLY))
            error(peek(), "no closing curly bracket at end of record definition");

        return result;
    }

    private FunctionLiteral parseFunctionLiteral() {
        advance(); // skip over backslash \

        FunctionLiteral result = new FunctionLiteral();

        // parse function parameters
        while (match(TokenType.TOK_IDENTIFIER)) result.addParameter(previous().lexeme());

        // expect "<- {" after parameters
        if (!match(TokenType.TOK_RIGHT_ARROW))
            error(peek(), "right arrow axpected after paramterlist of function literal");

        if (!match(TokenType.TOK_OPEN_CURLY))  error(peek(), "open urly bracket expected");

        // parse statements, when it finds a "}", it skips that token and stops parsing statements
        while (!match(TokenType.TOK_CLOSE_CURLY)) result.addStmt(parseStmt());

        return result;
    }

    private Stmt parseStmt() {
        Stmt result = null;
        switch (peek().tokenType()) {
            case TOK_RETURN -> {
                advance(); // skip over "return" keyword
                result = new ReturnStmt(parseExpression());
                break;
            }
        }

        return result;
    }



    /** HELPER FUNCTIONS **/

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().tokenType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().tokenType() == TokenType.TOK_EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token currentToken() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Interpreter.error(token.node(), token.line(), message);
        return new ParseError();
    }

    private static class ParseError extends RuntimeException {}
}
