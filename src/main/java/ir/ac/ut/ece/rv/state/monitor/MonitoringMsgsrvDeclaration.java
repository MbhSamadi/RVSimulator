package ir.ac.ut.ece.rv.state.monitor;

import com.sun.tools.javac.util.List;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryPrimitiveType;

public class MonitoringMsgsrvDeclaration extends MsgsrvDeclaration {

    public final static String MAIN_METHOD = "main";
    public final static String REGULAR_MESSAGE = "receive_regular_message";
    public final static String MONITORING_MESSAGE = "receive_monitoring_message";

    static MonitoringMsgsrvDeclaration forMain() {
        MonitoringMsgsrvDeclaration result = new MonitoringMsgsrvDeclaration();
        result.name = MAIN_METHOD;
        result.block = new BlockStatement();
        return result;
    }

    static MonitoringMsgsrvDeclaration forRegularMessage() {
        OrdinaryPrimitiveType stringType = new OrdinaryPrimitiveType();
        stringType.setName("String");

        FormalParameterDeclaration caller = new FormalParameterDeclaration();
        caller.setName("caller");
        caller.setType(stringType);

        FormalParameterDeclaration callee = new FormalParameterDeclaration();
        callee.setName("callee");
        callee.setType(stringType);

        FormalParameterDeclaration methodName = new FormalParameterDeclaration();
        methodName.setName("methodName");
        methodName.setType(stringType);

        MonitoringMsgsrvDeclaration result = new MonitoringMsgsrvDeclaration();
        result.name = REGULAR_MESSAGE;
        result.formalParameters = List.of(callee, caller, methodName);
        result.block = new BlockStatement();
        return result;
    }

    static MonitoringMsgsrvDeclaration forMonitoringMessage() {
        OrdinaryPrimitiveType stringType = new OrdinaryPrimitiveType();
        stringType.setName("String");

        FormalParameterDeclaration meta = new FormalParameterDeclaration();
        meta.setName("meta");
        meta.setType(stringType);

        MonitoringMsgsrvDeclaration result = new MonitoringMsgsrvDeclaration();
        result.name = MONITORING_MESSAGE;
        result.formalParameters = List.of(meta);
        result.block = new BlockStatement();
        return result;
    }

}
