package ir.ac.ut.ece.rv.state.monitor;

import com.sun.tools.javac.util.List;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;

public class MonitoringClassDeclaration extends ReactiveClassDeclaration {
    MonitoringClassDeclaration(String instanceName) {
        name = instanceName;
        this.msgsrvs = List.of(
                MonitoringMsgsrvDeclaration.forMain(),
                MonitoringMsgsrvDeclaration.forRegularMessage(),
                MonitoringMsgsrvDeclaration.forMonitoringMessage()
        );
    }
}
