import com.fasterxml.jackson.core.JsonProcessingException;
import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.PrimitiveValue;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class MessageCall {

    private static String BASE_PATH = "src/test/resources/message";

    @Test
    public void testSimple() throws JsonProcessingException {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/simple.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        simulator.run();
        GlobalState state = simulator.getState();

        PrimitiveValue history = (PrimitiveValue) state.getVariableValue("sender", "history");
        Assert.assertEquals(true, history.getContent());

        PrimitiveValue call = (PrimitiveValue) state.getVariableValue("receiver", "call");
        Assert.assertEquals(true, call.getContent());
    }

    @Test
    public void testArgument() throws JsonProcessingException {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/argument.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        simulator.run();
        GlobalState state = simulator.getState();

        PrimitiveValue history = (PrimitiveValue) state.getVariableValue("sender", "history");
        Assert.assertEquals(true, history.getContent());

        PrimitiveValue call = (PrimitiveValue) state.getVariableValue("receiver", "call");
        Assert.assertEquals(true, call.getContent());
    }

}
