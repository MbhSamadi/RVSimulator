package ir.ac.ut.ece.rv.simulation;

import ir.ac.ut.ece.rv.executor.ExecutorHandler;
import ir.ac.ut.ece.rv.state.GlobalState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;

import java.util.HashSet;
import java.util.Set;

public class NextStateGenerator {
    private ExecutorHandler executorHandler = new ExecutorHandler();

    public GlobalState generate(GlobalState state, String instanceName) {
        Statement lastStatement = state.getLastStatement(instanceName);

        if (lastStatement != null) {
            return executorHandler.exec(lastStatement, state, instanceName);
        } else {
            state.addMessageBody(instanceName);
            return state;
        }
    }

    public GlobalState initialGlobalState(GlobalState state) {
        Set<String> instanceNames = new HashSet<>(state.getInstanceNames());
        for (String instanceName : instanceNames) {
            state = execConstructor(state, instanceName);
        }
        return state;
    }

    private GlobalState execConstructor(GlobalState state, String instanceName) {
        Statement statement;
        statement = state.getLastStatement(instanceName);
        while (statement != null) {
            state = executorHandler.exec(statement, state, instanceName);
            statement = state.getLastStatement(instanceName);
        }
        return state;
    }
}
