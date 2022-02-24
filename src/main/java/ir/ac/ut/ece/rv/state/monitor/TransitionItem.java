package ir.ac.ut.ece.rv.state.monitor;

import java.util.List;

public class TransitionItem {
    private Transition pre;
    private Transition transition;
    private List<Transition> vio;
    private boolean isFinal;

    public TransitionItem(Transition pre, Transition transition, List<Transition> vio, boolean isFinal) {
        this.pre = pre;
        this.transition = transition;
        this.vio = vio;
        this.isFinal = isFinal;
    }

    public boolean isForMethod(String methodName) {
        return transition.getCalledMethod().equals(methodName);
    }

    public Transition getPre() {
        return pre;
    }

    public Transition getTransition() {
        return transition;
    }

    public List<Transition> getVio() {
        return vio;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
