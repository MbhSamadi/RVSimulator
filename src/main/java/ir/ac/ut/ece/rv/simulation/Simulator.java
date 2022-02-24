package ir.ac.ut.ece.rv.simulation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.ac.ut.ece.rv.state.DelayManager;
import ir.ac.ut.ece.rv.state.GlobalState;
import ir.ac.ut.ece.rv.state.LocalState;
import ir.ac.ut.ece.rv.state.monitor.Interval;
import ir.ac.ut.ece.rv.state.monitor.MonitoringState;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;

import java.util.Map;

public class Simulator {

    private RebecaModel rebecaModel;
    private Scheduler scheduler;
    private NextStateGenerator nextStateGenerator;
    private GlobalState state;

    private ObjectMapper mapper = new ObjectMapper();

    public Simulator(RebecaModel rebecaModel) {
        this.rebecaModel = rebecaModel;
        state = new GlobalState(rebecaModel);
        nextStateGenerator = new NextStateGenerator();
        state = nextStateGenerator.initialGlobalState(state);
        scheduler = new Scheduler();
    }

    public void run() throws JsonProcessingException {
        boolean waitForDelay = false;
        while (true) {
            String selectedActor = scheduler.select(state);
            if (selectedActor == null) {
                if (!waitForDelay) {
                    waitForDelay = true;
                    try {
                        Thread.sleep(DelayManager.MAX_DELAY);
                    } catch (Exception ignored) {
                    }
                    continue;
                } else {
                    break;
                }
            }
            waitForDelay = false;
            state = nextStateGenerator.generate(state, selectedActor);

            LocalState selectedActorState = state.getStates().get(selectedActor);
            System.out.println("instance: " + selectedActor);
            System.out.println("method:   " + state.getExecutingMethodName(selectedActor));
            System.out.println("vc:       " + selectedActorState.getCopyOfVectorClock().toString());
            if (selectedActorState instanceof MonitoringState){
                MonitoringState monitoringState = (MonitoringState) selectedActorState;
                int size = sizeof(monitoringState);
                System.out.println("size: " + size + " Bytes");
                monitoringState.setLatestSize(size);
            }
//            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(state));
            System.out.println("--------------");
        }
    }

    public GlobalState getState() {
        return state;
    }

    public void report() {
        long totalElapsedTime = 0;
        long totalMessageCount = 0;
        Map<String, MonitoringState> monitors = state.getMonitors();
        System.out.println("Final Report:");
        for (Map.Entry<String, MonitoringState> entry : monitors.entrySet()) {
            String name = entry.getKey();
            MonitoringState monitor = entry.getValue();
            System.out.println("--------------");
            System.out.println("Monitor '" + name + "'");
            System.out.println("Total received asks: " + monitor.getTotalReceivedAsks());
            System.out.println("Total received tells: " + monitor.getTotalReceivedTells());

            long monitorElapsedTime = 0;
            long monitorMessageCount = 0;
            for (Interval interval : monitor.getIntervals().values()) {
                monitorElapsedTime += interval.getDuration();
                monitorMessageCount++;
            }

            long avgElapsedTime = 0;
            if (monitorMessageCount != 0)
                avgElapsedTime = monitorElapsedTime / monitorMessageCount;
            System.out.println("Average monitor elapsed time: " + avgElapsedTime);
            System.out.println("Size: " + monitor.getLatestSize());

            totalElapsedTime += monitorElapsedTime;
            totalMessageCount += monitorMessageCount;
        }
        System.out.println("--------------");
        if (totalMessageCount != 0)
            System.out.println("Total average elapsed time: " + totalElapsedTime / totalMessageCount);
        else
            System.out.println("This actor has no messages");
    }

    private int sizeof(MonitoringState monitoringState) {
        int historySize = monitoringState.getHistory().stream().mapToInt(this::sizeOf).sum();
        int mmListSize = monitoringState.getMmList().stream().mapToInt(this::sizeOf).sum();
        int tempHistorySize = monitoringState.getTempHistory().stream().mapToInt(this::sizeOf).sum();
        int vioHistorySize = monitoringState.getVioHistory().stream().mapToInt(this::sizeOf).sum();

        return historySize + mmListSize + tempHistorySize + vioHistorySize;
    }

    private int sizeOf(Object obj) {
        final int CHAR_SIZE = 2;
        try {
            return mapper.writeValueAsString(obj).length() * CHAR_SIZE;
        } catch (JsonProcessingException e) {
            return 0;
        }
    }
}
