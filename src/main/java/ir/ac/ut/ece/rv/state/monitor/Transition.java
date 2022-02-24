package ir.ac.ut.ece.rv.state.monitor;

public class Transition {
    private String sourceState;
    private String destState;
    private String caller;
    private String callee;
    private String calledMethod;

    public Transition(String sourceState, String destState, String caller, String callee, String calledMethod) {
        this.sourceState = sourceState;
        this.destState = destState;
        this.caller = caller;
        this.callee = callee;
        this.calledMethod = calledMethod;
    }

    public static Transition of(String s) {
        s = s.replaceAll("[()'\"]", "");

        if (s.isEmpty())
            return null;

        String[] values = s.split(",");
        String[] transitionParams = values[1].split("-");
        return new Transition(
                values[0],
                values[2],
                transitionParams[0],
                transitionParams[2],
                transitionParams[1]
        );
    }

    @Override
    public String toString() {
        return sourceState + "-" + caller + "-" + calledMethod + "-" + callee + "-" + destState;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transition))
            return false;
        return toString().equals(obj.toString());
    }

    public String getSourceState() {
        return sourceState;
    }

    public String getDestState() {
        return destState;
    }

    public String getCaller() {
        return caller;
    }

    public String getCallee() {
        return callee;
    }

    public String getCalledMethod() {
        return calledMethod;
    }
}

