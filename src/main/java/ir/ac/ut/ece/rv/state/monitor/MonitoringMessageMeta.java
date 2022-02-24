package ir.ac.ut.ece.rv.state.monitor;

import ir.ac.ut.ece.rv.state.Value;
import ir.ac.ut.ece.rv.state.VectorClock;

import java.util.List;

public class MonitoringMessageMeta extends Value {
    private MonitoringMessageType type;
    private VectorClock vectorClock;
    private Transition originTransition;
    private Transition targetTransition;
    private List<History> history;

    public MonitoringMessageMeta(
            MonitoringMessageType type,
            VectorClock vectorClock,
            Transition originTransition,
            Transition targetTransition,
            List<History> history
    ) {
        this.type = type;
        this.vectorClock = vectorClock;
        this.originTransition = originTransition;
        this.targetTransition = targetTransition;
        this.history = history;
    }

    public MonitoringMessageType getType() {
        return type;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Transition getOriginTransition() {
        return originTransition;
    }

    public Transition getTargetTransition() {
        return targetTransition;
    }

    public List<History> getHistory() {
        return history;
    }
}

