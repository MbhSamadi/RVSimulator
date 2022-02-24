package ir.ac.ut.ece.rv.state.central;

import ir.ac.ut.ece.rv.state.Value;
import ir.ac.ut.ece.rv.state.monitor.Verdict;

import java.util.List;

public class DeclareMessageMeta extends Value {
    private Verdict verdict;
    private List<DeclareMessageMetaItem> items;

    public DeclareMessageMeta(Verdict verdict, List<DeclareMessageMetaItem> items) {
        this.verdict = verdict;
        this.items = items;
    }

    public void print() {
        System.out.println("verdict: " + verdict);
        items.forEach(item ->
                System.out.println("\t" + item.getMonitorName() + " -> history records: " + item.getHistoryCount())
        );
    }
}

