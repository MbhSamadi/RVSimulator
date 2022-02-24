package ir.ac.ut.ece.rv.simulation;

import ir.ac.ut.ece.rv.state.GlobalState;

import java.util.List;
import java.util.Random;

public class DefaultScenario implements Scenario {

    @Override
    public String nextActor(GlobalState globalState) {
        List<String> candidates = globalState.getCandidateActors();
        if (candidates.isEmpty())
            return null;
        int index = new Random().nextInt(candidates.size());
        return candidates.get(index);
    }
}
