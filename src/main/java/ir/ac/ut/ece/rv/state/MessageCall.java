package ir.ac.ut.ece.rv.state;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MessageCall {
    private String methodName;
    private List<Pair<String, Value>> arguments;
    private VectorClock vectorClock;

    public MessageCall(String methodName, List<String> names, List<Value> values, VectorClock vectorClock) {
        this.methodName = methodName;
        arguments = new ArrayList<>();
        int counter = 0;
        for (String name : names) {
            arguments.add(new Pair<>(name, values.get(counter++)));
        }
        this.vectorClock = vectorClock;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Pair<String, Value>> getArguments() {
        return arguments;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Value getArgument(String argumentName) {
        return arguments
                .stream()
                .filter(pair -> pair.getKey().equals(argumentName))
                .map(Pair::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Argument " + argumentName + " not found"));
    }
}
