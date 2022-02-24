package ir.ac.ut.ece.rv.simulation;

import ir.ac.ut.ece.rv.state.GlobalState;

public class Scheduler {

    private Scenario scenario;

    Scheduler() {
        scenario = new DefaultScenario();
//        scenario = new HistoryScenario();
    }

    public String select(GlobalState state) {
        return scenario.nextActor(state);
    }

}
