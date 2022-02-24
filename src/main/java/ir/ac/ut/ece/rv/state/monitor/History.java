package ir.ac.ut.ece.rv.state.monitor;

import ir.ac.ut.ece.rv.state.VectorClock;

public class History {
    private Transition transition;
    private VectorClock vc;
    private VectorClock vc2;
    private Verdict verdict;

    public History(Transition transition, VectorClock vc, VectorClock vc2, Verdict verdict) {
        this.transition = transition;
        this.vc = vc;
        this.vc2 = vc2;
        this.verdict = verdict;
    }

    @Override
    public String toString() {
        return transition.toString() + vc.toString() + verdict.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof History))
            return false;
        return this.toString().equals(obj.toString());
    }

    public Transition getTransition() {
        return transition;
    }

    public VectorClock getVc() {
        return vc;
    }

    public VectorClock getVc2() {
        return vc2;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVc2(VectorClock vc2) {
        this.vc2 = vc2;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }
}
