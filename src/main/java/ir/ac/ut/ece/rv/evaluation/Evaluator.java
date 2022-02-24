package ir.ac.ut.ece.rv.evaluation;

import ir.ac.ut.ece.rv.state.GlobalState;

import java.util.List;

public class Evaluator {
    private List<Metric> metrics;

    public void evaluate(GlobalState state) {
        metrics.forEach(metric -> metric.measure(state));
    }
}
