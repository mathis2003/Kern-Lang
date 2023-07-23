package com.example.kernlang.compiler.parser.expressions;

import com.example.kernlang.compiler.ast_visitors.ExprVisitor;

import java.util.ArrayList;
import java.util.Arrays;

public class ComparisonTerm extends ExtendibleASTNode {

    public ComparisonTerm() {
        super(
                new ArrayList<>(Arrays.asList(
                        AddSub::new,
                        Term::new
                )),
                "ComparisonTerm failed to parse"
        );
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitComparisonTerm(this);
    }

}
