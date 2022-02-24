package ir.ac.ut.ece.rv.state.monitor;

import ir.ac.ut.ece.rv.state.VectorClock;

public class TransitionClock {
    private Transition t;
    private VectorClock vc;

    public TransitionClock(Transition t, VectorClock vc) {
        this.t = t;
        this.vc = vc;
    }

    @Override
    public String toString() {
        return "{t: " + t.toString() + ", vc: " + vc.toString() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransitionClock))
            return false;
        return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
