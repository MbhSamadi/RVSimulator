package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConditionalStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;

public class ConditionalStatementExecutor extends StatementExecutor<ConditionalStatement> {

    @Override
    public GlobalState exec(ConditionalStatement statement, GlobalState globalState, String actorName) {
        PrimitiveValue value = (PrimitiveValue) ExpressionEvaluator.evaluate(statement.getCondition(), globalState, actorName);
        assert value != null && value.getContent() instanceof Boolean;
        Boolean condition = (Boolean) value.getContent();
        if (condition) {
            globalState.insertStatement(statement.getStatement(), actorName);
        } else {
            Statement elseStatement = statement.getElseStatement();
            if (elseStatement == null)
                return globalState;
            if (elseStatement instanceof ConditionalStatement)
                return exec((ConditionalStatement) elseStatement, globalState, actorName);
            globalState.insertStatement(elseStatement, actorName);
        }
        return globalState;
    }
}
