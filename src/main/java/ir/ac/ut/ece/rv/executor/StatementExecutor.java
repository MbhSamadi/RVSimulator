package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;

abstract class StatementExecutor<T extends Statement> {
    GlobalState execAndInc(T statement, GlobalState globalState, String actorName) {
        globalState.incrementVectorClock(actorName);
        return exec(statement, globalState, actorName);
    }

    abstract GlobalState exec(T statement, GlobalState globalState, String actorName);
}
