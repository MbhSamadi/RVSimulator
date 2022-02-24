package ir.ac.ut.ece.rv.state.central;

public class DeclareMessageMetaItem {
    private String monitorName;
    private Integer historyCount;

    public DeclareMessageMetaItem(String monitorName, Integer historyCount) {
        this.monitorName = monitorName;
        this.historyCount = historyCount;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public Integer getHistoryCount() {
        return historyCount;
    }
}
