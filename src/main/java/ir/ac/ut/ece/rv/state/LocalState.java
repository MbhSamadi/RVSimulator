package ir.ac.ut.ece.rv.state;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class LocalState {

    protected ReactiveClassDeclaration actor;
    protected List<VariableState> variables;
    protected List<VariableState> localVariables;
    protected List<Statement> statements; // Statements to be executed
    protected BlockingQueue<MessageCall> messages;
    protected Map<String, String> bindings;
    protected VectorClock vectorClock;
    protected String instanceName;
    protected String currentExecutingMethod;

    public LocalState(ReactiveClassDeclaration actor, MainRebecDefinition mainRebecDefinition) {
        this.actor = actor;
        variables = extractVariables();
        statements = extractConstructorStatements();
        messages = new ArrayBlockingQueue<>(1000);
        localVariables = new ArrayList<>();
        bindings = extractBindings(mainRebecDefinition);
        currentExecutingMethod = "constructor";
    }

    public void initVectorClock(Set<String> keySet, String instanceName) {
        vectorClock = new VectorClock(keySet, instanceName);
        this.instanceName = instanceName;
    }

    private Map<String, String> extractBindings(MainRebecDefinition mainRebecDefinition) {
        Map<String, String> bindings = new HashMap<>();
        List<String> knownRebecNames = extractKnownRebecNames();
        List<String> bindingNames = extractBindingNames(mainRebecDefinition);
        for (int i = 0; i < bindingNames.size(); i++) {
            bindings.put(knownRebecNames.get(i), bindingNames.get(i));
        }
        return bindings;
    }

    private List<String> extractBindingNames(MainRebecDefinition mainRebecDefinition) {
        return mainRebecDefinition
                .getBindings()
                .stream()
                .map(it -> ((TermPrimary) it).getName())
                .collect(Collectors.toList());
    }

    private List<String> extractKnownRebecNames() {
        return this.actor
                .getKnownRebecs()
                .stream()
                .flatMap(
                        fieldDeclaration -> fieldDeclaration
                                .getVariableDeclarators()
                                .stream()
                                .map(VariableDeclarator::getVariableName))
                .collect(Collectors.toList());
    }

    private List<VariableState> extractVariables() {
        return this.actor
                .getStatevars()
                .stream()
                .map(this::extractVariables)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<VariableState> extractVariables(FieldDeclaration fieldDeclaration) {
        return fieldDeclaration
                .getVariableDeclarators()
                .stream()
                .map(it -> new VariableState(it.getVariableName()))
                .collect(Collectors.toList());
    }

    private List<Statement> extractConstructorStatements() {
        ConstructorDeclaration constructorDeclaration = this.actor
                .getConstructors()
                .stream()
                .findFirst()
                .orElse(null);
        if (constructorDeclaration != null)
            return constructorDeclaration.getBlock().getStatements();
        return new ArrayList<>();
    }

    public void updateStatements() {
        MessageCall messageCall = messages.poll();
        loadMethodBody(messageCall);
    }

    protected void loadMethodBody(MessageCall messageCall) {
        currentExecutingMethod = messageCall.getMethodName();
        vectorClock.update(messageCall.getVectorClock());
        MsgsrvDeclaration methodDeclaration = findMethod(messageCall.getMethodName());
        if (methodDeclaration == null)
            return;
        messageCall.getArguments().forEach(argument -> {
            addLocalVariable(argument.getKey());
            setVariableValue(argument.getKey(), argument.getValue());
        });
        statements.addAll(methodDeclaration.getBlock().getStatements());
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setVariableValue(String variableName, Value value) {
        VariableState foundVarState = findVariableState(variableName);
        foundVarState.setValue(value);
    }

    public void setVariableValue(String leftVarName, String rightVarName) {
        VariableState leftVar = findVariableState(leftVarName);
        VariableState rightVar = findVariableState(rightVarName);
        leftVar.setValue(rightVar.getValue());
    }

    private VariableState findVariableState(String variableName) {
        return findVariableStateInList(localVariables, variableName)
                .orElseGet(() ->
                        findVariableStateInList(variables, variableName)
                                .orElseThrow(() -> new RuntimeException("Variable " + variableName + " not found"))
                );
    }

    private Optional<VariableState> findVariableStateInList(List<VariableState> list, String variableName) {
        return list
                .stream()
                .filter(varState -> varState.getName().equals(variableName))
                .findFirst();
    }

    public void addLocalVariable(String varName) {
        try {
            findVariableState(varName).setValue(null);
        } catch (RuntimeException e) {
            localVariables.add(new VariableState(varName));
        }
    }

    public Value getVariableValue(String varName) {
        return findVariableState(varName).getValue();
    }

    public void insertStatement(Statement statement) {
        statements.add(0, statement);
    }

    public void insertStatements(List<Statement> newStatements) {
        statements.addAll(0, newStatements);
    }

    public String getBinding(String calledInstance) {
        return bindings.get(calledInstance);
    }

    public void addMessageCall(String methodName, List<Value> argumentValues, VectorClock callerVc) {
        List<String> argumentNames = findMethod(methodName)
                .getFormalParameters()
                .stream()
                .map(FormalParameterDeclaration::getName)
                .collect(Collectors.toList());
        messages.add(
                new MessageCall(methodName, argumentNames, argumentValues, callerVc)
        );
    }

    protected MsgsrvDeclaration findMethod(String methodName) {
        return actor
                .getMsgsrvs()
                .stream()
                .filter(it -> it.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("method " + methodName + " not found"));
    }

    public boolean isExecCandidate() {
        return (!messages.isEmpty() || !statements.isEmpty());
    }

    public VectorClock getCopyOfVectorClock() {
        return new VectorClock(vectorClock);
    }

    public void incrementVectorClock() {
        vectorClock.increment();
    }

    public String getExecutingMethodName() {
        return currentExecutingMethod;
    }
}
