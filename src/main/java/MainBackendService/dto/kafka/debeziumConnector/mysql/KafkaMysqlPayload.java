package MainBackendService.dto.kafka.debeziumConnector.mysql;

import lombok.Data;

@Data
public class KafkaMysqlPayload<T> {
    private T before;
    private T after;
    private Source source;
    private MysqlOperation op;
    private String ts_ms;
    private String ts_us;
    private String ts_ns;

    public KafkaMysqlPayload(T before, T after, Source source, MysqlOperation op, String ts_ms, String ts_us, String ts_ns) {
        this.before = before;
        this.after = after;
        this.source = source;
        this.op = op;
        this.ts_ms = ts_ms;
        this.ts_us = ts_us;
        this.ts_ns = ts_ns;
    }

    public T getBefore() {
        return before;
    }

    public void setBefore(T before) {
        this.before = before;
    }

    public T getAfter() {
        return after;
    }

    public void setAfter(T after) {
        this.after = after;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public MysqlOperation getOp() {
        return op;
    }

    public void setOp(MysqlOperation op) {
        this.op = op;
    }

    public String getTs_ms() {
        return ts_ms;
    }

    public void setTs_ms(String ts_ms) {
        this.ts_ms = ts_ms;
    }

    public String getTs_us() {
        return ts_us;
    }

    public void setTs_us(String ts_us) {
        this.ts_us = ts_us;
    }

    public String getTs_ns() {
        return ts_ns;
    }

    public void setTs_ns(String ts_ns) {
        this.ts_ns = ts_ns;
    }

}
