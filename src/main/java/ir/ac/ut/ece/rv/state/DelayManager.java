package ir.ac.ut.ece.rv.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DelayManager {

    private static Integer MIN_DELAY = 200;
    public static Integer MAX_DELAY = 300;
    private static Integer ZERO_DELAY = 0;

    private Map<String, Integer> delays = new HashMap<>();

    public void sleep(String caller, String method, String callee) {
        try {
            Integer delay = getDelay(caller, method, callee);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Integer getDelay(String caller, String method, String callee) {
        if (caller.equals(callee))
            return ZERO_DELAY;
        String key = keyOf(caller, method, callee);
        if (delays.get(key) == null) {
            Integer delay = new Random().nextInt(MAX_DELAY - MIN_DELAY + 1) + MIN_DELAY;
            delays.put(key, delay);
        }
        return delays.get(key);
    }

    private String keyOf(String caller, String method, String callee) {
        return caller + "_" + method + "_" + callee;
    }

}
