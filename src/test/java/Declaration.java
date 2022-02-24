import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class Declaration {
    private static final String BASE_PATH = "src/test/resources";
    private static final String ACTOR_NAME = "obj";

    @Test
    public void declarationAndAssignment() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/declaration.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();

        PrimitiveValue a = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "a");
        Assert.assertEquals(2, a.getContent());

        state.getVariableValue(ACTOR_NAME, "noValue");

        PrimitiveValue c = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "c");
        Assert.assertEquals(7, c.getContent());
    }

    @Test(expected = RuntimeException.class)
    public void undeclaredVariable() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/declaration.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();
        state.getVariableValue(ACTOR_NAME, "undeclaredVariable");

    }

}
