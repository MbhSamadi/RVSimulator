package ir.ac.ut.ece.rv.state.monitor;

import ir.ac.ut.ece.rv.state.*;
import ir.ac.ut.ece.rv.state.central.DeclareMessageMeta;
import ir.ac.ut.ece.rv.state.central.DeclareMessageMetaItem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static ir.ac.ut.ece.rv.state.monitor.MonitoringMsgsrvDeclaration.*;

public class MonitoringState extends LocalState {
    private BlockingQueue<MessageCall> sharedMessages;
    private List<History> history;
    private List<MMItem> mmList;
    private List<History> tempHistory;
    private List<History> vioHistory;
    private TransitionTable transitionTable;
    private ReactiveClassDeclaration associatedActor;
    private Map<TransitionClock, List<Transition>> reqTrans;
    private Map<TransitionClock, List<History>> rcvHistory;
    private Map<TransitionClock, Interval> intervals;
    private int totalReceivedAsks;
    private int totalReceivedTells;
    private int latestSize;

    public MonitoringState(ReactiveClassDeclaration associatedActor, String instanceName) {
        super(new MonitoringClassDeclaration(instanceName), new MainRebecDefinition());
        sharedMessages = new ArrayBlockingQueue<>(1000);
        history = new ArrayList<>();
        mmList = new ArrayList<>();
        tempHistory = new ArrayList<>();
        vioHistory = new ArrayList<>();
        this.associatedActor = associatedActor;
        transitionTable = new TransitionTable(associatedActor.getName());
        reqTrans = new HashMap<>();
        rcvHistory = new HashMap<>();
        totalReceivedAsks = 0;
        totalReceivedTells = 0;
        intervals = new HashMap<>();
        latestSize = 0;
    }

    public void addSharedMessage(String methodName, List<Value> argumentValues, VectorClock callerVc) {
        List<String> argumentNames = findMethod(methodName)
                .getFormalParameters()
                .stream()
                .map(FormalParameterDeclaration::getName)
                .collect(Collectors.toList());
        sharedMessages.add(
                new MessageCall(methodName, argumentNames, argumentValues, callerVc)
        );
    }

    @Override
    public boolean isExecCandidate() {
        return super.isExecCandidate() || !sharedMessages.isEmpty();
    }

    @Override
    public void updateStatements() {
        MessageCall message = messages.poll();
        if (message != null) {
            loadMethodBody(message);
        } else {
            message = sharedMessages.poll();
            loadMethodBody(message);
        }
    }

    @Override
    protected void loadMethodBody(MessageCall messageCall) {
        currentExecutingMethod = messageCall.getMethodName();
        vectorClock.update(messageCall.getVectorClock());
        MsgsrvDeclaration methodDeclaration = findMethod(messageCall.getMethodName());
        if (!(methodDeclaration instanceof MonitoringMsgsrvDeclaration))
            throw new RuntimeException("Illegal method '" + messageCall.getMethodName() + "' found in monitor");

        switch (messageCall.getMethodName()) {
            case REGULAR_MESSAGE:
                String caller = (String) ((PrimitiveValue) messageCall.getArgument("caller")).getContent();
                String callee = (String) ((PrimitiveValue) messageCall.getArgument("callee")).getContent();
                String methodName = (String) ((PrimitiveValue) messageCall.getArgument("methodName")).getContent();
                receiveRegularMessage(caller, callee, methodName, messageCall.getVectorClock());
                break;
            case MONITORING_MESSAGE:
                MonitoringMessageMeta meta = (MonitoringMessageMeta) messageCall.getArgument("meta");
                receiveMonitoringMessage(meta);

                break;
            case MAIN_METHOD:

                break;
            default:
                throw new RuntimeException("Illegal method '" + messageCall.getMethodName() + "' found in monitor");
        }

    }

    private void receiveMonitoringMessage(MonitoringMessageMeta meta) {
        VectorClock vc = meta.getVectorClock();
        Transition targetTransition = meta.getTargetTransition();
        Transition originTransition = meta.getOriginTransition();
        MonitoringMessageType type = meta.getType();

        if (type == MonitoringMessageType.TELL) {
            totalReceivedTells++;
            TransitionClock key = new TransitionClock(originTransition, vc);

            List<History> rcvHistList = rcvHistory.getOrDefault(key, new ArrayList<>());
            rcvHistList.addAll(meta.getHistory());
            rcvHistory.put(key, rcvHistList);

            List<Transition> reqTransList = reqTrans.getOrDefault(key, new ArrayList<>());
            reqTransList.remove(targetTransition);
            reqTrans.put(key, reqTransList);

            if (reqTransList.isEmpty()) {
                transitionTable.getItems().forEach(item -> {
                    item.getVio().forEach(vio -> {
                        List<History> recs = getRecs(targetTransition, rcvHistList);
                        vioHistory.addAll(recs);
                        rcvHistList.removeAll(recs);
                        rcvHistory.put(key, rcvHistList);
                    });
                });
                List<History> recs = rcvHistory.get(key);
                rcvHistory.put(key, new ArrayList<>());
                recs.forEach(rec -> evalVc(rec, originTransition, vc));
                evalHist(originTransition, vc);

                intervals.get(key).setEnd(new Date());
            }
        } else {
            totalReceivedAsks++;
            if (hasUnknownVerdict(targetTransition, vc, this.history) || hasUnhandledMsg(targetTransition, vc)) {
                mmList.add(new MMItem(originTransition, targetTransition, vc, originTransition.getCaller()));
            } else {
                List<History> recs = getRecs(targetTransition, vc, history);
                MonitoringMessageMeta newMeta = new MonitoringMessageMeta(
                        MonitoringMessageType.TELL,
                        vc,
                        originTransition,
                        targetTransition,
                        recs
                );
                GlobalState.getInstance().addMonitoringMessageCall(instanceName, currentExecutingMethod, originTransition.getCaller(), newMeta);
            }
        }
    }

    private void evalHist(Transition t, VectorClock vc) {
        if (tempHistory.stream().anyMatch(it -> it.getTransition().equals(t))) {
            List<History> recs = getRecs(t, tempHistory);
            VectorClock vc2 = recs.stream().findFirst().get().getVc2();
            for (History rec : recs) {
                vc2 = VectorClock.of(vc2, rec.getVc2());
            }
            if (tempHistory.stream().anyMatch(it -> it.getTransition().equals(t) && it.getVc().equals(vc) && it.getVerdict() == Verdict.FALSE)) {
                updateHistory(t, vc, vc2, Verdict.FALSE);
            } else {
                updateHistory(t, vc, vc2, Verdict.BOTH);
            }
            if (reachToFinalState(t) && (getVerdict(t) == Verdict.FALSE || getVerdict(t) == Verdict.BOTH)) {
                declare(getVerdict(t));
            }

            tempHistory.removeAll(recs);
        } else {
            history.removeIf(it -> it.getTransition().equals(t) && it.getVc().equals(vc) && it.getVc2() == null && it.getVerdict() == Verdict.UNKNOWN);
        }
        handleMonitorMessage(t);
    }

    private void handleMonitorMessage(Transition t) {
        List<MMItem> handleList = mmList
                .stream()
                .filter(it -> it.getTarget().equals(t))
                .filter(it -> !hasUnknownVerdict(it.getTarget(), it.getVc(), history))
                .filter(it -> !hasUnhandledMsg(it.getTarget(), it.getVc()))
                .collect(Collectors.toList());

        for (MMItem mmItem : handleList) {
            List<History> recs = getRecs(mmItem.getTarget(), mmItem.getVc(), history);
            MonitoringMessageMeta meta = new MonitoringMessageMeta(
                    MonitoringMessageType.TELL,
                    mmItem.getVc(),
                    mmItem.getOrigin(),
                    mmItem.getTarget(),
                    recs
            );
            GlobalState.getInstance().addMonitoringMessageCall(instanceName, currentExecutingMethod, mmItem.getOrigin().getCaller(), meta);
            mmList.remove(mmItem);
        }
    }

    private void declare(Verdict verdict) {
        List<DeclareMessageMetaItem> items = new ArrayList<>();
        GlobalState
                .getInstance()
                .getMonitors()
                .forEach((key, monitor) -> items.add(new DeclareMessageMetaItem(key, monitor.history.size())));

        DeclareMessageMeta meta = new DeclareMessageMeta(verdict, items);
        GlobalState
                .getInstance()
                .addDeclareMessageCall(instanceName, currentExecutingMethod, meta);
        System.out.println("declare sent to central monitor");
    }

    private Verdict getVerdict(Transition t) {
        return tempHistory
                .stream()
                .filter(it -> it.getTransition().equals(t))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No record in temp history with given 't'"))
                .getVerdict();
    }

    private boolean reachToFinalState(Transition t) {
        return transitionTable
                .getItems()
                .stream()
                .anyMatch(it -> it.getTransition().equals(t) && it.isFinal());
    }

    private void updateHistory(Transition t, VectorClock vc, VectorClock vc2, Verdict verdict) {
        List<History> pc = history
                .stream()
                .filter(it -> it.getTransition().equals(t) && it.getVc().equals(vc) && it.getVerdict() == Verdict.UNKNOWN)
                .collect(Collectors.toList());
        for (History it : pc) {
            it.setVc2(vc2);
            it.setVerdict(verdict);
        }
    }

    private void evalVc(History r, Transition t, VectorClock vc) {
        VectorClock vc1 = r.getVc();
        VectorClock vc2 = r.getVc2();
        Verdict verdict = r.getVerdict();
        Transition pre = r.getTransition();

        Verdict status = notViolation(pre, vc1, vc2);
        if (status == Verdict.TRUE) {
            if (vc1.isConcurrent(vc)) {
                tempHistory.add(new History(t, vc, VectorClock.of(vc, vc2), Verdict.BOTH));
            } else if (vc1.isLessThan(vc)) {
                if (verdict == Verdict.TRUE) {
                    tempHistory.add(new History(t, vc, VectorClock.of(vc, vc2), Verdict.FALSE));
                } else {
                    tempHistory.add(new History(t, vc, VectorClock.of(vc, vc2), verdict));
                }
            }
        } else if (status == Verdict.BOTH) {
            if (!vc.isLessThan(vc1)) {
                tempHistory.add(new History(t, vc, VectorClock.of(vc, vc2), Verdict.BOTH));
            }
        }
    }

    private Verdict notViolation(Transition pre, VectorClock vc1Pre, VectorClock vc2Pre) {
        Verdict status = Verdict.TRUE;
        List<TransitionItem> items = transitionTable
                .getItems()
                .stream()
                .filter(it -> pre.equals(it.getPre()))
                .collect(Collectors.toList());
        for (TransitionItem it : items) {
            for (Transition vio : it.getVio()) {
                List<History> recs = getRecs(vio, vioHistory);
                for (History rec : recs) {
                    VectorClock vc1Vio = rec.getVc();
                    VectorClock vc2Vio = rec.getVc2();
                    if (vc1Pre.isLessThan(vc1Vio)) {
                        return Verdict.FALSE;
                    } else if (vc1Vio.isConcurrent(vc1Pre) && vc2Pre.isLessThan(vc2Vio)) {
                        status = Verdict.BOTH;
                    }
                }
            }
        }
        return status;
    }

    private List<History> getRecs(Transition transition, List<History> list) {
        return list
                .stream()
                .filter(it -> it.getTransition().equals(transition))
                .collect(Collectors.toList());
    }

    private List<History> getRecs(Transition transition, VectorClock vc, List<History> list) {
        return list
                .stream()
                .filter(it -> it.getTransition().equals(transition) && (it.getVc().isLessThan(vc) || it.getVc().isConcurrent(vc)))
                .collect(Collectors.toList());
    }

    private boolean hasUnhandledMsg(Transition targetTransition, VectorClock vc) {
        return sharedMessages.stream().anyMatch(it -> {
            String methodName = (String) ((PrimitiveValue) it.getArgument("methodName")).getContent();
            return methodName.equals(targetTransition.getCalledMethod()) && ((it.getVectorClock().isLessThan(vc) || it.getVectorClock().isConcurrent(vc)));
        });
    }

    private boolean hasUnknownVerdict(Transition targetTransition, VectorClock vc, List<History> history) {
        return history.stream().anyMatch(it ->
                it.getVerdict() == Verdict.UNKNOWN &&
                        (it.getTransition().equals(targetTransition)) &&
                        (it.getVc().isLessThan(vc) || it.getVc().isConcurrent(vc))
        );
    }


    private void receiveRegularMessage(String caller, String callee, String methodName, VectorClock messageVC) {
        List<TransitionItem> items = transitionTable.findItemByMethodName(methodName);
        items.forEach(item -> {
            Transition origin = item.getTransition();
            TransitionClock key = new TransitionClock(origin, messageVC);
            List<Transition> reqTransitions = reqTrans.getOrDefault(key, new ArrayList<>());
            List<Transition> preTrans = new ArrayList<>();
            if (item.getPre() != null) {
                reqTransitions.add(item.getPre());
                preTrans.add(item.getPre());
            }
            reqTransitions.addAll(item.getVio());
            preTrans.addAll(item.getVio());
            reqTrans.put(key, reqTransitions);

            if (preTrans.size() > 0) {
                preTrans.forEach(target -> {
                    MonitoringMessageMeta meta = new MonitoringMessageMeta(
                            MonitoringMessageType.ASK,
                            messageVC,
                            origin,
                            target,
                            Collections.emptyList()
                    );
                    GlobalState.getInstance().addMonitoringMessageCall(instanceName, currentExecutingMethod, target.getCaller(), meta);
                    intervals.put(key, new Interval(new Date()));
                });

                if (!hasUnknownVerdict(origin, messageVC, history)) {
                    history.add(new History(origin, messageVC, null, Verdict.UNKNOWN));
                }
            } else {
                history.add(new History(origin, messageVC, messageVC, Verdict.TRUE));
                handleMonitorMessage(origin);
            }
        });
    }

    public Map<TransitionClock, Interval> getIntervals() {
        return intervals;
    }

    public int getTotalReceivedAsks() {
        return totalReceivedAsks;
    }

    public int getTotalReceivedTells() {
        return totalReceivedTells;
    }

    public List<History> getHistory() {
        return history;
    }

    public List<MMItem> getMmList() {
        return mmList;
    }

    public List<History> getTempHistory() {
        return tempHistory;
    }

    public List<History> getVioHistory() {
        return vioHistory;
    }
    public int getLatestSize(){
        return latestSize;
    }
    public void setLatestSize(int latestSize){
        this.latestSize = latestSize;
    }
}
