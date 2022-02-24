package ir.ac.ut.ece.rv.executor;

import ir.ac.ut.ece.rv.state.GlobalState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;

public class BlockStatementExecutor extends StatementExecutor<BlockStatement> {
    @Override
    public GlobalState exec(BlockStatement statement, GlobalState globalState, String actorName) {
        globalState.insertStatements(statement.getStatements(), actorName);
        return globalState;
    }
}
