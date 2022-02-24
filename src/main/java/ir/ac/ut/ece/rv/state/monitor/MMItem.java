package ir.ac.ut.ece.rv.state.monitor;

import ir.ac.ut.ece.rv.state.VectorClock;

public class MMItem {
    private Transition origin;
    private Transition target;
    private VectorClock vc;
    private String sender;

    public MMItem(Transition origin, Transition target, VectorClock vc, String sender) {
        this.origin = origin;
        this.target = target;
        this.vc = vc;
        this.sender = sender;
    }

    public Transition getOrigin() {
        return origin;
    }

    public Transition getTarget() {
        return target;
    }

    public VectorClock getVc() {
        return vc;
    }

    public String getSender() {
        return sender;
    }
}
