import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class If {

    private static String BASE_PATH = "src/test/resources/if";
    private static String ACTOR_NAME = "obj";

    @Test
    public void testSingleIf() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/if.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();

        PrimitiveValue insideIf = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "insideIf");
        Assert.assertEquals(true, insideIf.getContent());
    }

    @Test
    public void testIfElse() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/if-else.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();

        PrimitiveValue insideElse = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "insideElse");
        Assert.assertEquals(true, insideElse.getContent());
    }

    @Test
    public void testNestedIfElse() {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/if-else-nested.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        GlobalState state = simulator.getState();

        PrimitiveValue insideElseOfElseIf = (PrimitiveValue) state.getVariableValue(ACTOR_NAME, "insideElseOfElseIf");
        Assert.assertEquals(true, insideElseOfElseIf.getContent());
    }


}
