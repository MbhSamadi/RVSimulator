package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;

import java.util.HashMap;
import java.util.Map;

public class ExecutorHandler {
    private Map<Class, StatementExecutor> executorMap;

    public ExecutorHandler() {
        executorMap = new HashMap<>();
        executorMap.put(BinaryExpression.class, new BinaryExpressionExecutor());
        executorMap.put(FieldDeclaration.class, new FieldDeclarationExecutor());
        executorMap.put(ConditionalStatement.class, new ConditionalStatementExecutor());
        executorMap.put(BlockStatement.class, new BlockStatementExecutor());
        executorMap.put(DotPrimary.class, new DotPrimaryExecutor());
    }

    public GlobalState exec(Statement statement, GlobalState state, String actorName) {
        return executorMap.get(statement.getClass()).execAndInc(statement, state, actorName);
    }
}
