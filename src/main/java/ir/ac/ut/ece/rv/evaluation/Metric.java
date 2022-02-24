package ir.ac.ut.ece.rv.evaluation;

import ir.ac.ut.ece.rv.state.GlobalState;

public interface Metric {
    void measure(GlobalState globalState);
}
