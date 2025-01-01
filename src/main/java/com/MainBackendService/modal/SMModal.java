package com.MainBackendService.modal;

public class SMModal {
    private String id;
    private String name;
    private Integer count;
    private Float interval;
    private Float EF;
    private String nextDay;

    public SMModal(String id, String name, Integer count, Float interval, Float EF, String nextDay) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.interval = interval;
        this.EF = EF;
        this.nextDay = nextDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Float getInterval() {
        return interval;
    }

    public void setInterval(Float interval) {
        this.interval = interval;
    }

    public Float getEF() {
        return EF;
    }

    public void setEF(Float EF) {
        this.EF = EF;
    }

    public String getNextDay() {
        return nextDay;
    }

    public void setNextDay(String nextDay) {
        this.nextDay = nextDay;
    }
}
