package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.Value;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;

public class FieldDeclarationExecutor extends StatementExecutor<FieldDeclaration> {

    @Override
    public GlobalState exec(FieldDeclaration statement, GlobalState globalState, String actorName) {
        statement
                .getVariableDeclarators()
                .forEach(variableDeclarator -> {
                    String variableName = variableDeclarator.getVariableName();
                    globalState.addLocalVariable(actorName, variableName);
                    if (variableDeclarator.getVariableInitializer() != null) {
                        Expression expression = ((OrdinaryVariableInitializer) variableDeclarator.getVariableInitializer()).getValue();
                        Value value = ExpressionEvaluator.evaluate(expression, globalState, actorName);
                        globalState.setVariableValue(actorName, variableName, value);
                    }
                });
        return globalState;
    }
}
