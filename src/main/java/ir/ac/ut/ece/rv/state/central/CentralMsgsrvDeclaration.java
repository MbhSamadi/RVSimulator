package ir.ac.ut.ece.rv.state.central;

import com.sun.tools.javac.util.List;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BlockStatement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryPrimitiveType;

public class CentralMsgsrvDeclaration extends MsgsrvDeclaration {

    public final static String DECLARE_METHOD = "declare";

    static CentralMsgsrvDeclaration forDeclare() {
        OrdinaryPrimitiveType stringType = new OrdinaryPrimitiveType();
        stringType.setName("String");

        FormalParameterDeclaration meta = new FormalParameterDeclaration();
        meta.setName("meta");
        meta.setType(stringType);

        CentralMsgsrvDeclaration result = new CentralMsgsrvDeclaration();
        result.name = DECLARE_METHOD;
        result.formalParameters = List.of(meta);
        result.block = new BlockStatement();
        return result;
    }

}
