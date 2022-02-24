package ir.ac.ut.ece.rv.state.central;

import com.sun.tools.javac.util.List;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;

public class CentralClassDeclaration extends ReactiveClassDeclaration {

    public static String NAME = "central";

    public CentralClassDeclaration() {
        this.name = NAME;
        this.msgsrvs = List.of(
                CentralMsgsrvDeclaration.forDeclare()
        );
    }
}
