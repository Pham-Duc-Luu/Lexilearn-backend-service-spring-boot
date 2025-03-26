package com.MainBackendService.dto.kafka.debeziumConnector.mysql;

public enum MysqlOperation {
    DELETE("d"), CREATE("c"), UPDATE("u");
    private final String typeOfOperation;

    MysqlOperation(String typeOfOperation) {
        this.typeOfOperation = typeOfOperation;
    }

    public String getTypeOfOperation() {
        return typeOfOperation;
    }
}
