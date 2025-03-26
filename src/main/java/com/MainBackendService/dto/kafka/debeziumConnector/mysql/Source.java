package com.MainBackendService.dto.kafka.debeziumConnector.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {
    private String version;
    private String connector;
    private String name;

    @JsonProperty("ts_ms")
    private long timestampMs;

    @JsonProperty("ts_us")
    private long timestampUs;

    @JsonProperty("ts_ns")
    private long timestampNs;

    private boolean snapshot;
    private String db;
    private String table;

    @JsonProperty("server_id")
    private long serverId;

    private String gtid;
    private String file;
    private long pos;
    private int row;
    private long thread;
    private String query;
}
