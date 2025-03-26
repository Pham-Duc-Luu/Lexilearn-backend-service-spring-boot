package com.MainBackendService.dto.kafka.debeziumConnector.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class KafkaMysqlMessageStructure<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @JsonProperty("schema")
    private JsonNode schema;
    @JsonProperty("payload")
    private JsonNode kafkaMysqlPayload;

    public JsonNode getSchema() {
        return schema;
    }

    public void setSchema(JsonNode schema) {
        this.schema = schema;
    }

    public JsonNode getKafkaMysqlPayload() {
        return kafkaMysqlPayload;
    }

    public void setKafkaMysqlPayload(JsonNode kafkaMysqlPayload) {
        this.kafkaMysqlPayload = kafkaMysqlPayload;
    }

    public MysqlOperation getPayloadOp() throws JsonProcessingException {
        return objectMapper.treeToValue(kafkaMysqlPayload.get("op"), MysqlOperation.class);
    }

    public JsonNode getPayloadAfter() {
        return kafkaMysqlPayload.get("after");
    }


    public JsonNode getPayloadBefore() {
        return kafkaMysqlPayload.get("before");
    }
}
