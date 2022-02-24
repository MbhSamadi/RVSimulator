package ir.ac.ut.ece.rv.state.central;

import ir.ac.ut.ece.rv.state.LocalState;
import ir.ac.ut.ece.rv.state.MessageCall;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;

import java.util.ArrayList;
import java.util.List;

import static ir.ac.ut.ece.rv.state.central.CentralMsgsrvDeclaration.DECLARE_METHOD;

public class CentralState extends LocalState {
    private List<DeclareMessageMeta> claims;

    public CentralState(ReactiveClassDeclaration associatedActor) {
        super(new CentralClassDeclaration(), new MainRebecDefinition());
        claims = new ArrayList<>();
    }

    @Override
    protected void loadMethodBody(MessageCall messageCall) {
        currentExecutingMethod = messageCall.getMethodName();
        vectorClock.update(messageCall.getVectorClock());
        MsgsrvDeclaration methodDeclaration = findMethod(messageCall.getMethodName());
        if (!(methodDeclaration instanceof CentralMsgsrvDeclaration))
            throw new RuntimeException("Illegal method '" + messageCall.getMethodName() + "' found in central");

        if (DECLARE_METHOD.equals(messageCall.getMethodName())) {
            DeclareMessageMeta meta = (DeclareMessageMeta) messageCall.getArgument("meta");
            claimMethod(meta);
        } else {
            throw new RuntimeException("Illegal method '" + messageCall.getMethodName() + "' found in central");
        }

    }

    private void claimMethod(DeclareMessageMeta meta) {
        claims.add(meta);
        System.out.println("CENTRAL MONITOR");
        meta.print();
        System.out.println("number of calls: " + claims.size());
    }

}
