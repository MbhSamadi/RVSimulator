package ir.ac.ut.ece.rv;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.ac.ut.ece.rv.simulation.Simulator;
import ir.ac.ut.ece.rv.utility.RebecaCompilerUtility;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

public class Main {
    private final static String BASE_PATH = "src/main/resources/input";

    public static void main(String[] args) throws JsonProcessingException {
        RebecaModel rebecaModel = RebecaCompilerUtility.getRebecaModelOf(BASE_PATH + "/output.rebec");

        Simulator simulator = new Simulator(rebecaModel);
        simulator.run();
        simulator.report();
    }

}
