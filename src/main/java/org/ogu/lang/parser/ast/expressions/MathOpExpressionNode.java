package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.OperatorNode;

/**
 * Created by ediaz on 11/2/16.
 */
public class MathOpExpressionNode extends BinaryOpExpressionNode {

    public MathOpExpressionNode(OperatorNode op, ExpressionNode left, ExpressionNode right) {
        super(op, left, right);
        this.operator = Operator.fromSymbol(op.getName());
    }


    private Operator operator;

    public Operator getOperator() {
        return operator;
    }


    public enum Operator {
        MULTIPLICATION("*"),
        DIVISION("/"),
        FLOOR_DIVISION("//"),
        SUM("+"),
        SUBTRACTION("-");

        private String symbol;

        private Operator(String symbol) {
            this.symbol = symbol;
        }

        public static Operator fromSymbol(String symbol) {
            for (Operator operator : Operator.values()) {
                if (operator.symbol.equals(symbol)) {
                    return operator;
                }
            }
            throw new IllegalArgumentException(symbol);
        }
    }

}
