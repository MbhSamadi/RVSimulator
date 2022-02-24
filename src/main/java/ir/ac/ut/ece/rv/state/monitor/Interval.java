package ir.ac.ut.ece.rv.state.monitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Interval {

    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Interval(Date start) {
        this.start = start;
        this.end = null;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @JsonIgnore
    public Long getDuration() {
        return end.getTime() - start.getTime();
    }
}
