package ir.ac.ut.ece.rv.simulation;

import ir.ac.ut.ece.rv.state.GlobalState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class HistoryScenario implements Scenario {

    private Queue<String> actorQueue = new ArrayDeque<>();

    HistoryScenario() {
        String filePath = "src/main/resources/scenario.txt";
        try {
            String data = new String(Files.readAllBytes(Paths.get(filePath)));
            actorQueue.addAll(Arrays.asList(data.split("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String nextActor(GlobalState globalState) {
        return actorQueue.poll();
    }
}
