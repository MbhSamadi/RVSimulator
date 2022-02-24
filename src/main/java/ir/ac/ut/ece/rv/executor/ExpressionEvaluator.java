package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.state.Value;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ExpressionEvaluator {
    public static Value evaluate(Expression expression, GlobalState globalState, String actorName) {
        if (expression instanceof Literal) {
            return PrimitiveValue.of((Literal) expression);
        } else if (expression instanceof TermPrimary) {
            return globalState.getVariableValue(actorName, ((TermPrimary) expression).getName());
        } else if (expression instanceof BinaryExpression) {
            return evaluateBinaryExpression((BinaryExpression) expression, globalState, actorName);
        } else if (expression instanceof NonDetExpression) {
            return evaluateNonDetExpression((NonDetExpression) expression, globalState, actorName);
        }
        return null;
    }

    private static Value evaluateNonDetExpression(NonDetExpression expression, GlobalState globalState, String actorName) {
        List<Value> values = expression
                .getChoices()
                .stream()
                .map(it -> evaluate(it, globalState, actorName))
                .collect(Collectors.toList());
        int randomIndex = new Random().nextInt(values.size());
        return values.get(randomIndex);
    }

    private static Value evaluateBinaryExpression(BinaryExpression binaryExpression, GlobalState globalState, String actorName) {
        PrimitiveValue leftValue = (PrimitiveValue) evaluate(binaryExpression.getLeft(), globalState, actorName);
        PrimitiveValue rightValue = (PrimitiveValue) evaluate(binaryExpression.getRight(), globalState, actorName);
        if (rightValue == null || leftValue == null)
            return null;

        //always not assignment
        switch (binaryExpression.getOperator()) {
            case "+":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) + ((Integer) rightValue.getContent()));
            case "-":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) - ((Integer) rightValue.getContent()));
            case "*":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) * ((Integer) rightValue.getContent()));
            case "/":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) / ((Integer) rightValue.getContent()));
            case "&&":
                return new PrimitiveValue<>(((Boolean) leftValue.getContent()) && ((Boolean) rightValue.getContent()));
            case "||":
                return new PrimitiveValue<>(((Boolean) leftValue.getContent()) || ((Boolean) rightValue.getContent()));
            case ">":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) > ((Integer) rightValue.getContent()));
            case ">=":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) >= ((Integer) rightValue.getContent()));
            case "<":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) < ((Integer) rightValue.getContent()));
            case "<=":
                return new PrimitiveValue<>(((Integer) leftValue.getContent()) <= ((Integer) rightValue.getContent()));
            case "==":
                return new PrimitiveValue<>(leftValue.getContent() == rightValue.getContent());
            case "!=":
                return new PrimitiveValue<>(leftValue.getContent() != rightValue.getContent());
            default:
                return null;
        }
    }
}
