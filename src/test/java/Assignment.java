import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class Assignment {

    private static String BASE_PATH = "src/test/resources";
    private static String ACTOR_NAME = "obj";

    @Test
    public void testAssignments() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/assignment.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();

        PrimitiveValue i5 = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "i5");
        Assert.assertEquals(5, i5.getContent());

        PrimitiveValue i11 = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "i11");
        Assert.assertEquals(11, i11.getContent());

        PrimitiveValue b0 = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "b0");
        Assert.assertEquals(false, b0.getContent());
    }


}
