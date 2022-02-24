import com.fasterxml.jackson.core.JsonProcessingException;
import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.state.LocalState;
import ir.ac.ut.ece.rv.state.VectorClock;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.junit.Assert;
import org.junit.Test;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VC {
    private static String BASE_PATH = "src/test/resources";

    @Test
    public void declarationAndAssignment() throws JsonProcessingException {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/vc.rebeca");
        Simulator simulator = new Simulator(rebecaModel);
        simulator.run();

        Map<String, LocalState> states = simulator.getState().getStates();
        states.forEach((instanceName, localState) -> {
            VectorClock vectorClock = localState.getCopyOfVectorClock();
            Integer instanceClock = vectorClock.getInstanceClock().get(instanceName);
            List<Integer> illegalClocks = states
                    .values()
                    .stream()
                    .map(it -> it.getCopyOfVectorClock().getInstanceClock().get(instanceName))
                    .filter(it -> it > instanceClock)
                    .collect(Collectors.toList());
            Assert.assertEquals(0, illegalClocks.size());
        });
    }
}
