package ir.ac.ut.ece.rv.simulation;

import ir.ac.ut.ece.rv.state.GlobalState;

public interface Scenario {

    String nextActor(GlobalState globalState);

}
