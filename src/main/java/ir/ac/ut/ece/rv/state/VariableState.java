package ir.ac.ut.ece.rv.state;

public class VariableState {

    private String name;
    private Value value;

    public VariableState(String name) {
        this.name = name;
        this.value = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
