package ir.ac.ut.ece.rv.state;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryPrimitiveType;

public class PrimitiveValue<T> extends Value {
    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public PrimitiveValue(T input) {
        content = input;
    }


    public static PrimitiveValue of(Literal literal) {
        switch (((OrdinaryPrimitiveType) literal.getType()).getName()) {
            case ("boolean"):
                return new PrimitiveValue<>(Boolean.valueOf(literal.getLiteralValue()));
            case ("byte"):
            case ("int"):
                return new PrimitiveValue<>(Integer.valueOf(literal.getLiteralValue()));
            default:
                return null;
        }
    }
}
