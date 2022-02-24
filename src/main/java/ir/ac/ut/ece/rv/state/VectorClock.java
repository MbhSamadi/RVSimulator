package ir.ac.ut.ece.rv.state;

import ir.ac.ut.ece.rv.state.central.CentralClassDeclaration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VectorClock {
    private Map<String, Integer> instanceClock;
    private String instanceName;

    VectorClock(Set<String> instances, String instanceName) {
        instanceClock = instances
                .stream()
                .collect(Collectors.toMap(it -> it, it -> 0));
        this.instanceName = instanceName;
    }

    VectorClock(VectorClock vc) {
        instanceClock = new HashMap<>();
        vc.instanceClock.forEach((instance, clock) -> instanceClock.put(instance, clock));
        instanceName = vc.instanceName;
    }

    public static VectorClock of(VectorClock vc1, VectorClock vc2) {
        VectorClock result = new VectorClock(vc1);
        result.update(vc2);
        return result;
    }

    public void increment() {
        instanceClock.put(instanceName, instanceClock.get(instanceName) + 1);
    }

    public void update(VectorClock newVc) {
        if (newVc == null) {
            return;
        }
        instanceClock
                .keySet()
                .forEach(instanceName -> instanceClock.put(instanceName,
                        Math.max(instanceClock.get(instanceName), newVc.instanceClock.get(instanceName))
                ));
    }
    public Map<String, Integer> getInstanceClock() {
        return instanceClock;
    }

    @Override
    public String toString() {
        String clocks = instanceClock
                .keySet()
                .stream()
                .filter(instance -> !instance.equals(CentralClassDeclaration.NAME))
                .map(instance -> instance + ":" + instanceClock.get(instance))
                .collect(Collectors.joining(","));
        return instanceName + "[" + clocks + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VectorClock))
            return false;
        return toString().equals(obj.toString());
    }

    public boolean isLessThan(VectorClock vc) {
        return instanceClock
                .keySet()
                .stream()
                .allMatch(key -> instanceClock.get(key) <= vc.instanceClock.get(key));
    }

    public boolean isConcurrent(VectorClock vc) {
        boolean hasLessValue = instanceClock
                .keySet()
                .stream()
                .anyMatch(key -> instanceClock.get(key) < vc.instanceClock.get(key));
        boolean hasMoreValue = instanceClock
                .keySet()
                .stream()
                .anyMatch(key -> instanceClock.get(key) > vc.instanceClock.get(key));
        return hasLessValue && hasMoreValue;
    }

    public boolean isEqual(VectorClock vc) {
        return instanceClock
                .keySet()
                .stream()
                .allMatch(key -> instanceClock.get(key).equals(vc.instanceClock.get(key)));
    }

    public String getInstanceName() {
        return instanceName;
    }
}
