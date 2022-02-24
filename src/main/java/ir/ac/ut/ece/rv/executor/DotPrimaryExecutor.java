package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.Value;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;

import java.util.List;
import java.util.stream.Collectors;

public class DotPrimaryExecutor extends StatementExecutor<DotPrimary> {
    @Override
    public GlobalState exec(DotPrimary statement, GlobalState globalState, String actorName) {
        TermPrimary leftTerm = (TermPrimary) statement.getLeft();
        TermPrimary rightTerm = (TermPrimary) statement.getRight();
        String calledInstance = leftTerm.getName();
        String calledMethod = rightTerm.getName();

        String calledActor;
        if (calledInstance.equals("self")) {
            calledActor = actorName;
        } else {
            calledActor = globalState.getBinding(actorName, calledInstance);
        }
        String executingMethodName = globalState.getExecutingMethodName(actorName);
        List<Value> arguments = rightTerm
                .getParentSuffixPrimary()
                .getArguments()
                .stream()
                .map(argument -> ExpressionEvaluator.evaluate(argument, globalState, actorName))
                .collect(Collectors.toList());
        globalState.addMessageCall(actorName, executingMethodName, calledActor, calledMethod, arguments);
        globalState.addRegularMessageCall(actorName, executingMethodName, calledActor, calledMethod);
        return globalState;

    }
}
