import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class Expressions {
    private static final String BASE_PATH = "src/test/resources/expressions";
    private static final String ACTOR_NAME = "obj";

    @Test
    public void simpleArithmetic() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/simpleArithmetic.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();
        PrimitiveValue sumResult = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "sumResult");
        PrimitiveValue minusResult = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "minusResult");
        PrimitiveValue multResult = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "multResult");
        PrimitiveValue divResult = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "divResult");
        Assert.assertEquals(30, sumResult.getContent());
        Assert.assertEquals(20, minusResult.getContent());
        Assert.assertEquals(125, multResult.getContent());
        Assert.assertEquals(5, divResult.getContent());
    }

    @Test
    public void complexArithmetic() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/complexArithmetic.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();
        PrimitiveValue result = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "result");
        Assert.assertEquals(-312495, result.getContent());
    }

    @Test
    public void complexLogical() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/complexLogical.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();
        state.getVariableValue(ACTOR_NAME, "result");
        PrimitiveValue result = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "result");
        Assert.assertEquals(true, result.getContent());
    }


}
