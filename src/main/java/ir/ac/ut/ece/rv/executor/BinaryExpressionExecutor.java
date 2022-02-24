package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.Value;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;

public class BinaryExpressionExecutor extends StatementExecutor<BinaryExpression> {

    @Override
    public GlobalState exec(BinaryExpression statement, GlobalState globalState, String actorName) {
        //always assignment
        Value rightValue = ExpressionEvaluator.evaluate(statement.getRight(), globalState, actorName);
        TermPrimary left = (TermPrimary) statement.getLeft();
        globalState.setVariableValue(actorName, left.getName(), rightValue);
        return globalState;
    }


}
